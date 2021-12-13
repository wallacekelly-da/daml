package com.daml.error.definitions

import com.daml.error.utils.ErrorDetails
import com.daml.error.{DamlContextualizedErrorLogger, ErrorCategory, ErrorClass, ErrorCode}
import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.google.protobuf.any
import com.google.rpc.status.Status
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object FooError extends ErrorCode("FOO_ERROR", ErrorCategory.InvalidGivenCurrentSystemStateOther)(ErrorClass.root())


class TransactionErrorSpec extends AnyFlatSpec with Matchers{

  it should "do it" in {


    val tested: TransactionError = new TransactionError {
      /** The error code, usually passed in as implicit where the error class is defined */
      override def code: ErrorCode = FooError

      /** A human readable string indicating the error */
      override def cause: String = "This is a cause"
    }

    val d = new DamlContextualizedErrorLogger(
      ContextualizedLogger.get(getClass),
      LoggingContext.ForTesting,
      None
    )

    val status: Status = tested.rpcStatus()(d)
    val status_simple: Status = tested.rpcStatus_simple(d)
// Status(val code : scala.Int = { /* compiled code */ }, val message : scala.Predef.String = { /* compiled code */ }, val details : scala.Seq[com.google.protobuf.any.Any] = { /* compiled code */ }, val unknownFields : scalapb.UnknownFieldSet = { /* compiled code */ }) extends scala.AnyRef with scalapb.GeneratedMessage with scalapb.lenses.Updatable[com.google.rpc.status.Status] with scala.Product with scala.Serializable {
    //
    status.code shouldBe 9
    status.message shouldBe "FOO_ERROR(9,0): This is a cause; category=9, location=Some(TransactionErrorSpec.scala:17)"
    val details: Seq[any.Any] = status.details
    ErrorDetails.from(details) shouldBe Seq.empty
    status.unknownFields shouldBe ""


    status.code shouldBe
    status.code shouldBe status_simple.code
    status.message shouldBe status_simple.message
    status.details shouldBe status_simple.details
    status.unknownFields shouldBe status_simple.unknownFields
    // Status(9,FOO_ERROR(9,0): This is a cause; category=9, location=Some(TransactionErrorSpec.scala:17),List(Any(type.googleapis.com/google.rpc.ErrorInfo,<ByteString@314b8f2d size=101 contents="\n\tFOO_ERROR\032/\n\blocation\022#Some(TransactionErrorS...">,UnknownFieldSet(Map()))),UnknownFieldSet(Map()))
    // Status(9,FOO_ERROR(9,0): This is a cause,List(Any(type.googleapis.com/google.rpc.ErrorInfo,<ByteString@6fa590ba size=101 contents="\n\tFOO_ERROR\032/\n\blocation\022#Some(TransactionErrorS...">,UnknownFieldSet(Map()))),UnknownFieldSet(Map()))

  }

}
