### 基于分布式session的接口权限控制 样例 
 [返回主文档](../README.md)

**spring-session官方传送点**：[官方传送点](https://spring.io/projects/spring-session)



#### 一、针对Session一致性问题处理方案

##### 1、Session定向转发
> 利用同一会话只发往同一服务器的方式去处理（也就是会话绑定服务器）
>
>**Tips:** `基本不被采用。因为，如果一台服务器重启，那么会导致转发到这个服务器上的 Session 全部丢失。`

（A）nginx中可以参考 第三方模块 nginx-sticky-module

（B） 各种SLB工具中，利用ip或其他方式达成的效果

 


 ##### 2、Session平行同步复制
 > 多台服务器间复制同步seesion
>
>**Tips:** 基本不采用Session 都要同步到每一个节点上，不但效率低，而且浪费内存

（A）具体参考，可以根据容器选择对应的session集群复制查询。

    eg: tomcat集群session复制




##### 3、利用外部存储处理
> 利用sql或nosql来存储处理原本需要容器存储的Session
>
>**Tips:** 现在比较通用的方式

（A）利用附带的容器拓展方式，读取外部存储内容
        
     eg:
     tomcat: 参照《Redissson Tomcat会话整合》格式寻找当前tomcat版本的解决方案
     
     jetty: 参照 《jetty 集群Seesion 存储mysql/Redis/mongoDb》 格式寻找解决方案

（B）基于应用层处理（利用Filter）
>  利用Filter处理`HttpServletRequest` 请求对象，包装成自己的 RequestWrapper 对象，从而让实现调用 HttpServletRequest#getSession() 方法时，获得读写外部存储器的 SessionWrapper 对象



#### 二、Spring-Session 与 Redis
> 官方整合示例文档参考：`https://spring.io/projects/spring-session-data-redis`
>
> 前提哦：需要一个`redis 2.8+` 以上的版本哦

##### 1、pom.xml新增依赖

```xml
<!-- 实现对 Spring Session 使用 Redis 作为数据源的自动化配置 -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
<!-- 自动化配置 Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```



##### 2、@EnableRedisHttpSession

> 启用配置，启用httpsession，你可以任意摆放到可以被spring管理的地方



##### 3、yaml配置

```yaml
spring:
  session:
    store-type: redis
    redis:
      save-mode: redis
      flush-mode: on_save
  redis:
    host: 127.0.0.1
    password: coffeeandice
    port: 6379
#    database: 0  默认是第一个数据库，你可以自定义任意一个（已存在）的库来存放
```



#### 三、Spring-Session 与 MongoDB

> 官方整合示例文档参考：`https://spring.io/projects/spring-session-data-mongodb`

##### 1、pom.xml新增依赖

```xml
<!-- 实现对 Spring Session 使用 MongoDB 作为数据源的自动化配置 -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-mongodb</artifactId>
</dependency>

<!-- 自动化配置 Spring Data Mongodb -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```



##### 2、@EnableMongoHttpSession

> 启用配置，启用httpsession，你可以任意摆放到可以被spring管理的地方



##### 3、yaml配置

```yaml
spring:
  data:
    # MongoDB 配置项，对应 MongoProperties 类
    mongodb:
      host: 127.0.0.1
      port: 27017
      database: admin # 可以通过db.stats()查看数据库
      username: root
      password: root # 建议纯英文，因为加上数字会识别不出
      # 上述属性，也可以只配置 uri
#      uri: mongodb://localhost/admin

logging:
  level:
    org:
      springframework:
        data:
          mongodb:
            core: DEBUG # 打印 mongodb 操作的具体语句。生产环境下，不建议开启。

```



##### 4、新增配置类SessionConfiguration

>使用默认的`JdkMongoSessionConverter`可读性很差的，参考之前文档`https://blog.csdn.net/CoffeeAndIce/article/details/91355024` 有异曲同工之处

```java
// 默认情况下
@Bean
public JdkMongoSessionConverter jdkMongoSessionConverter() {
    return new JdkMongoSessionConverter(Duration.ofMinutes(30));
}
```



> 为了可读性替换走起

```java
@Configuration
public class SessionConfiguration {

    @Bean
    public AbstractMongoSessionConverter mongoSessionConverter() {
        return new JacksonMongoSessionConverter();
    }

}
```

#### 四、触发测试

|         name         |                         description                          |
| :------------------: | :----------------------------------------------------------: |
|        设置值        | [设置key为coffeeandice 值为 handsome的value对到session内](http://localhost:8080/session/set?key=22&value=666) |
|        获取值        | [测试获取已经设置的session值](http://localhost:8080/session/get_all) |
| 测试代码路径（一致） | [示例路径地址](./RedisHandler/src/main/java/cn/coffeeandice/controller/SessionController.java) |

