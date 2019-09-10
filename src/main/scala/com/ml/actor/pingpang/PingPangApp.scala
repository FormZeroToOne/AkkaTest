package com.ml.actor.pingpang

import akka.actor.{ActorRef, ActorSystem, Props}

object PingPangApp extends App {

  //actorSystem
  private val pingPangActor = ActorSystem("PingPangActor")


  //通过actorSystem 创建ActorRef
  //创建kgActorRef
  private val kgActorRef: ActorRef = pingPangActor.actorOf(Props[KeGeActor],"kg")

  //创建LongGeActor 的actorRef

  private val mmRef: ActorRef = pingPangActor.actorOf(Props(new LongGeActor(kgActorRef)),"mm")

  mmRef! "start"
  kgActorRef! "start"


}
