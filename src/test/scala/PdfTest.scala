import io.github.cloudify.scala.spdf._
import java.io._
import java.net._

import org.scalatest.{ FlatSpec, Matchers }

object Sort {
  def quickSort[T <% Ordered[T]](s: Seq[T]): Seq[T] = {
    if (s.length <= 1) s
    else {
      val pivot = s(s.length / 2)
      quickSort(s filter (pivot >)) ++ (s filter (pivot ==)) ++ quickSort(s filter (pivot <))
    }
  }
}

class QuickSortTest extends FlatSpec with Matchers {
  "A Seq" should "be quick sort" in {
    val s = Seq(5, 4, 3, 2, 1)
    Sort.quickSort(s).foreach(print)
  }
}

object PdfTest extends App {

  // Create a new Pdf converter with a custom configuration
  // run `wkhtmltopdf --extended-help` for a full list of options
  val pdf = Pdf(new PdfConfig {
    orientation := Portrait
    pageSize := "Letter"
    marginTop := "1in"
    marginBottom := "1in"
    marginLeft := "1in"
    marginRight := "1in"
  })

  val name = "fuyf"

  val page =
    <html>
      <body style="">
        <div>
          <img src="/home/fuyf/Downloads/hqy/header.jpg" width="1280" alt=""/>
        </div>
        <div style="height: 200px; margin: 0 auto; display: table; font-size: 30; color: #E63F00;">
          <div style="text-align: center; display: table-cell; vertical-align: middle; font-weight:bold;">基金交易对账单</div>
        </div>
        <div style="height: 300px; margin-left: 80px; font-size: 16; display: table;">
          <p>客户姓名: { name }</p>
          <p>客户姓名: { name }</p>
          <p>客户姓名: { name }</p>
          <p>客户姓名: { name }</p>
        </div>
      </body>
    </html>

  // Save the PDF generated from the above HTML into a Byte Array
  //  val outputStream = new ByteArrayOutputStream
  pdf.run(page, new File("/home/fuyf/vmshare/test1.pdf"))

  //  println(SourceDocumentLike.URLSourceDocument.commandParameter(new URL("http://www.baidu.com")))

  // Save the PDF of Google's homepage into a file
  //  pdf.run(new URL("http://www.baidu.com"), new File("/tmp/google.pdf"))
}
