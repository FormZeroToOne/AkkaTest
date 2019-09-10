package com.ml.actor.spark

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._ //导入时间单位

class SparkMaster extends Actor {

  //存储worker信息的
  val id2WorkerInfo = collection.mutable.HashMap[String, WorkerInfo]()


  /* override def preStart(): Unit = {
     context.system.scheduler.schedule(0 millis,6000 millis,self,RemoveTimeOutWorker)
   }*/

  override def receive: Receive = {
    //会受到worker注册过来的信息
    case RegisterWorkerInfo(wkId, core, ram) => {
      //将worker的信息存储起来，存储到hashMap中
      if (!id2WorkerInfo.contains(wkId)) {
        val workerInfo = new WorkerInfo(wkId, core, ram)
        id2WorkerInfo += ((wkId, workerInfo))

        //master存储完worker注册的数据之后,要告诉worker说你已经注册成功
        sender() ! RegisteredWorkerInfo //此时worker会收到注册成功之后的消息
      }
    }
    case HeartBeat(wkId) => {
      //master收到worker的心跳消息之后，更新worker的上一次心跳时间
      val workInfo = id2WorkerInfo(wkId)
      //更改心跳时间
      val currenTime = System.currentTimeMillis()
      workInfo.lastHeartBeatTime = currenTime

    }

    case CheckTimeOutWorker => {
      import context.dispatcher //是用调度器的时候,必须导入context.dispatcher
      context.system.scheduler.schedule(0 millis, 6000 millis, self, RemoveTimeOutWorker)
    }

    case RemoveTimeOutWorker => {
      //将hashMap中的所有value都拿出来，查看当前时间和上一次心跳时间的差 3000
      val workerInfos = id2WorkerInfo.values
      val currentTime = System.currentTimeMillis()

      //过滤超时的worker
      workerInfos.filter(wkInfo => currentTime - wkInfo.lastHeartBeatTime > 3000)
        .foreach(wk => id2WorkerInfo.remove(wk.id))

      println(s"-------------还剩 ${id2WorkerInfo.size} 存活的worker------------")
    }


  }


}


object SparkMaster {

  def main(args: Array[String]): Unit = {

    //参数校验
    if (args.length != 3) {
      println(
        """
          |请输入参数:<host><post><masterName>
          |
        """.stripMargin
      )
      sys.exit() //终止程序
    }

    val host = args(0)
    val port = args(1)
    val masterName = args(2)


    val config = ConfigFactory.parseString(
      s"""
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
akka.remote.netty.tcp.hostname = $host
akka.remote.netty.tcp.port = $port
      """.stripMargin)


    val sparkMasterSystem = ActorSystem("SparkMaster", config)
    val masterNameRef = sparkMasterSystem.actorOf(Props[SparkMaster], masterName)

    //自己给自己发送消息,去启动一个调度器,定期的检测HashMap中超时的worker
    masterNameRef ! CheckTimeOutWorker
  }


}

