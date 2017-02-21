package com.heqiying.fundmng.settle.interface

import java.time.LocalDateTime

import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server.directives.{ DebuggingDirectives, LogEntry }
import akka.http.scaladsl.server.{ Route, RouteResult }
import com.heqiying.fundmng.settle.directives.UsefulDirective._
import com.heqiying.fundmng.settle.model.Accesser

object HttpLoggerInterface {
  val logRoute = { route: Route =>
    (extractAccesser & extractUri & extractMethod & extractClientIP) { (accesser, uri, method, clientip) =>
      DebuggingDirectives.logRequestResult(showLogs(uri, method, clientip, accesser) _) {
        DebuggingDirectives.logRequestResult(("Request Response", Logging.InfoLevel)) {
          route
        }
      }
    }
  }

  private[this] def showLogs(uri: Uri, method: HttpMethod, clientip: RemoteAddress, accesser: Option[Accesser])(request: HttpRequest): RouteResult => Option[LogEntry] = {
    case Complete(response: HttpResponse) =>
      Some(LogEntry(
        s"request completed. status = ${response.status}, method = ${request.method}, path = ${request.uri}, " +
          s"response_headers = ${response.headers}, " +
          s"response length = ${response.entity.contentLengthOption.getOrElse(0L)} bytes",
        Logging.InfoLevel
      ))

    case _ =>
      Some(LogEntry(
        s"request missed. method = ${request.method}, path = ${request.uri}",
        Logging.WarningLevel
      ))
  }
}
