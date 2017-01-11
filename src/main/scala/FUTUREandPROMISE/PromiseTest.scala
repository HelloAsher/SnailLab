package FUTUREandPROMISE

import java.util.{Timer, TimerTask}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by Asher on 2016/11/28.
  */
object PromiseTest {
  val timer = new Timer()

  def delayedSuccess[T](seconds: Int, value: T): Future[T] = {
    val result = Promise[T]
    Thread.sleep(3000)
    result.success(value)

    result.future
  }

  def delayedFailure[T](seconds: Int, msg: String): Future[T] = {
    val result = Promise[T]
    timer.schedule(new TimerTask {
      override def run() = {
        result.failure(new IllegalArgumentException(msg))
      }
    }, seconds * 1000)
    result.future
  }

  def main(args: Array[String]): Unit = {
    val f = delayedSuccess(3, "nihaoyzb!")
    //val f = delayedFailure(3, "nihaoyzb!")
    f onComplete {
      case Success(t) => {
        println(t)
      }
      case Failure(e) => {
        println(s"An error has occured: $e")
      }
    }
    //println("sss")
  }

}
