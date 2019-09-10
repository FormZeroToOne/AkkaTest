package com.ml.actor.robot

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn

class servers extends Actor{
  override def receive: Receive = {
    case  "start" => println("YRRobot is start ....")

    case ClientMessage(msg) => {
     println(s"收到客户端的消息: $msg")
      msg match {
        case "你叫啥" => sender() ! serverMessage("hello,My name is  YRRobot")
        case "你是男是女" => sender() ! serverMessage("o, i is Female")
        case "你有男朋友么" => sender() ! serverMessage("No, I don't have this")
        case _ => sender() ! serverMessage("sorry,I'm don't answer you this question")  //sender()发送端代理对象，发送到客户端的mailbox中 ->客户端的receive
      }

    }


  }
}

object servers extends  App{
  val host:String = "127.0.0.1"
  val port:Int = 8878

  private val config: Config = ConfigFactory.parseString(
    s"""
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
akka.remote.netty.tcp.hostname = $host
akka.remote.netty.tcp.port = $port

       """.stripMargin
  )


  //指定Ip 和端口
  private val serverActorSystem = ActorSystem("Server",config)

  private val serverActorRef: ActorRef = serverActorSystem.actorOf(Props[servers],"YR")
  serverActorRef! "start"
}
