// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.api.testtool.suites

import com.daml.ledger.api.testtool.infrastructure.Allocation._
import com.daml.ledger.api.testtool.infrastructure.Assertions._
import com.daml.ledger.api.testtool.infrastructure.LedgerTestSuite
import com.daml.ledger.test.package_management.PackageManagementTest.PackageManagementTestTemplate
import com.daml.ledger.test.package_management.PackageManagementTest.PackageManagementTestTemplate._
import com.google.protobuf.ByteString
import io.grpc.Status

import scala.collection.compat._
import scala.concurrent.{ExecutionContext, Future}

final class PackageManagementServiceIT extends LedgerTestSuite {
  private[this] val testPackageResourcePath =
    com.daml.ledger.test.TestDars.fileNames("package_management")

  private def loadTestPackage()(implicit ec: ExecutionContext): Future[ByteString] = {
    val testPackage = Future {
      val in = getClass.getClassLoader.getResourceAsStream(testPackageResourcePath)
      assert(in != null, s"Unable to load test package resource at '$testPackageResourcePath'")
      in
    }
    val bytes = testPackage.map(ByteString.readFrom)
    bytes.onComplete(_ => testPackage.map(_.close()))
    bytes
  }

  test(
    "PackageManagementEmptyUpload",
    "An attempt at uploading an empty payload should fail",
    allocate(NoParties),
  )(implicit ec => { case Participants(Participant(ledger)) =>
    for {
      failure <- ledger.uploadDarFile(ByteString.EMPTY).mustFail("uploading an empty package")
    } yield {
      assertGrpcError(
        failure,
        Status.Code.INVALID_ARGUMENT,
        "Invalid argument: Invalid DAR: package-upload",
      )
    }
  })

  test(
    "PackageManagementLoad",
    "Concurrent uploads of the same package should be idempotent and result in the package being available for use",
    allocate(SingleParty),
  )(implicit ec => { case Participants(Participant(ledger, party)) =>
    for {
      testPackage <- loadTestPackage()
      _ <- Future.sequence(Vector.fill(8)(ledger.uploadDarFile(testPackage)))
      knownPackages <- ledger.listKnownPackages()
      contract <- ledger.create(party, new PackageManagementTestTemplate(party))
      acsBefore <- ledger.activeContracts(party)
      _ <- ledger.exercise(party, contract.exerciseTestChoice)
      acsAfter <- ledger.activeContracts(party)
    } yield {
      val duplicatePackageIds =
        knownPackages.groupBy(_.packageId).view.mapValues(_.size).filter(_._2 > 1).toMap
      assert(
        duplicatePackageIds.isEmpty,
        s"There are duplicate package identifiers: ${duplicatePackageIds
          .map { case (name, count) => s"$name ($count)" }
          .mkString(", ")}",
      )
      assert(
        acsBefore.size == 1,
        "After the contract has been created there should be one active contract but there's none",
      )
      assert(
        acsAfter.isEmpty,
        s"There should be no active package after the contract has been consumed: ${acsAfter.map(_.contractId).mkString(", ")}",
      )
    }
  })
}
