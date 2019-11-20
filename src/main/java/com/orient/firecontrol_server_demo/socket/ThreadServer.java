package com.orient.firecontrol_server_demo.socket;


import com.orient.firecontrol_server_demo.model.AlarmEnum;
import com.orient.firecontrol_server_demo.model.ConnectDetail;
import com.orient.firecontrol_server_demo.redis.JedisUtil;
import com.orient.firecontrol_server_demo.utils.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhoudun
 * @Date: 2019/6/4 9:37
 * @Func:
 * @Version 1.0
 */
@Slf4j
public class ThreadServer implements Runnable {


    private Socket client;                            //承载当前线程内的socketClient
    private BufferedReader mBufferedReader;        //套接字客户端发送流
    private PrintWriter mPrintWriter;                //套接字客户端输出流
    private String mStrMSG;                        //来自server的消息
    private BufferedInputStream in;
    private SocketManager socketManager;





    public ThreadServer() {
    }

    public ThreadServer(Socket client) {
        this.client = client;
        try {
            sendMessage("200");
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("获取客户端输入流错误:{}", e.getMessage());
        }
    }

    private void sendMessage(String str) {
        try {
            mPrintWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "utf-8"), true);// 设置向套接字客户端写入流的编码格式
            mPrintWriter.println(str);  //输出
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("回复客户端-设置输出流错误:{}", e.getMessage());
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



