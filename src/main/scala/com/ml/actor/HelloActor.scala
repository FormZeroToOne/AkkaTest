package com.ml.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class  HelloActor extends Actor {
  //用来接受消息
  override def receive: Receive = {
    //接受消息并处理
    case "你好帅" => println("竟说实话,我喜欢你种人")
    case  "丑"  => println("滚犊子 ！")
    case  "stop" => {
      context.stop(self)  //关闭实例
      context.system.terminate();  //关闭actorSystem

    }
  }
}

object HelloActor {

  private val nBFactory = ActorSystem("NBFactory")//相当于一个工厂
  private val helloActorRef: ActorRef = nBFactory.actorOf(Props[HelloActor],"HelloActor")

  def main(args: Array[String]): Unit = {

    //给自己发送消息
    helloActorRef! "你好帅"
    helloActorRef! "丑"

    helloActorRef! "stop"

  }

}
