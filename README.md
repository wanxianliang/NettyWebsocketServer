This Repository is a Request Framwork base on Netty。you can use this to run a netty server Simply or build a netty client simply。



Run a netty server with follow code

```java
ServerRemote serverRemote = ServerRemote.newServerInstance(8080, requestPlus -> {
  //deal with request and then response
            return ResponsePlus.build(0, "ok");
});
serverRemote.run();
```



Run Server in spring boot project

```java
public class SpringBootStartEvent implements ApplicationListener<SpringApplicationEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Bean(name = "serverRemote")
    ServerRemote createNettyRemote() {
        return ServerRemote.newServerInstance(8080, requestPlus -> {
            //deal with request and return response
            return ResponsePlus.build(0, "ok");
        });
    }

    @Override
    public void onApplicationEvent(SpringApplicationEvent springApplicationEvent) {
        Object bean = applicationContext.getBean("serverRemote");
        if (bean instanceof ServerRemote) {
            try {
                ((ServerRemote) bean).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("Start netty server successfully");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```



Run a client with follow code

```java
String remoteAddress = "127.0.0.1";
int port = 8080;
ConnectManager connectManager = ConnectManager.newConnectInstance(remoteAddress, port);
RequestPlus requestPlus = new RequestPlus();
String requestId = connectManager.sendMsg(requestPlus);
connectManager.handleResult(requestId, responsePlus -> {
//deal with response
});
});
```



Todos

- [ ] Healthy check

If you like this repo,please give me a start ❤️，thank you ❀
