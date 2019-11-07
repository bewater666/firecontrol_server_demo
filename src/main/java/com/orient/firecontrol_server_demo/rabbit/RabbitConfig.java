package com.orient.firecontrol_server_demo.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 14:08
 * @func
 */
@Configuration
public class RabbitConfig {
    public static final String QUEUE_A = "queue_ff";
    public static final String QUEUE_B = "queue_40";
    public static final String QUEUE_C = "queue_41";
    public static final String QUEUE_D = "queue_51";
    public static final String QUEUE_E = "queue_60";
    public static final String QUEUE_F = "queue_50";  //控制下发指令  下发这个指令的时候充当消费者  其他指令为生产者


    @Bean
    public Queue queue_ff(){
        return new Queue(QUEUE_A);
    }

    @Bean
    public Queue queue_40(){
        return new Queue(QUEUE_B);
    }

    @Bean
    public Queue queue_41(){
        return new Queue(QUEUE_C);
    }

    @Bean
    public Queue queue_51(){
        return new Queue(QUEUE_D);
    }

    @Bean
    public Queue queue_60(){
        return new Queue(QUEUE_E);
    }

    @Bean
    public Queue queue_50(){
        return new Queue(QUEUE_F);
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange("topicExchange");
    }

    @Bean
    Binding bindingExchangeMessageff(){
        return BindingBuilder.bind(queue_ff()).to(exchange()).with("topic.ff");
    }

    @Bean
    Binding bindingExchangeMessage40(){
        return BindingBuilder.bind(queue_40()).to(exchange()).with("topic.40");
    }

    @Bean
    Binding bindingExchangeMessage41(){
        return BindingBuilder.bind(queue_41()).to(exchange()).with("topic.41");
    }

    @Bean
    Binding bindingExchangeMessage51(){
        return BindingBuilder.bind(queue_51()).to(exchange()).with("topic.51");
    }

    @Bean
    Binding bindingExchangeMessage60(){
        return BindingBuilder.bind(queue_60()).to(exchange()).with("topic.60");
    }

    @Bean
    Binding bindingExchangeMessage50(){
        return BindingBuilder.bind(queue_50()).to(exchange()).with("topic.50");
    }


    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private TopicAckReceiver topicAckReceiver;//消息接收处理类

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // RabbitMQ默认是自动确认，这里改为手动确认消息

        //负责接受 50下发反控指令  60下发对时指令
        container.setQueues(queue_50(),queue_60());
        container.setMessageListener(topicAckReceiver);

        return container;
    }

}
