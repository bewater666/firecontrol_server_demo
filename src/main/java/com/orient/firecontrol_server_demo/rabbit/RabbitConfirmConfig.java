package com.orient.firecontrol_server_demo.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 16:17
 * @func
 */
@Configuration
public class RabbitConfirmConfig {

    @Bean
    public RabbitTemplate createTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate  = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);//必须设置为true，才能让下面的ReturnCallback函数生效

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("=======RabbitMQ ConfirmCallback=========");
                System.out.println("correlationData = " + correlationData);
                System.out.println("ack = " + ack);
                System.out.println("cause = " + cause);
                System.out.println("=======RabbitMQ ConfirmCallback=========");
            }
        });


        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("--------------RabbitMQ ReturnCallback----------------");
                System.out.println("message = " + message);
                System.out.println("replyCode = " + replyCode);
                System.out.println("replyText = " + replyText);
                System.out.println("exchange = " + exchange);
                System.out.println("routingKey = " + routingKey);
                System.out.println("--------------RabbitMQ ReturnCallback----------------");
            }
        });

        return rabbitTemplate;




    }



}
