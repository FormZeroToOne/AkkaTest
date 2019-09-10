package com.ml.actor.robot

//服务端发送给客户端的消息
case  class serverMessage(msg:String)

//客户端给服务端发送消息
case  class ClientMessage(msg:String)

