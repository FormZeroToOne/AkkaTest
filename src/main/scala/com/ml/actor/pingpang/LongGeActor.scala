package com.ml.actor.pingpang

import akka.actor.{Actor, ActorRef}

/**
  * 马龙
  */
class LongGeActor(kg: ActorRef) extends Actor{
  override def receive: Receive = {

    case  "start" => {
      println("I'm Ready")
      kg ! "啪"
    }

    case "啪啪"  =>{
      println("你真猛")
      Thread.sleep(1000)
      kg ! "啪"
    }
  }
}
