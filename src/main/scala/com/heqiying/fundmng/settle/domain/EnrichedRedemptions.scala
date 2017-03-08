package com.heqiying.fundmng.settle.domain

import scala.language.implicitConversions
import com.heqiying.fundmng.settle.model.Redemption

class EnrichedRedemptions(redems: Seq[Redemption]) {
  def find(fundUuid: String, accountUuid: String): Option[Redemption] = {
    redems.find(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }

  def findAll(fundUuid: String, accountUuid: String): Seq[Redemption] = {
    redems.filter(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }
}

object EnrichedRedemptions {
  implicit def enrichRedemptions(redems: Seq[Redemption]): EnrichedRedemptions = new EnrichedRedemptions(redems)
}
