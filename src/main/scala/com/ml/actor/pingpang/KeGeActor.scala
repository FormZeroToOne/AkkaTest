package com.ml.actor.pingpang

import akka.actor.Actor

/**
  * 张继科
  */
class KeGeActor extends  Actor{
  override def receive: Receive = {
    case "start" => println("你来一个: I’m ok")
    case  "啪"   =>{
          println("继科:那必须的")
          Thread.sleep(1000)
          sender()! "啪啪"
    }
  }
}
