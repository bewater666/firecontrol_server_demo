package com.orient.firecontrol_server_demo.rabbit;

import com.orient.firecontrol_server_demo.dao.ConnectDetailDao;
import com.orient.firecontrol_server_demo.model.ConnectDetail;
import com.orient.firecontrol_server_demo.utils.HexUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 16:50
 * @func 消息的消费者 并对消息的接收与否做出确认 若消费者程序出现问题 消息会混滚回队列
 */
@Component
@Slf4j
public class TopicAckReceiver implements ChannelAwareMessageListener {

    private Socket client;
    @Autowired
    private TextMessageConverter textMessageConverter;
    @Autowired
    private ConnectDetailDao connectDetailDao;


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //deliveryTag该消息的index。
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msg = textMessageConverter.fromMessage(message).toString();
            if (msg.substring(24, 26).equals("50")){//下发50控制指令
                //eb90eb9002 3201110100 0010 50 01 0100 01 01 03
                System.out.println("收到控制指令:==="+msg);
                String buildCode = msg.substring(10, 20);
                String substring = msg.substring(28, 32);
                String boxCode = buildCode+substring;  //监控箱编号
                List<ConnectDetail> connectDetails = connectDetailDao.queryByBoxCode(boxCode);
                if (connectDetails.size()!=0){
                    ConnectDetail connectDetail = connectDetails.get(connectDetails.size() - 1);
                    String ipaddr = connectDetail.getIpaddr();
                    int port = connectDetail.getPort();
                    try {
                        client = new Socket(ipaddr, port);
                        byte[] bytes = HexUtil.hexStringToByteArray(msg);
                        sendMessage(bytes);
                        //发完关闭连接
                        client.close();
                    } catch (IOException e) {
                        System.out.println("连接硬件设备失败,请检查"+connectDetail.getBoxcode()+"硬件设备是否在线");
                    }
                }

            }
            if (msg.substring(24, 26).equals("60")){//下发60对时指令
                //60指令是发送给所有正在连接的硬件设备
                List<ConnectDetail> connectDetails = connectDetailDao.listAll();
                for (ConnectDetail connectDetail:
                connectDetails) {
                    String ipaddr = connectDetail.getIpaddr();
                    int port = connectDetail.getPort();
                    try {
                        client = new Socket(ipaddr, port);
                        byte[] bytes = HexUtil.hexStringToByteArray(msg);
                        sendMessage(bytes);
                        //发完关闭连接
                        client.close();
                    } catch (IOException e) {
                        System.out.println("连接硬件设备失败,请检查"+connectDetail.getBoxcode()+"硬件设备是否在线");
                    }

                }

            }
            channel.basicAck(deliveryTag, true);//是否批量. true：将一次性拒绝所有小于deliveryTag的消息。
        } catch (Exception e) {
            channel.basicReject(deliveryTag, true);//为true会重新放回队列
            e.printStackTrace();
        }
    }

    private void sendMessage(byte[] bytes) {
        try {
            OutputStream outputStream = client.getOutputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(bytes); //输出
            byteArrayOutputStream.flush();
            byteArrayOutputStream.writeTo(outputStream);
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("回复客户端-设置输出流错误:{}", e.getMessage());
        }
    }
}
