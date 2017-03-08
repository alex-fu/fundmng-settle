package com.heqiying.fundmng.settle.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{ DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat }

case class AppError(
    code: Int = 9999,
    message: String = "interval server error",
    detail: String = ""
) extends Exception {
  override def toString: String = s"""AppError($code, $message, $detail)"""
}

object AppErrors {
  // Common Settle Errors (1000 ~ 1099)
  def AlreadyInSettling(detail: String = "") = AppError(1000, "Already in settling, reject", detail)
  def InvalidSettleParam(detail: String = "") = AppError(1001, "Invalid SettleParam type", detail)
  def GenNewSettleFailed(detail: String = "") = AppError(1002, "Generate new settle failed", detail)
  def UnknownSettleError(detail: String = "") = AppError(1003, "Unknown settle error", detail)
  def CannotDropSettleError(detail: String = "") = AppError(1004, "Cannot drop settle", detail)
  def CannotConfirmSettleError(detail: String = "") = AppError(1005, "Cannot confirm settle", detail)
  def AlreadySettled(detail: String = "") = AppError(1006, "Already settled", detail)

  // OpenDay Settle Errors (1100 ~ 1199)
  def InvalidFund(detail: String = "") = AppError(1100, "Invalid fund", detail)
  def InvalidOpenDay(detail: String = "") = AppError(1101, "Invalid openday", detail)
  def NavNotExist(detail: String = "") = AppError(1102, "Net asset value not found", detail)
  def InvalidTrades(detail: String = "") = AppError(1103, "Invalid trades", detail)
}

object AppErrorJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  //  implicit val appErrorJsonSupport = jsonFormat3(AppError.apply)

  implicit object AppErrorStatsJsonFormat extends RootJsonFormat[AppError] {
    def write(e: AppError) = {
      JsObject(
        "error" -> JsObject(
          "code" -> JsNumber(e.code),
          "message" -> JsString(e.message),
          "detail" -> JsString(e.detail)
        )
      )
    }
    def read(j: JsValue) = ???
  }
}

