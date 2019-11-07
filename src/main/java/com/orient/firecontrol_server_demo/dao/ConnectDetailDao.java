package com.orient.firecontrol_server_demo.dao;

import com.orient.firecontrol_server_demo.model.ConnectDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/12 15:25
 * @func
 */
@Mapper
@Repository
public interface ConnectDetailDao {
    /**
     * 根据支路地址查找连接信息  因为连接信息可能会发生变更拿到后取最新的一条
     * @param boxcode
     * @return
     */
    List<ConnectDetail> queryByBoxCode(String boxcode);

    /**
     * 新增连接信息
     * @param connectDetail
     */
    void insert(ConnectDetail connectDetail);


    List<ConnectDetail> listAll();
}
