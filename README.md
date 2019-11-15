# rabbitmq中间件

### 一、quque方式
**1. P2P(简单队列模式)**  

![image](https://www.rabbitmq.com/img/tutorials/python-one.png)
    
一个生产者对应一个消费者，点对点发送
   - P 生产者
   - 队列
   - C 消费者  

    流程：  
    1）.创建连接、通道，声明一个队列     
    2）.消费者监听此队列，告知服务器要回调的对象    
    3）.生产者生产消息放入队列(消息内容是字节数组) ，一旦有消息到队列，RMQ服务器就会发送给消费者
    
**2. WORK QUEUE(竞争消费者模式)**   

![image](https://www.rabbitmq.com/img/tutorials/python-two.png)

一个生产者对应多个消费者，一个消息只能被一个消费者消费      
    - 流程：同P2P，不同是一个队列被多个消费者监听
    - 应用场景：效率高的消费者消费消息多,可以用来进行负载均衡.

**注意：实际上1和2两种模式其实是属于一种模式，只不过消费者数量不同而已，其他完全相同**    

### 二、exchange方式
**在RabbitMQ中,交换器主要有四种类型:++direct,fanout,topic,headers++**
流程：

```
1）生产者声明一个交换机，不声明队列，将消息发送到交换机
2）消费者声明一个队列，将队列和交换机绑定，接收队列消息，
注意点：队列存在与否只与消费者有关，如果没有消费者，则没有队列存在，消息直接丢失，生产者只与交换机对接，交换机将消息发送到与其绑定的队列中；只有在发送消息的时候消费者和队列活着才能发送成功，已经死亡或还未出生的消费者都不能收到消息。类似于用喇叭喊话
```

**3. Publish/Subscribe(发布/订阅模式)**       
**fanout**

![image](https://www.rabbitmq.com/img/tutorials/python-three.png)

- 介绍：一个生产者将消息首先发送到交换器,消费者声明一个队列并与交换机绑定，接收所有该交换机的消息。 

**4. Routing(路由订阅)**        
**direct**

![image](https://www.rabbitmq.com/img/tutorials/python-four.png)
    
- 介绍：一个生产者将消息首先发送到交换器,并声明可订阅的roukingKey(即图中的err,info等字符串),然后消费者声明一个队列与交换机绑定，并订阅该交换机routingKey的消息.
- 与发布订阅模式相似,只不过发布/订阅是无意识广播,发送给所有绑定交换机的队列,路由订阅是根据路由键选择性订阅，稍微灵活一点

**5. Topic(主题)**        
**topic**

![image](https://www.rabbitmq.com/img/tutorials/python-five.png)
    
- 介绍：一个生产者将消息首先发送到交换器,并声明可订阅的roukingKey(即图中的err,info等字符串),然后消费者声明一个队列与交换机绑定，并订阅该交换机routingKey的消息.
- 与Routing模式相似,但是订阅要更加灵活，它的routingKey通过"."分割成若干段，可以通过"*"，"#"进行匹配
- "*"可以匹配一段任意字符串
- "#"可以匹配任意多段字符串
    - 例如：图中"*.orange. *"可以匹配"abc.orange.def"
    - "*. *.rabbit"可以匹配"abc.def.rabbbit"
    - "lazy.#"可以匹配"lazy.abc.efg"或"lazy.hij.klmn.opqrst"

**6. Headers**(较少用)  
**headers**

- 介绍：一个生产者将消息首先发送到交换器,并声明可订阅的headers,然后消费者声明一个队列与交换机绑定，并订阅该交换机headers的消息.
- 与Topic模式基本相同，不过不是通过routingKey，而是通过headers(键值对)
- 例子：
```
#生产者headers
Map<String,Object> headers = new Hashtable<>();
headers.put("abc","hello");
headers.put("def",123);

#消费者headers
#消费者要在headers声明匹配类型x-match，值为any或all
Map<String,Object> matchHeaders = new Hashtable<>();
matchHeaders.put("x-match", "any");//any代表匹配任意一个键值对就可以，all代表全部键值对匹配才会订阅
matchHeaders.put("abc", "hello");
```

github地址:https://github.com/mrguanxs/rmq_middlware    
待改进：消息不应该直接返回，而是应<font color='red'>在回调中执行方法</font>
