package com.heqiying.fundmng.settle.domain

import scala.language.implicitConversions
import com.heqiying.fundmng.settle.model.Share

class EnrichedShares(shares: Seq[Share]) {
  def find(fundUuid: String, accountUuid: String): Option[Share] = {
    shares.find(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }
}

object EnrichedShares {
  implicit def enrichShares(shares: Seq[Share]): EnrichedShares = new EnrichedShares(shares)
}
