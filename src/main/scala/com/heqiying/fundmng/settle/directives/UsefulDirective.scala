package com.heqiying.fundmng.settle.directives

import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.heqiying.fundmng.settle.model.{ Accesser, GroupType }

object UsefulDirective {
  def extractAccesser: Directive1[Option[Accesser]] = {
    for {
      loginName <- optionalHeaderValueByName("X-AccesserLoginName")
      name <- optionalHeaderValueByName("X-AccesserName")
      email <- optionalHeaderValueByName("X-AccesserEmail")
      wxid <- optionalHeaderValueByName("X-AccesserWxID")
      atype <- optionalHeaderValueByName("X-AccesserType")
    } yield {
      if (loginName.nonEmpty && atype.nonEmpty && GroupType.types.contains(atype.get))
        Some(Accesser(loginName.get, name, email, wxid, atype.get))
      else
        None
    }
  }

  val rejectDirective: Directive0 = Directive(_ => reject)
  val forbiddenRoute: StandardRoute = complete(HttpResponse(StatusCodes.Forbidden, entity = "Forbidden to access!"))
  val notFoundRoute: StandardRoute = complete(HttpResponse(StatusCodes.NotFound))
}
