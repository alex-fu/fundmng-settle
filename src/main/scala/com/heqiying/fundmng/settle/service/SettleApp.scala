package com.heqiying.fundmng.settle.service

import akka.actor.ActorRef

class SettleApp(val settleActorRef: ActorRef)
  extends SettleService with QueryService
