package com.ml.actor.spark

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._  //导入时间单位


class SparkWorker(masterUrl : String)  extends Actor{

  //master 的ref 对象
   var masterProxy:ActorSelection = _
   val workerId:String = UUID.randomUUID().toString
  override def preStart(): Unit = {
    masterProxy= context.actorSelection(masterUrl)
  }


  override def receive: Receive = {
    //worker要向master注册自己的信息
    case  "started" =>{
      //向master注册自己的信息:id,core,ram
      masterProxy ! RegisterWorkerInfo(workerId,4,32*1024)  //此时master会收到该条消息
    }

    case RegisteredWorkerInfo => { //master发送给自己的注册成功消息
      //worker要启动一个定时器,定时向master发送心跳
      import  context.dispatcher
      context.system.scheduler.schedule(0 millis,1500 millis,self,SendHeartBeat)
    }

    case SendHeartBeat => {
      //开始向master发送心跳了
      println(s"-------------$workerId 发送心跳-----------")
      masterProxy ! HeartBeat(workerId)  //此时,master将会收到心跳雄安熙
    }

  }
}

object SparkWorker{

  def main(args: Array[String]): Unit = {

    //参数校验
    if(args.length!=4){
      println(
        """
          |请输入参数:<host><post><workName><masterUrl>
          |
        """.stripMargin
      )
      sys.exit()  //终止程序
    }

    val  host = args(0)
    val  port = args(1)
    val workName = args(2)
    val masterUrl = args(3)



    val config = ConfigFactory.parseString(
      s"""
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
akka.remote.netty.tcp.hostname = $host
akka.remote.netty.tcp.port = $port
      """.stripMargin)



    val sparkWorkerSystem = ActorSystem("SparkWorker",config)
    //创建workerActorRef
    val workerActorRef = sparkWorkerSystem.actorOf(Props(new SparkWorker(masterUrl)),workName)

    //给自己发送一个已启动的消息,表示自己已就绪了
    workerActorRef  ! "started"

  }
}
