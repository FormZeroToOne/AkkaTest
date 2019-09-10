package com.ml.actor.spark


/**
  * worker -> master
  *
  */

// worker 向master注册自己(信息)
case class RegisterWorkerInfo(id:String,core:Int,ram: Int)

//worker给master 发送心跳信息
case class HeartBeat(id:String)


//master -> worker

//master向worker发送注册成功消息
case  object RegisteredWorkerInfo


//worker发送给自己的消息,告诉自己说要开始周期性的向master发送心跳消息
case  object SendHeartBeat

//master 自己给自己发送一个检查超时的worker的信息,并启动一个调度器，周期性检测删除超时worker
case object CheckTimeOutWorker

//master发送给自己的消息,删除超时的worker
case  object RemoveTimeOutWorker


//存储worker信息的对象
case class WorkerInfo(val id:String,core:Int,ram:Int){
  var lastHeartBeatTime: Long = _
}
