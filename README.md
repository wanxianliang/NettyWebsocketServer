This Repository is a Request Framwork base on Netty。you can use this to run a netty server Simply or build a netty client simply。



Run a netty server with follow code

```java
ServerRemote serverRemote = ServerRemote.newServerInstance(8080, requestPlus -> {
  //deal with request and then response
            return ResponsePlus.build(0, "ok");
});
serverRemote.run();
```



Run a client with follow code

```java
ConnectManager connectManager = ConnectManager.newConnectInstance("127.0.0.1", 2222);
RequestPlus requestPlus = new RequestPlus();
String requestId = connectManager.sendMsg(requestPlus);
connectManager.handleResult(requestId, responsePlus -> {
  //deal with response       
});
```



Todos

- [ ] Healthy check

If you like this repo,please give me a start ❤️，thank you ❀