    @Override
    public void run() {
        try {
            while (true) {
                this.socketManager= BeanContext.getApplicationContext().getBean(SocketManager.class);
                //读取客户端数据
                BufferedInputStream input = new BufferedInputStream(client.getInputStream());
                byte[] buffer;
                buffer = new byte[10000]; //预设10000个数组大小  即10000个字节 eb代表一个字节
                if (buffer.length != 0) {
//                    System.out.println("length=" + buffer.length);
                    // 读取缓冲区
                    int read = input.read(buffer);
                    System.out.println("read的大小:"+read);
                    // 16进制数组转换字符串
                    String three = HexUtil.byteArrayToHexString(buffer, false);
                    String newThree = three.substring(0,read*2);
                    System.out.println("真正硬件的数据:"+newThree+"结束了");
                    System.out.println("转换成字符串后的大小"+newThree.length());
                    //此时要判断硬件过来的数据是否符合规范
                    if (newThree.startsWith("eb90eb9002")&&newThree.endsWith("03")){
                        //这时候可能会有多条正常数据连在一起发过来如:
                        //eb90eb90023201130001420041060100000117532652f2530000f4000100010325001c001e000000000000000100020207000000010003021c0000000100040204000000010005020900000003eb90eb900232011300011c00ff0601000001010001010100020101000301010004010100050103
                        //将报文长度高字节放在前面 所以这里需要颠倒一下
                        String length1 = newThree.substring(22, 24);
                        String length2 = newThree.substring(20, 22);
                        String length = length1+length2;
                        Integer realLen = Integer.parseInt(length, 16);
                        String thisData = newThree.substring(0, 20 + realLen * 2)+"03";//这是本次需要的数据
                        String restData = newThree.substring(22 + realLen * 2); //这次剩下来的数据
                        List<String> dataList = new ArrayList<>();
                        dataList.add(thisData);
                        for (int i = 0; i <restData.length();) {
                            String length3 = restData.substring(22+i, 24+i);
                            String length4 = restData.substring(20+i, 22+i);
                            String lengthd = length3+length4;
                            Integer len = Integer.parseInt(lengthd, 16);
                            String data = restData.substring(i, i+(20 + len * 2))+"03";
                            dataList.add(data);
                            i=i+data.length();
                        }
                        for (String trueData:
                        dataList) {
                            //ff指令→心跳包 eb90eb9002 3201110100 2000 ff 07 01000001 01000100 01000201 01000301 01000401 01000501 01001100 03
                            if (trueData.substring(24, 26).equals("ff")){
                                log.info("#####ff接收开始#####");
                                log.info("这是一条心跳包指令");
                                log.info("建筑物id==="+trueData.substring(10, 20));
                                log.info("一共发送了"+trueData.substring(27, 28)+"个数据包过来");
                                socketManager.sendFF(trueData);
                                log.info("已存入队列");
                                String data = trueData.substring(28, trueData.length() - 2);
                                System.out.println("当前设备监控箱编号==="+trueData.substring(10, 20)+data.substring(0, 4));
                                for (int i = 0; i <data.length(); ) {
                                    String datai = data.substring(i, i+8 );
                                    System.out.println("当前设备id==="+trueData.substring(10, 20)+datai.substring(0, 6));
                                    System.out.println("当前的设备状态码==="+datai.substring(6));
                                    System.out.println("当前设备状态==="+((datai.substring(6).equals("01"))?"正常":"中断"));
                                    i=i+8;
                                }
                                log.info("#####ff接收结束#####");
                                System.out.println(client);


                                SocketAddress remoteSocketAddress = client.getRemoteSocketAddress();
                                System.out.println("地址==="+remoteSocketAddress);
                                int port = client.getPort();
                                String ipAddr = client.getInetAddress().toString().substring(1);
                                String boxCode = trueData.substring(10, 20)+data.substring(0, 4);
                                // 清除可能存在的box socket
                                if (JedisUtil.exists(boxCode)) {
                                    JedisUtil.delKey(boxCode);
                                }
                                //将改socket保存起来
                                JedisUtil.setObject("socket", client.toString());
//                                ConnectDetail connectDetail = new ConnectDetail();
//                                connectDetail.setIpaddr(ipAddr).setPort(port).setBoxcode(boxCode);
//                                socketManager.insert(connectDetail);
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }
                            //40指令 事件值上送 eb90eb9002 3201110100 1000 40 01 0100 0201 0001 180824183618 03
                            if (trueData.substring(24, 26).equals("40")){
                                log.info("#####40接收开始#####");
                                log.warn("这是一条事件值上送指令");
                                //确认收到事件值上报 需要给硬件回复一条确认消息 只需要将状态码40改成06
                                String substring1 = trueData.substring(0, 24);
                                String substring2 = trueData.substring(26);
                                String realData = substring1+"06"+substring2;
                                byte[] bytes = HexUtil.hexStringToByteArray(realData);
                                sendMessage(bytes);
                                log.info("建筑物id==="+trueData.substring(10, 20));
                                log.info("一共发送了"+trueData.substring(27, 28)+"个数据包过来");
                                socketManager.send40(trueData);
                                log.info("已存入队列");
                                String data = trueData.substring(28, trueData.length() - 2);
                                for (int i = 0; i < data.length();) {
                                    String datai = data.substring(i, i+24 );
                                    System.out.println("当前监控箱编号==="+trueData.substring(10, 20)+datai.substring(0, 4));
                                    System.out.println("当前的设备id==="+trueData.substring(10, 20)+datai.substring(0, 6));
                                    System.out.println("当前设备类型==="+datai.substring(6, 8));
                                    System.out.println("告警时间===20"+datai.substring(12, 14)+"-"+datai.substring(14, 16)
                                            +"-"+datai.substring(16, 18)
                                            +" "+datai.substring(18, 20)+":"+datai.substring(20, 22)+":"+datai.substring(22, 24));
                                    System.out.println("告警码==="+datai.substring(8, 10));
                                    String deviceType = datai.substring(7, 8);
                                    String alarmCode = datai.substring(8, 10);
                                    if (alarmCode.substring(0, 1).equals("0")){//告警码第一位是0 直接去掉
                                        alarmCode = alarmCode.substring(1);
                                    }
                                    AlarmEnum[] values = AlarmEnum.values();
                                    String alarmDetail="";
                                    for (int j = 0; j <values.length ; j++) {
                                        if (values[j].getDeviceType().equals(deviceType)&&values[j].getAlarmCode().equals(alarmCode)){
                                            alarmDetail = values[j].getAlarmDetail();
                                        }
                                    }
                                    System.out.println("当前告警信息==="+alarmDetail);
                                    i=i+24;
                                }
                                log.info("#####40接收结束#####");
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }

                            //41指令 测量循环上送
                            //eb90eb9002
                            // 3201110100
                            // 5200 41 08
                            // 0200 0001 0500 0400 0500 0000 5d01   1设备类型 主机 下面有五项数据
                            // 0200 0103 0000 0e00 0000 4f01 5001 5001  3设备类型 三相子机 下面有6项数据
                            // 0200 0202 1600 5001  2设备类型 单项子机 下面有2项数据
                            // 0200 0302 0e01 5101
                            // 0200 0402 0600 4f01
                            // 0200 0502 df00 4d01
                            // 0200 0602 d402 5701
                            // 0200 0702 0000 5001 03
                            // TODO: 2019/11/7 这里做了修改 修改指令 在末尾加入当前时间戳 记录测量时间 为了画折线图看数据随时间变化
                            //理论上拼接了时间 报文长度也要做修改  但是后续开发不影响 所以这里不要改了
                            if (trueData.substring(24, 26).equals("41")||trueData.substring(24, 26).equals("42")){
                                log.info("######41接收开始#####");
                                log.info("这是测量值循环定时上送数据");
                                log.info("建筑物id==="+trueData.substring(10, 20));
                                log.info("一共发送了"+trueData.substring(27, 28)+"个数据包过来");
                                //记录测量时间 把当前收到的信息 当成测量时间
                                Date date = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String format = simpleDateFormat.format(date);
                                String lengthData = trueData.substring(22, 24)+trueData.substring(20, 22);
                                //将16进制数据 转成int数据 注意这里要乘以2 因为1=2个字符
                                Integer len = Integer.parseInt(lengthData, 16)*2;
                                //将当前时间戳拼接进去
                                String needData = trueData.substring(0,20+len)+format+"03";
                                //将需要的数据存入队列
                                socketManager.send41(needData);
                                log.info("已存入队列");
                                String data = trueData.substring(28, trueData.length() - 2);
                                for (int i = 0; i <data.length();) {
                                    String substring = data.substring(i + 7, i + 8);
                                    if (substring.equals("1")){
                                        System.out.println("***主机设备,有5项数据");
                                        //A相电压
                                        String voltageA1 = data.substring(i + 10, i + 12);
                                        String voltageA2 = data.substring(i + 8, i + 10);
                                        String vA = voltageA1+voltageA2;
                                        double voltageA = Integer.parseInt(vA, 16);
                                        System.out.println("A相电压为==="+voltageA/100+"V");
                                        //B相电压
                                        String voltageB1 = data.substring(i + 14, i + 16);
                                        String voltageB2 = data.substring(i + 12, i + 14);
                                        String vB = voltageB1+voltageB2;
                                        double voltageB = Integer.parseInt(vB, 16);
                                        System.out.println("B相电压为==="+voltageB/100+"V");
                                        //C相电压
                                        String voltageC1 = data.substring(i + 18, i + 20);
                                        String voltageC2 = data.substring(i + 16, i + 18);
                                        String vC = voltageC1+voltageC2;
                                        double voltageC = Integer.parseInt(vC, 16);
                                        System.out.println("C相电压为==="+voltageC/100+"V");
                                        //剩余电流
                                        String remainElec1 = data.substring(i + 22, i + 24);
                                        String remainElec2 = data.substring(i + 20, i + 22);
                                        String E = remainElec1+remainElec2;
                                        double remainElec = Integer.parseInt(E, 16);
                                        System.out.println("剩余电流为==="+remainElec/10+"mA");
                                        //配电箱温度
                                        String boxTemp1 = data.substring(i + 26, i + 28);
                                        String boxTemp2 = data.substring(i + 24, i + 26);
                                        String T = boxTemp1+boxTemp2;
                                        double boxTemp = Integer.parseInt(T, 16);
                                        System.out.println("配电箱环境温度为==="+boxTemp/10+"℃");
                                        i= i+4*7;
                                    }
                                    if (substring.equals("2")){
                                        System.out.println("***单项子机设备,有2项数据");
                                        //支路电流
                                        String branchElec1 = data.substring(i + 10, i + 12);
                                        String branchElec2 = data.substring(i + 8, i + 10);
                                        String E = branchElec1+branchElec2;
                                        double branchElec = Integer.parseInt(E, 16);
                                        System.out.println("支路电流==="+branchElec/100+"mA");
                                        //支路接头温度
                                        String branchTemp1 = data.substring(i + 14, i + 16);
                                        String branchTemp2 = data.substring(i + 12, i + 14);
                                        String T = branchTemp1+branchTemp2;
                                        double branchTemp = Integer.parseInt(T, 16);
                                        System.out.println("支路接头温度==="+branchTemp/10+"℃");
                                        i= i+4*4;
                                    }
                                    if (substring.equals("3")){
                                        System.out.println("***三相子机设备,有6项数据");
                                        //支路 A 相电流
                                        String branchElecA1 = data.substring(i + 10, i + 12);
                                        String branchElecA2 = data.substring(i + 8, i + 10);
                                        String AE = branchElecA1+branchElecA2;
                                        double branchElecA = Integer.parseInt(AE, 16);
                                        System.out.println("支路A相电流==="+branchElecA/100+"mA");
                                        //支路 B 相电流
                                        String branchElecB1 = data.substring(i + 14, i + 16);
                                        String branchElecB2 = data.substring(i + 12, i + 14);
                                        String BE = branchElecB1+branchElecB2;
                                        double branchElecB = Integer.parseInt(BE, 16);
                                        System.out.println("支路B相电流==="+branchElecB/100+"mA");
                                        //支路 C 相电流
                                        String branchElecC1 = data.substring(i + 18, i + 20);
                                        String branchElecC2 = data.substring(i + 16, i + 18);
                                        String CE = branchElecC1+branchElecC2;
                                        double branchElecC = Integer.parseInt(CE, 16);
                                        System.out.println("支路C相电流==="+branchElecC/100+"mA");
                                        //支路 A 相接头温度
                                        String branchTempA1 = data.substring(i + 22, i + 24);
                                        String branchTempA2 = data.substring(i + 20, i + 22);
                                        String AT = branchTempA1+branchTempA2;
                                        double branchTempA = Integer.parseInt(AT, 16);
                                        System.out.println("支路 A 相接头温度==="+branchTempA/10+"℃");
                                        //支路 B 相接头温度
                                        String branchTempB1 = data.substring(i + 26, i + 28);
                                        String branchTempB2 = data.substring(i + 24, i + 26);
                                        String BT = branchTempB1+branchTempB2;
                                        double branchTempB = Integer.parseInt(BT, 16);
                                        System.out.println("支路 B 相接头温度==="+branchTempB/10+"℃");
                                        //支路 C 相接头温度
                                        String branchTempC1 = data.substring(i + 26, i + 28);
                                        String branchTempC2 = data.substring(i + 24, i + 26);
                                        String CT = branchTempC1+branchTempC2;
                                        double branchTempC = Integer.parseInt(CT, 16);
                                        System.out.println("支路 C 相接头温度==="+branchTempC/10+"℃");
                                        i= i+4*8;
                                    }
                                }
                                //eb90eb9002 3201110100 0800 42 01 0200 0502 a100 4d01 03
                                log.info("#####41接收结束#####");
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }

                            //控制结果上送
                            //eb90eb90023201110100 0c00 51 01 0200 0101 03
                            if (trueData.substring(24, 26).equals("51")){
                                log.warn("#####51指令开始接收#####");
                                log.warn("这是控制结果上传数据");
                                socketManager.send51(trueData);
                                log.info("已存入队列");
                                String result = trueData.substring(34, 36);
                                if (result.equals("01")){
                                    System.out.println("断开支路成功");
                                }
                                if (result.equals("81")){
                                    System.out.println("断开支路失败");
                                }
                                if (result.equals("02")){
                                    System.out.println("支路合上成功");
                                }
                                if (result.equals("82")){
                                    System.out.println("支路合上失败");
                                }
                                log.warn("#####51指令接收结束");
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }

                            //对时指令
                            //eb90eb9002 ffffffffff 0b00 60 01 ffff 191010171703 03
                            //当对时指令时 省市区建筑物监控箱编号都为ff
                            if (trueData.substring(24, 26).equals("60")){
                                log.info("#####60指令接收开始#####");
                                log.info("这是一条对时指令");
                                socketManager.send60(trueData);
                                log.info("已存入队列");
                                System.out.println("对时时间为===20"+trueData.substring(32, 34)+"-"+trueData.substring(34, 36)
                                        +"-"+trueData.substring(36, 38)+" "+trueData.substring(38, 40)+":"+trueData.substring(40, 42)
                                        +":"+trueData.substring(42, 44));
                                log.info("######60指令接收结束#####");
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }
                        }

                        








                    }else {
                        log.error("硬件传来的数据不符合规范");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("socket closed") || "Connection reset".equals(e.getMessage().toString()) || "Read timed out".equals(e.getMessage().toString())) {
                try {
                    this.client.close();
                    log.info("服务端关闭断开的socket链接");
                } catch (IOException e1) {
                    log.info("服务端关闭socket异常");
                    e1.printStackTrace();
                }
            } else {
                log.error("有链接断开");
            }
        } finally {
            System.out.println("读取数据结束client is over");
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
