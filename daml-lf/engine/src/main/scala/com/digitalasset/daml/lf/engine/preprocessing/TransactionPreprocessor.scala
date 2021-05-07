// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.lf
package engine
package preprocessing

import com.daml.lf.data.{BackStack, ImmArray}
import com.daml.lf.transaction.{GenTransaction, Node, NodeId}
import com.daml.lf.value.Value
import com.daml.lf.value.Value.ContractId

private[preprocessing] final class TransactionPreprocessor(
    compiledPackages: MutableCompiledPackages
) {

  import Preprocessor._

  val commandPreprocessor = new CommandPreprocessor(compiledPackages)

  // Accumulator used by unsafeTranslateTransactionRoots method.
  private[this] case class Acc(
      globalCids: Set[ContractId],
      localCids: Set[ContractId],
      commands: BackStack[speedy.Command],
  ) {
    def update(
        newInputCids: Iterable[ContractId],
        newLocalCids: Iterable[ContractId],
        cmd: speedy.Command,
    ) = Acc(
      globalCids ++ newInputCids.filterNot(localCids),
      localCids ++ newLocalCids,
      commands :+ cmd,
    )
  }

  /*
   * Translates a transaction tree into a sequence of Speedy commands
   * and collects the global contract IDs.
   * A contract ID `cid` is considered *local* w.r.t. a node `n`, if
   * either:
   *  - it is local in any node appearing previously (w.r.t. traversal
   *    order) in the transaction, or
   *  - `n` is a create node such that `n.coid == cid`
   *
   * A contract ID `cid` is considered *global* in a root node `n`,
   * if:
   *  - `cid` is not considered local w.r.t. `n`, and
   *  - if `cid` is reference in the input fields of a `n`, i.e. :
   *    - `n` is a create node and `cid` appears in the payload of the
   *      create contract (`n.arg`), or
   *    - `n` is an exercise node and `cid` is the ID of the exercise
   *      contract (`n.targetCoid`), or
   *    - `n` is an exercise node and `cid` appears in the exercise
   *      argument (`n.choosenValue`).
   *
   * A contract ID is considered *global* w.r.t. a transaction `tx` if
   * it is global w.r.t. one of the roots of `tx`.
   *
   * Note that it is, in general, not possible to recover from a
   * transaction, the original sequence of commands that generated this
   * transaction. In particular:
   *  - we cannot distinguish a exercise performed "by ID" from an
   *    exercise performed "by key" (as of LF v1.13).
   *  - we cannot distinguish a createAndExercise from a create
   *    followed by an exercise.
   *
   * Consequently the sequence of commands and the set of global
   * contract IDs generated by this method may be different from the
   * original sequence of commands. In particular:
   * - all exercises are translated into exercise by ID.
   * - a cid is not considered global if there exists a create node
   *   within the transaction that creates a contract with the same ID.
   *
   * Under the assumption that the underlying ledger guarantees the
   * uniqueness of all contract IDs (including transient contracts),
   * the reinterpretation of the generated transaction will succeed
   * iff the original submission was valid and succeeded.
   *
   * See review comments in https://github.com/digital-asset/daml/pull/9370
   * for more details.
   */
  @throws[PreprocessorException]
  def unsafeTranslateTransactionRoots[Cid <: Value.ContractId](
      tx: GenTransaction[NodeId, Cid]
  ): (ImmArray[speedy.Command], Set[ContractId]) = {

    val result = tx.roots.foldLeft(Acc(Set.empty, Set.empty, BackStack.empty)) { (acc, id) =>
      tx.nodes.get(id) match {
        case Some(node: Node.GenActionNode[_, Cid]) =>
          node match {
            case create: Node.NodeCreate[Cid] =>
              val (cmd, newCids) =
                commandPreprocessor.unsafePreprocessCreate(create.templateId, create.arg)
              acc.update(newCids, List(create.coid), cmd)
            case exe: Node.NodeExercises[_, Cid] =>
              val (cmd, newCids) = commandPreprocessor.unsafePreprocessExercise(
                exe.templateId,
                exe.targetCoid,
                exe.choiceId,
                exe.chosenValue,
              )
              val newLocalCids = GenTransaction(tx.nodes, ImmArray(id)).localContracts.keys
              acc.update(newCids, newLocalCids, cmd)
            case _: Node.NodeFetch[_] =>
              fail(s"Transaction contains a fetch root node $id")
            case _: Node.NodeLookupByKey[_] =>
              fail(s"Transaction contains a lookup by key root node $id")
          }
        case Some(_: Node.NodeRollback[NodeId]) =>
          fail(s"invalid transaction, root refers to a rollback node $id")
        case None =>
          fail(s"invalid transaction, root refers to non-existing node $id")
      }
    }

    // The following check ensures that `localCids ∩ globalCids = ∅`.
    // It is probably not 100% necessary, as the reinterpretation should catch the cases where it is not true.
    // We still prefer to perform the check here as:
    //  - it is cheap,
    //  - it catches obviously buggy transaction,
    //  - it is easier to reason about "soundness" of preprocessing under the disjointness assumption.
    if (result.localCids exists result.globalCids)
      fail("Conflicting discriminators between a global and local contract ID.")

    result.commands.toImmArray -> result.globalCids
  }

}
