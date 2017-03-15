import java.sql.Date
import java.util.UUID

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.util.FastFuture
import akka.stream.ActorMaterializer
import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model.{ DBSchema, _ }
import com.heqiying.fundmng.settle.service.{ SettleActor, SettleApp }
import org.scalatest.{ FlatSpec, Matchers }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object InitData {

  import scala.concurrent.ExecutionContext.Implicits.global

  def genUuid = UUID.randomUUID().toString

  //  val testFundUuid = "e38f6b5f-7bb1-4feb-9e01-ec1ea3406e26"
  val testFundUuid = genUuid
  println(s"testFundUuid: $testFundUuid")
  val testFund = "S-TestFund"
  val testAccountUuid = genUuid
  println(s"testAccountUuid: $testAccountUuid")
  val testAccount = "12345678"

  val testAccount2Uuid = genUuid
  println(s"testAccount2Uuid: $testAccount2Uuid")
  val testAccount2 = "87654321"

  val testAccount3Uuid = genUuid
  println(s"testAccount3Uuid: $testAccount3Uuid")
  val testAccount3 = "88888888"

  val openDate1 = "2017-03-31"
  val openDate1BeforeRewardNav = 1.0
  val openDate1Nav = 1.0
  val openDate1AccuNav = 1.0

  val openDate2_0 = "2017-06-29" // fake openday
  val openDate2_0BeforeRewardNav = 1.49
  val openDate2_0Nav = 1.49
  val openDate2_0AccuNav = 1.49

  val openDate2 = "2017-06-30"
  val openDate2BeforeRewardNav = 1.5
  val openDate2Nav = 1.5
  val openDate2AccuNav = 1.5

  def apply() = {
    clearAll()
    initBaseData()
  }

  def clearAll() = {
    val schemaSqls = Seq(
      DBSchema.funds.delete,
      DBSchema.fundNavs.delete,
      DBSchema.investors.delete,
      DBSchema.purchases.delete,
      DBSchema.redemptions.delete,
      DBSchema.dividendTypeChanges.delete,
      DBSchema.dividends.delete,
      DBSchema.settles.delete,
      DBSchema.trades.delete,
      DBSchema.tradeSummaries.delete,
      DBSchema.shares.delete,
      DBSchema.investStatements.delete,
      DBSchema.remits.delete,
      DBSchema.appendants.delete
    )

    Await.result(schemaSqls.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
  }

  def initBaseData() = {
    val initData = Seq(
      // init Fund
      DBSchema.funds += Fund(None, testFund, "TestFund", Some("TF"),
        None, Some(Date.valueOf("2016-01-01")), None, Some(0), Some(0),
        Some(0), Some(0), Some(4), Some(4), Some(4),
        Funds.RaisingFundState, Some("zh"), Some("zy"),
        testFundUuid, 1, Funds.ReviewedReviewState, Funds.Alive),

      // init Investor
      DBSchema.investors += Investor(None, testAccount, "fuyf", "13888888888", None, None,
        None, Investors.PersonType, Investors.InvestedState, Investors.Active,
        testAccountUuid, 1, Investors.ReviewedReviewState, Investors.Alive),
      DBSchema.investors += Investor(None, testAccount2, "fuyf2", "13222222222", None, None,
        None, Investors.PersonType, Investors.InvestedState, Investors.Active,
        testAccount2Uuid, 1, Investors.ReviewedReviewState, Investors.Alive),
      DBSchema.investors += Investor(None, testAccount3, "fuyf3", "13211111111", None, None,
        None, Investors.PersonType, Investors.InvestedState, Investors.Active,
        testAccount3Uuid, 1, Investors.ReviewedReviewState, Investors.Alive)
    )

    Await.result(initData.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
  }

  def initTradesOnOpenDate1() = {
    val initData = Seq(
      // init FundNav
      DBSchema.fundNavs += FundNav(None, testFundUuid, testFund, Date.valueOf(openDate1),
        openDate1BeforeRewardNav, openDate1Nav, openDate1AccuNav),

      // init Purchase
      DBSchema.purchases += Purchase(None, testAccountUuid, testAccount,
        testFundUuid, testFund, Date.valueOf(openDate1),
        Purchases.SubscribeType, Date.valueOf("2017-03-20"),
        None, None, Some(1000000), None),
      DBSchema.purchases += Purchase(None, testAccount2Uuid, testAccount2,
        testFundUuid, testFund, Date.valueOf(openDate1),
        Purchases.SubscribeType, Date.valueOf("2017-03-23"),
        None, None, Some(5000000), None)

    // init Redemption

    // init DividendTypeChange

    // init Dividend

    )

    Await.result(initData.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
  }

  def initTradesOnOpenDate2() = {
    val initData = Seq(
      // init FundNav
      DBSchema.fundNavs += FundNav(None, testFundUuid, testFund, Date.valueOf(openDate2_0),
        openDate2_0BeforeRewardNav, openDate2_0Nav, openDate2_0AccuNav),
      DBSchema.fundNavs += FundNav(None, testFundUuid, testFund, Date.valueOf(openDate2),
        openDate2BeforeRewardNav, openDate2Nav, openDate2AccuNav),

      // init Purchase
      DBSchema.purchases += Purchase(None, testAccount3Uuid, testAccount3,
        testFundUuid, testFund, Date.valueOf(openDate2),
        Purchases.SubscribeType, Date.valueOf("2017-06-20"),
        None, None, Some(1000000), None),

      // init Redemption
      DBSchema.redemptions += Redemption(None, testAccount2Uuid, testAccount2,
        testFundUuid, testFund, Date.valueOf(openDate2), Date.valueOf("2017-06-23"),
        None, None, Some(3000000), None),

      // init DividendTypeChange
      DBSchema.dividendTypeChanges += DividendTypeChange(None, testAccountUuid, testAccount, testFundUuid,
        testFund, Date.valueOf(openDate2), Date.valueOf("2017-06-24"), DividendTypeChanges.CashDividendType, 0, None),
      DBSchema.dividendTypeChanges += DividendTypeChange(None, testAccount2Uuid, testAccount2, testFundUuid,
        testFund, Date.valueOf(openDate2), Date.valueOf("2017-06-25"), DividendTypeChanges.ReinvestDividendType, 0, None),

      // init Dividend
      DBSchema.dividends += Dividend(None, testFundUuid, testFund, Date.valueOf(openDate2),
        Date.valueOf("2017-06-21"), 0.5, None)
    )

    Await.result(initData.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
  }

}

class OpendaySettleSpecTest extends FlatSpec with Matchers {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  // initialize singleton settle actor
  val settleActorRef = system.actorOf(Props.apply[SettleActor])
  val settleApp = new SettleApp(settleActorRef)

  InitData()
  val fundUuid = InitData.testFundUuid
  var settle1Uuid: String = _
  var settle2Uuid: String = _

  "An openday settle" should "made the right shares, trades, tradeSummaries, statements and settle state" in {
    InitData.initTradesOnOpenDate1()

    val openDate = InitData.openDate1
    val f0 = settleApp.opendaySettle(fundUuid, openDate)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"openday settle failed on fund $fundUuid, openday $openDate")
    }
    settle1Uuid = Await.result(f0, Duration.Inf)
    println(s"settle done! settleUuid on $openDate is $settle1Uuid")

    // query settle results
    val f1 = for {
      shares <- settleApp.getShares(settle1Uuid)
      trades <- settleApp.getTrades(settle1Uuid)
      tradeSummaries <- settleApp.getTradeSummaries(settle1Uuid)
      statements <- settleApp.getInvestStatements(settle1Uuid)
      remits <- settleApp.getRemits(settle1Uuid)
      settles <- settleApp.getSettles(fundUuid)
    } yield {
      (shares, trades, tradeSummaries, statements, remits, settles)
    }
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid $settle1Uuid")
    }

    val (shares, trades, tradeSummaries, statements, remits, settles) = Await.result(f1, Duration.Inf)
    shares.length shouldEqual 2
    val sharesSel = shares.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.share, x.amount))
    sharesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle1Uuid, 1000000, 1000000)
    sharesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle1Uuid, 5000000, 5000000)

    trades.length shouldEqual 2
    val tradesSel = trades.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.tradeType,
      x.preShare, x.share, x.tradeNav, x.dividendPerShare, x.purchaseAmount, x.redemptionAmount,
      x.cashDividendAmount, x.reinvestShare, x.fee))
    tradesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle1Uuid, Trades.PurchaseTradeType,
      0, 1000000, 1, 0, 1000000, 0, 0, 0, 0)
    tradesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle1Uuid, Trades.PurchaseTradeType,
      0, 5000000, 1, 0, 5000000, 0, 0, 0, 0)

    tradeSummaries.length shouldEqual 1
    val tradeSummariesSel = tradeSummaries.map(x => (x.fundUuid, x.settleUuid, x.tradeType,
      x.tradeNav, x.redemptionAmount, x.redemptionShare, x.purchaseAmount, x.purchaseShare,
      x.dividendPerShare, x.cashDividendAmount, x.reinvestShare,
      x.shareChange, x.remitChange, x.fee))
    tradeSummariesSel should contain(InitData.testFundUuid, settle1Uuid, Trades.PurchaseTradeType,
      1, 0, 0, 6000000, 6000000, 0, 0, 0, 0, 0, 0)

    remits.length shouldEqual 0

    settles.length shouldEqual 1
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual Some(settle1Uuid, InitData.testFundUuid, Settles.OpendaySettleType, InitData.openDate1, Settles.SettledState)
  }

  it should "be dropped when drop this settle" in {
    val f0 = settleApp.dropSettle(fundUuid, settle1Uuid)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"drop settle failed on fund $fundUuid, settleUuid: $settle1Uuid")
    }
    val uuid = Await.result(f0, Duration.Inf)
    uuid shouldEqual settle1Uuid

    println(s"drop settle done!")

    // query settle state
    val f1 = for {
      settles <- settleApp.getSettles(fundUuid)
    } yield settles
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid: $settle1Uuid")
    }

    val settles = Await.result(f1, Duration.Inf)
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual None
  }

  it should "be settle again" in {
    val openDate = InitData.openDate1
    val f0 = settleApp.opendaySettle(fundUuid, openDate)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"openday settle failed on fund $fundUuid, openday $openDate")
    }
    settle1Uuid = Await.result(f0, Duration.Inf)
    println(s"settle done! settleUuid on $openDate is $settle1Uuid")

    // query settle results
    val f1 = for {
      shares <- settleApp.getShares(settle1Uuid)
      trades <- settleApp.getTrades(settle1Uuid)
      tradeSummaries <- settleApp.getTradeSummaries(settle1Uuid)
      statements <- settleApp.getInvestStatements(settle1Uuid)
      remits <- settleApp.getRemits(settle1Uuid)
      settles <- settleApp.getSettles(fundUuid)
    } yield {
      (shares, trades, tradeSummaries, statements, remits, settles)
    }
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid $settle1Uuid")
    }

    val (shares, trades, tradeSummaries, statements, remits, settles) = Await.result(f1, Duration.Inf)
    shares.length shouldEqual 2
    val sharesSel = shares.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.share, x.amount))
    sharesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle1Uuid, 1000000, 1000000)
    sharesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle1Uuid, 5000000, 5000000)

    trades.length shouldEqual 2
    val tradesSel = trades.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.tradeType,
      x.preShare, x.share, x.tradeNav, x.dividendPerShare, x.purchaseAmount, x.redemptionAmount,
      x.cashDividendAmount, x.reinvestShare, x.fee))
    tradesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle1Uuid, Trades.PurchaseTradeType,
      0, 1000000, 1, 0, 1000000, 0, 0, 0, 0)
    tradesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle1Uuid, Trades.PurchaseTradeType,
      0, 5000000, 1, 0, 5000000, 0, 0, 0, 0)

    tradeSummaries.length shouldEqual 1
    val tradeSummariesSel = tradeSummaries.map(x => (x.fundUuid, x.settleUuid, x.tradeType,
      x.tradeNav, x.redemptionAmount, x.redemptionShare, x.purchaseAmount, x.purchaseShare,
      x.dividendPerShare, x.cashDividendAmount, x.reinvestShare,
      x.shareChange, x.remitChange, x.fee))
    tradeSummariesSel should contain(InitData.testFundUuid, settle1Uuid, Trades.PurchaseTradeType,
      1, 0, 0, 6000000, 6000000, 0, 0, 0, 0, 0, 0)

    remits.length shouldEqual 0

    settles.length shouldEqual 1
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual Some(settle1Uuid, InitData.testFundUuid, Settles.OpendaySettleType, InitData.openDate1, Settles.SettledState)
  }

  it should "be confirmed when confirm this settle" in {
    val f0 = settleApp.confirmSettle(fundUuid, settle1Uuid)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"confirm settle failed on fund $fundUuid, settleUuid: $settle1Uuid")
    }
    val uuid = Await.result(f0, Duration.Inf)
    uuid shouldEqual settle1Uuid

    println(s"confirm settle done!")

    // query settle state
    val f1 = for {
      settles <- settleApp.getSettles(fundUuid)
    } yield settles
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid: $settle1Uuid")
    }

    val settles = Await.result(f1, Duration.Inf)
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual Some(settle1Uuid, InitData.testFundUuid, Settles.OpendaySettleType, InitData.openDate1, Settles.ConfirmedState)
  }

  ////////////////////////////////////////////////////////////

  "An second openday settle" should "made the right things again" in {
    InitData.initTradesOnOpenDate2()

    val openDate = InitData.openDate2
    val f0 = settleApp.opendaySettle(fundUuid, openDate)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"openday settle failed on fund $fundUuid, openday $openDate")
    }
    settle2Uuid = Await.result(f0, Duration.Inf)
    println(s"settle done! settleUuid on $openDate is $settle2Uuid")

    // query settle results
    val f1 = for {
      shares <- settleApp.getShares(settle2Uuid)
      trades <- settleApp.getTrades(settle2Uuid)
      tradeSummaries <- settleApp.getTradeSummaries(settle2Uuid)
      statements <- settleApp.getInvestStatements(settle2Uuid)
      remits <- settleApp.getRemits(settle2Uuid)
      settles <- settleApp.getSettles(fundUuid)
    } yield {
      (shares, trades, tradeSummaries, statements, remits, settles)
    }
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid $settle2Uuid")
    }

    val (shares, trades, tradeSummaries, statements, remits, settles) = Await.result(f1, Duration.Inf)
    shares.length shouldEqual 3
    val sharesSel = shares.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.share, x.amount))
    sharesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle2Uuid, 1000000, 1000000)
    sharesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle2Uuid, 4500000, 4500000)
    sharesSel should contain(InitData.testFundUuid, InitData.testAccount3Uuid, settle2Uuid, 1000000, 1000000)

    trades.length shouldEqual 4
    val tradesSel = trades.map(x => (x.fundUuid, x.accountUuid, x.settleUuid, x.tradeType,
      x.preShare, x.share, x.tradeNav, x.dividendPerShare, x.purchaseAmount, x.redemptionAmount,
      x.cashDividendAmount, x.reinvestShare, x.fee))
    tradesSel should contain(InitData.testFundUuid, InitData.testAccountUuid, settle2Uuid, Trades.DividendTradeType,
      1000000, 1000000, 1, 0.5, 0, 0, 500000, 0, 0)
    tradesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle2Uuid, Trades.RedemptionTradeType,
      5000000, 3000000, 1.5, 0, 0, 3000000, 0, 0, 0)
    tradesSel should contain(InitData.testFundUuid, InitData.testAccount2Uuid, settle2Uuid, Trades.DividendTradeType,
      3000000, 4500000, 1, 0.5, 0, 0, 0, 1500000, 0)
    tradesSel should contain(InitData.testFundUuid, InitData.testAccount3Uuid, settle2Uuid, Trades.PurchaseTradeType,
      0, 1000000, 1, 0, 1000000, 0, 0, 0, 0)

    tradeSummaries.length shouldEqual 3
    val tradeSummariesSel = tradeSummaries.map(x => (x.fundUuid, x.settleUuid, x.tradeType,
      x.tradeNav, x.redemptionAmount, x.redemptionShare, x.purchaseAmount, x.purchaseShare,
      x.dividendPerShare, x.cashDividendAmount, x.reinvestShare,
      x.shareChange, x.remitChange, x.fee))
    tradeSummariesSel should contain(InitData.testFundUuid, settle2Uuid, Trades.PurchaseTradeType,
      1, 0, 0, 1000000, 1000000, 0, 0, 0, 0, 0, 0)
    tradeSummariesSel should contain(InitData.testFundUuid, settle2Uuid, Trades.RedemptionTradeType,
      1.5, 3000000, 2000000, 0, 0, 0, 0, 0, 0, 0, 0)
    tradeSummariesSel should contain(InitData.testFundUuid, settle2Uuid, Trades.DividendTradeType,
      1, 0, 0, 0, 0, 0.5, 500000, 1500000, 0, 0, 0)

    remits.length shouldEqual 2
    val remitsSel = remits.map(x => (x.fundUuid, x.settleUuid, x.accountUuid, x.remitAmount))
    remitsSel should contain(InitData.testFundUuid, settle2Uuid, InitData.testAccountUuid, 500000)
    remitsSel should contain(InitData.testFundUuid, settle2Uuid, InitData.testAccount2Uuid, 3000000)

    settles.length shouldEqual 2
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual Some(settle2Uuid, InitData.testFundUuid, Settles.OpendaySettleType, InitData.openDate2, Settles.SettledState)
  }

  it should "be confirmed when confirm this settle" in {
    val f0 = settleApp.confirmSettle(fundUuid, settle2Uuid)
    f0.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"confirm settle failed on fund $fundUuid, settleUuid: $settle2Uuid")
    }
    val uuid = Await.result(f0, Duration.Inf)
    uuid shouldEqual settle2Uuid

    println(s"confirm settle done!")

    // query settle state
    val f1 = for {
      settles <- settleApp.getSettles(fundUuid)
    } yield settles
    f1.onFailure {
      case e =>
        println(s"ERROR: $e")
        fail(s"query settle result failed on fund $fundUuid, settleUuid: $settle2Uuid")
    }

    val settles = Await.result(f1, Duration.Inf)
    val settleSel = settles.headOption.map(x => (x.uuid, x.fundUuid, x.tpe, x.openDate.toString, x.state))
    settleSel shouldEqual Some(settle2Uuid, InitData.testFundUuid, Settles.OpendaySettleType, InitData.openDate2, Settles.ConfirmedState)
  }
}

