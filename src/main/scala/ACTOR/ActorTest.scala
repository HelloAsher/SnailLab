package ACTOR

import scala.actors.Actor
import scala.actors.Actor._

/**
  * Created by Asher on 2016/11/29.
  */
object ActorTest {
  def main(args: Array[String]): Unit = {
    val a1 = Actor.actor {
      var work = true

      while (work) {

        receive {
          // 接受消息, 或者用receiveWith(1000)

          case msg: String => println("a1: " + msg)

          case x: Int => work = false; println("a1 stop: " + x)

        }

      }
    }
    a1 ! "nihaoyzb!"
    a1 ! "nihao123"
    a1 ! 11
    a1 ! "res"

  }

}
