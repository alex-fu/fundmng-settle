package com.heqiying.fundmng.settle.interface

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.heqiying.fundmng.settle.common.{ AppConfig, LazyLogging }
import com.heqiying.fundmng.settle.model.Accesser
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

trait ActiHelper extends LazyLogging {
  implicit val system: ActorSystem
  implicit val mat: ActorMaterializer
  implicit val ec = system.dispatcher
  def debugResponse(relativeUri: String, resp: Future[HttpResponse]) = {
    resp onComplete {
      case Success(r) =>
        logger.debug(s"Response from activiti-rest $relativeUri(${r.status}):")
        r.entity.toStrict(10.seconds).map { strict =>
          logger.debug(s"\n${strict.data.utf8String.parseJson.prettyPrint}")
        }
      case Failure(e) =>
        logger.error(s"access to $relativeUri failed: $e")
    }
  }
}

object ActiIdentityRPC {
  private def default(implicit system: ActorSystem, mat: ActorMaterializer) = new ActiIdentityRPC(
    new ActivitiInterface(AppConfig.app.activiti.dummyUser, Some(AppConfig.app.activiti.dummyPassword))
  )
  def create(accesser: Option[Accesser])(implicit system: ActorSystem, mat: ActorMaterializer): ActiIdentityRPC = {
    accesser.
      map(x => new ActivitiInterface(x.loginName)).
      map(new ActiIdentityRPC(_)).
      getOrElse(default)
  }
}

class ActiIdentityRPC(val acti: ActivitiInterface)(implicit val system: ActorSystem, val mat: ActorMaterializer) extends ActiHelper {
}