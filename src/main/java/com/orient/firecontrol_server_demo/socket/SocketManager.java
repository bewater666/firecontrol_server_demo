package com.orient.firecontrol_server_demo.socket;

import com.orient.firecontrol_server_demo.dao.ConnectDetailDao;
import com.orient.firecontrol_server_demo.model.ConnectDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 15:46
 * @func
 */
@Slf4j
@Component
public class SocketManager {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectDetailDao connectDetailDao;


    public void sendFF(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.ff", msg);
    }

    public void send40(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.40", msg);
    }

    public void send41(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.41", msg);
    }

    public void send51(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.51", msg);
    }

    public void send60(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.60", msg);
    }


    public void insert(ConnectDetail connectDetail){
        connectDetailDao.insert(connectDetail);
    }

}
