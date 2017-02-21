import java.io.{File, PrintWriter}
import java.sql.Date
import java.util.UUID

import akka.http.scaladsl.util.FastFuture
import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

object GenInitTableSqls extends App {
  val schemaSqls =
//    DBSchema.funds.schema.createStatements ++
      DBSchema.fundNavs.schema.createStatements ++
      DBSchema.investors.schema.createStatements ++
      DBSchema.purchases.schema.createStatements ++
      DBSchema.redemptions.schema.createStatements ++
      DBSchema.dividendTypeChanges.schema.createStatements ++
      DBSchema.dividends.schema.createStatements ++
      DBSchema.settles.schema.createStatements ++
      DBSchema.trades.schema.createStatements ++
      DBSchema.tradeSummaries.schema.createStatements ++
      DBSchema.shares.schema.createStatements ++
      DBSchema.investStatements.schema.createStatements ++
      DBSchema.remits.schema.createStatements

  //  println(schemaSqls.mkString(";\n"))

  val outputFile = "/home/fuyf/project/fundmng-flyway/sql/V3__CreateSettleTables.sql"
  val writer = new PrintWriter(new File(outputFile))
  writer.write(schemaSqls.mkString(";\n"))
  writer.close()

  Source.fromFile(outputFile).foreach(print)
}

object InitTable extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val schemaSqls = Seq(
//    DBSchema.funds.schema.create,
      DBSchema.fundNavs.schema.create,
      DBSchema.investors.schema.create,
      DBSchema.purchases.schema.create,
      DBSchema.redemptions.schema.create,
      DBSchema.dividendTypeChanges.schema.create,
      DBSchema.dividends.schema.create,
      DBSchema.settles.schema.create,
      DBSchema.trades.schema.create,
      DBSchema.tradeSummaries.schema.create,
      DBSchema.shares.schema.create,
      DBSchema.investStatements.schema.create,
      DBSchema.remits.schema.create
  )

  Await.result(schemaSqls.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
}

object InitData extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val now = Some(System.currentTimeMillis())

  def genUuid = UUID.randomUUID().toString

  val testFundUuid = genUuid
  val testFund = "S-TestFund"
  val testAccountUuid = genUuid
  val testAccount = "12345678"
  val testAccount2Uuid = genUuid
  val testAccount2 = "87654321"

  val initData = Seq(
    // init Fund
    DBSchema.funds += Fund(None, testFund, "TestFund", Some("TF"),
      None, Some(Date.valueOf("2016-01-01")), None, Some(0), Some(0),
      Some(0), Some(0), Some(4), Some(4), Some(4),
      Funds.RaisingFundState, Some("zh"), Some("zy"),
      testFundUuid, 1, Funds.ReviewedReviewState, Funds.Alive),

    // init FundNav
    DBSchema.fundNavs += FundNav(None, testFundUuid, testFund, Date.valueOf("2017-03-31"),
      1, 1, 1),

    // init Investor
    DBSchema.investors += Investor(None, testAccount, "fuyf", "13888888888", None, None,
      None, Investors.PersonType, Investors.InvestedState, Investors.Active,
      testAccountUuid, 1, Investors.ReviewedReviewState, Investors.Alive),

    // init Purchase
    DBSchema.purchases += Purchase(None, testAccountUuid, testAccount,
      testFundUuid, testFund, Date.valueOf("2017-03-31"),
      Purchases.SubscribeType, Date.valueOf("2017-03-20"),
      None, None, Some(1000000), None),
    DBSchema.purchases += Purchase(None, testAccount2Uuid, testAccount2,
      testFundUuid, testFund, Date.valueOf("2017-03-31"),
      Purchases.SubscribeType, Date.valueOf("2017-03-23"),
      None, None, Some(5000000), None)

    // init Redemption

    // init DividendTypeChange

    // init Dividend

  )

  Await.result(initData.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
}
