import com.heqiying.fundmng.settle.utils.{ AppError, AppErrors }

object AppErrorTest extends App {
  val a = AppErrors.AlreadyInSettling()
  println(a)
}
