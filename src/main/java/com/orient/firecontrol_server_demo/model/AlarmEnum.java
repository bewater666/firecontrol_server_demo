package com.orient.firecontrol_server_demo.model;

import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/15 10:23
 * @func
 */
public enum AlarmEnum {
    CONNECT_DEVICEERROR1("1","0","0","主机通信中断或设备异常"),
    REMAIN_ELECERROR1("1","1","3","主机剩余电流告警"),
    OVER_TEMP_ERRORONE1("1","2","1","主机过温一级告警"),
    OVER_TEMP_ERRORTWO1("1","3","2","主机过温二级告警"),
    OVER_TEMP_ERRORTHREE1("1","4","3","主机过温三级告警"),
    LESS_VOLTAGE_ERRORA1("1","5","3","主机A 相欠压告警"),
    LESS_VOLTAGE_ERRORB1("1","6","3","主机B 相欠压告警"),
    LESS_VOLTAGE_ERRORC1("1","7","3","主机C 相欠压告警"),
    OVER_VOLTAGE_ERRORA1("1","8","3","主机A 相过压告警"),
    OVER_VOLTAGE_ERRORB1("1","9","3","主机B 相过压告警"),
    OVER_VOLTAGE_ERRORC1("1","10","3","主机C 相过压告警"),

    CONNECT_DEVICEERROR2("2","0","0","单相子机通信中断或设备异常"),
    OVER_ELEC_ERRORONE2("2","1","1","单相子机过流一级告警"),
    OVER_ELEC_ERRORTWO2("2","2","2","单相子机过流二级告警"),
    OVER_ELEC_ERRORTHREE2("2","3","3","单相子机过流三级告警"),
    OVER_TEMP_ERRORONE2("2","4","1","单相子机过温一级告警"),
    OVER_TEMP_ERRORTWO2("2","5","2","单相子机过温二级告警"),
    OVER_TEMP_ERRORTHREE2("2","6","3","单相子机过温三级告警"),

    CONNECT_DEVICEERROR3("3","0","0","三相子机通信中断或设备异常"),
    OVER_ELEC_ERRORONEA3("3","1","1","三相子机A 相过流一级告警"),
    OVER_ELEC_ERRORTWOA3("3","2","2","三相子机A 相过流二级告警"),
    OVER_ELEC_ERRORTHREEA3("3","3","3","三相子机A 相过流三级告警"),
    OVER_ELEC_ERRORONEB3("3","4","1","三相子机B 相过流一级告警"),
    OVER_ELEC_ERRORTWOB3("3","5","2","三相子机B 相过流二级告警"),
    OVER_ELEC_ERRORTHREEB3("3","6","3","三相子机B 相过流三级告警"),
    OVER_ELEC_ERRORONEC3("3","7","1","三相子机C 相过流一级告警"),
    OVER_ELEC_ERRORTWOC3("3","8","2","三相子机C 相过流二级告警"),
    OVER_ELEC_ERRORTHREEC3("3","9","3","三相子机C 相过流三级告警"),
    OVER_TEMP_ERRORONEA3("3","10","1","三相子机A 相过温一级告警"),
    OVER_TEMP_ERRORTWOA3("3","11","2","三相子机A 相过温二级告警"),
    OVER_TEMP_ERRORTHREEA3("3","12","3","三相子机A 相过温三级告警"),
    OVER_TEMP_ERRORONEB3("3","13","1","三相子机B 相过温一级告警"),
    OVER_TEMP_ERRORTWOB3("3","14","2","三相子机B 相过温二级告警"),
    OVER_TEMP_ERRORTHREEB3("3","15","3","三相子机B 相过温三级告警"),
    OVER_TEMP_ERRORONEC3("3","16","1","三相子机C 相过温一级告警"),
    OVER_TEMP_ERRORTWOC3("3","17","2","三相子机C 相过温二级告警"),
    OVER_TEMP_ERRORTHREEC3("3","18","3","三相子机C 相过温三级告警");

    String deviceType;  //默认加了private final
    String alarmCode;
    String alarmGrade;
    String alarmDetail;

    private AlarmEnum(String deviceType,String alarmCode,String alarmGrade,String alarmDetail){
        this.deviceType = deviceType;
        this.alarmCode = alarmCode;
        this.alarmGrade = alarmGrade;
        this.alarmDetail = alarmDetail;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmGrade() {
        return alarmGrade;
    }

    public void setAlarmGrade(String alarmGrade) {
        this.alarmGrade = alarmGrade;
    }

    public String getAlarmDetail() {
        return alarmDetail;
    }

    public void setAlarmDetail(String alarmDetail) {
        this.alarmDetail = alarmDetail;
    }

//    private AlarmEnum find(String deviceType,String alarmCode){
//
//    }
}
