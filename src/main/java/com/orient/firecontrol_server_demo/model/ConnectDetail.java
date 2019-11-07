package com.orient.firecontrol_server_demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/12 15:13
 * @func 该类用来表示与socket连接的硬件设备地址信息(ip地址及端口号)
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)  //链式写法
public class ConnectDetail {
    private Integer id;
    private String ipaddr;
    private int port;
    private String boxcode; //与之对应的 监控箱编号

}