object OpendaySettleTest extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  // initialize singleton settle actor
  val settleActorRef = system.actorOf(Props.apply[SettleActor])
  val settleApp = new SettleApp(settleActorRef)
  InitData()

  // settle on 0331
  val fundUuid = InitData.testFundUuid
  val openDate = InitData.openDate1
  val f0 = settleApp.opendaySettle(fundUuid, openDate)
  f0.onFailure {
    case e =>
      println(e)
  }
  val settleUuid = Await.result(f0, Duration.Inf)

  println(s"Settle done: settleUuid($settleUuid)")

  // query results
  val f1 = settleApp.getShares(settleUuid)
  val f2 = settleApp.getTrades(settleUuid)
  val f3 = settleApp.getTradeSummaries(settleUuid)
  val f4 = settleApp.getInvestStatements(settleUuid)
  val f5 = settleApp.getSettles(fundUuid)

  val f6 = for {
    r1 <- f1
    r2 <- f2
    r3 <- f3
    r4 <- f4
    r5 <- f5
  } yield {
    println(s"=> Shares:")
    r1.foreach(println)

    println(s"=> Trades:")
    r2.foreach(println)

    println(s"=> TradeSummaries:")
    r3.foreach(println)

    println(s"=> InvestStatements:")
    r4.foreach(println)

    println(s"=> Settles:")
    r5.foreach(println)
  }

  // settle on 0630
}
