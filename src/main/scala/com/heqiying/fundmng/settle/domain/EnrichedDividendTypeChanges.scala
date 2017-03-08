package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.model.DividendTypeChange

import scala.language.implicitConversions

class EnrichedDividendTypeChanges(dtcs: Seq[DividendTypeChange]) {
  def find(fundUuid: String, accountUuid: String): Option[DividendTypeChange] = {
    dtcs.find(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }

  def findAll(fundUuid: String, accountUuid: String): Seq[DividendTypeChange] = {
    dtcs.filter(x => x.fundUuid == fundUuid && x.accountUuid == accountUuid)
  }
}

object EnrichedDividendTypeChanges {
  implicit def enrichDividendTypeChanges(dtcs: Seq[DividendTypeChange]): EnrichedDividendTypeChanges = new EnrichedDividendTypeChanges(dtcs)
}
