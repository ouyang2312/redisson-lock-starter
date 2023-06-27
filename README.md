### 1.引入依赖

```xml
<dependency>
<groupId>com.oy</groupId>
<artifactId>redisson-lock-starter</artifactId>
<version>${version}</version>
</dependency>
```

### 2. spring的yml文件

~~~yml
oylock:
  enable: true
  address: redis://127.0.0.1:6379
  password: 123456
  database: 10
  waitTime: 60
  leaseTime: 60
#    cluster-server:
#      node-addresses: redis://127.0.0.1:6379,redis://127.0.0.1:6379
~~~

address 和 node-addresses有一个就行了

### 3. 使用注解 @Lock @LockKey 使用

```java
@Service
public class TestService {
    
    @Lock
    public String test(@LockKey String param) throws Exception {
        Thread.sleep(1000 * 50);
        return "success";
    }
}

```
