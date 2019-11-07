package com.orient.firecontrol_server_demo.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zhoudun
 * @Date: 2019/6/4 9:35
 * @Func:
 * @Version 1.0
 */
@Slf4j
public class SocketThread extends Thread {

    private ExecutorService mExecutorService;  									// 线程池
    private ServerSocket serverSocket;  										// serverSocket对象

    public SocketThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            mExecutorService=Executors.newCachedThreadPool();
            Socket client;  										 // 用来临时保存客户端连接的Socket对象
            while(true) {
                client = serverSocket.accept();							 // 接受客户度连接
                System.out.println(client.isBound());
//                client.setKeepAlive(true);
//                client.setSoTimeout(7*60*60*1000);							 // 设置该socket的超时时间，超过则捕获异常
                try {
                    client.sendUrgentData(0xFF);	//判断远端是否断开了连接

                } catch (IOException e1) {
                    log.error("有链接断开");

                    System.out.println("client is over");

                }
                mExecutorService.execute(new ThreadServer(client));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


}
