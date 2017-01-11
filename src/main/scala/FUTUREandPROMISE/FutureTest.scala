package FUTUREandPROMISE

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by Asher on 2016/11/28.
  */
object FutureTest{

  def main(args: Array[String]): Unit = {
    val s = "hello"
    val f: Future[String] = Future {
      //Thread.sleep(1000)
      s + " future!"
    }

    f onComplete{
      case Success(t) => {
        println(t)
      }
      case Failure(e) => {
        println(s"An error has occured: $e.getMessage")
      }
    }
    println("sss")

  }




}
