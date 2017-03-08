import java.io.{ File, PrintWriter }
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
      DBSchema.remits.schema.createStatements ++
      DBSchema.appendants.schema.createStatements

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
    DBSchema.remits.schema.create,
    DBSchema.appendants.schema.create
  )

  Await.result(schemaSqls.foldLeft(FastFuture.successful[Any](()))((fs, x) => fs.flatMap(_ => db.run(x))), Duration.Inf)
}

