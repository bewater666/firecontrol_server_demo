package com.orient.firecontrol_server_demo.rabbit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 17:16
 * @func 对消息体进行格式转换 因为消息message 的body是一个byte[]的形式
 */
@Component
public class TextMessageConverter implements MessageConverter {
    /**
     * 从 String 转换为 byte[]
     * @param object
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    /**
     * 从 byte[] 转换为 String
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        if(null != contentType && contentType.contains("text")) {
            return new String(message.getBody());
        }
        return message.getBody();
    }

}
