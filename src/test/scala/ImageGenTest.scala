import java.io.File

import com.heqiying.fundmng.settle.utils.{ ImageGen, ImageGenConfig }

object ImageGenTest extends App {
  val imageGen = ImageGen(new ImageGenConfig {
    width := 600
    disableSmartWidth := true
    encoding := "utf-8"
  })

  val name = "fuyf"
  val identityId = "123456"
  val accountId = "9000000"
  val investorType = "个人"

  val page =
    <html>
      <body style="">
        <div>
          <img src="/home/fuyf/Downloads/hqy/header.jpg" width="600" alt=""/>
        </div>
        <div style="height: 100px; margin: 0 auto; display: table; font-size: 20; color: #E63F00;">
          <div style="text-align: center; display: table-cell; vertical-align: middle; font-weight:bold;">基金交易对账单</div>
        </div>
        <div style="margin-left: 80px; font-size: 16; display: table;">
          <p>客户姓名: { name }</p>
          <p>证件号码: { identityId }</p>
          <p>基金账户: { accountId }</p>
          <p>客户类型: { investorType }</p>
        </div>
        <div style="margin: 0 auto; text-align: center; ">
          <p style="font-size: 20; color: #E63F00;">您本期的基金交易明细</p>
          <div style="text-align: center;">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td style="border:1px solid">交易日期</td>
                <td style="border:1px solid">交易类型</td>
                <td style="border:1px solid">基金名称</td>
                <td style="border:1px solid">成交金额(元)</td>
                <td style="border:1px solid">成交份额(份)</td>
                <td style="border:1px solid">手续费(元)</td>
                <td style="border:1px solid">成交净值</td>
              </tr>
            </table>
          </div>
        </div>
      </body>
    </html>

  imageGen.run(page, new File("/home/fuyf/vmshare/test.png"))
}
