package com.orient.firecontrol_server_demo;

import com.orient.firecontrol_server_demo.socket.SocketThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ServerSocket;
@Slf4j
@SpringBootApplication
public class FirecontrolServerDemoApplication {
    // serverSocket对象
    private static ServerSocket serverSocket;

    private static Integer PORT = 11815;

    public static void main(String[] args) {
        SpringApplication.run(FirecontrolServerDemoApplication.class, args);
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("start...");		 // 创建一个线程池
            log.info("start...socket服务端口启动:{}",PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketThread socketDemo=new SocketThread(serverSocket);
        socketDemo.start();
    }

}
