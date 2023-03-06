This Repository is a web socket server base on Netty 。



Run a websocket server easily

```java
 ServerRemote serverRemote = ServerRemote.newServerInstance(2222, requestPlus -> {
            if (RequestTypeEnum.TEXT.getType().equals(requestPlus.getType())) {
                RequestWithTextData requestWithTextData = (RequestWithTextData) requestPlus;
                String text = requestWithTextData.getText();
                log.info("server receive text data {}", text);
            } else if (RequestTypeEnum.Binary.getType().equals(requestPlus.getType())) {
                RequestWithBinaryData requestWithFileData = (RequestWithBinaryData) requestPlus;
                ByteBuf buf = requestWithFileData.getByteBuf();
                log.info("server receive binary data,buf size {}", buf.capacity());
            }
            return null;
        });
serverRemote.run();
```



Run Server in spring boot project

```java
public class SpringBootStartEvent implements ApplicationListener<SpringApplicationEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Bean(name = "serverRemote")
    ServerRemote createNettyRemote() {
        return ServerRemote.newServerInstance(2222, requestPlus -> {
            //deal with request and return response
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

If you like this repo,please give me a start ❤️，thank you ❀
