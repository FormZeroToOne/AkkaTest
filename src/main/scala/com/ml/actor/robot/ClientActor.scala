package com.ml.actor.robot

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn

class ClientActor(host:String,post:Int) extends Actor{

  var serverActorRef: ActorSelection = _  //服务端的代理对象

  //在receive 方法之前调用(获取到serverRef)
  override def preStart(): Unit = {
   serverActorRef=  context.actorSelection(s"akka.tcp://Server@${host}:${post}/user/YR")
  }

  override def receive: Receive = {  //shit
    case "start" => println("YRRobot已启动...")
    case msg: String =>{//shit
      serverActorRef! ClientMessage(msg)  //把客户端的内容发送到服务端(actorRef) =>服务端的mailbox ->服务端的receive方法
    }

    case  serverMessage(msg) => println(s"收到服务端的消息： $msg")

  }
}


object ClientActor extends App{
  val host:String = "127.0.0.1"
  val port:Int = 8800

  val shost:String = "127.0.0.1"
  val sport:Int = 8878
  private val config: Config = ConfigFactory.parseString(
    s"""
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
akka.remote.netty.tcp.hostname = $host
akka.remote.netty.tcp.port = $port

       """.stripMargin
  )
  private val clietntActorSystem = ActorSystem("clietntActor",config)

  //创建dispatch | mailbox
  private val clietn00Ref: ActorRef = clietntActorSystem.actorOf(Props(new ClientActor(shost,sport)),"clietn001")

 clietn00Ref ! "start"  //自己给自己发送了一条消息，到自己的mailbox => receive

  while (true){
     val question = StdIn.readLine()  //同步阻塞,shit
    clietn00Ref ! question //mailbox -> receive
  }

}