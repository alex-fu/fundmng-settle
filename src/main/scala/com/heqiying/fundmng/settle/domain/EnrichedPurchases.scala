package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.model.Purchase

import scala.language.implicitConversions

class EnrichedPurchases(purchases: Seq[Purchase]) {
  def find(fundUuid: String, accountUuid: String): Option[Purchase] = {
    purchases.find(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }

  def findAll(fundUuid: String, accountUuid: String): Seq[Purchase] = {
    purchases.filter(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }
}

object EnrichedPurchases {
  implicit def enrichPurchases(purchases: Seq[Purchase]): EnrichedPurchases = new EnrichedPurchases(purchases)
}
