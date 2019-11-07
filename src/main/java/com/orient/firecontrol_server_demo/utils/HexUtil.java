package com.orient.firecontrol_server_demo.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/9/9 10:16
 * @func
 */
public class HexUtil {
    /**
     * 十六进制字符
     */
    private static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};



    /**
     * @func  16进制字节数组转16进制字符串
     * @param bytes
     * @param toUpper   是否需要转大写
     * @return
     */
    public static String byteArrayToHexString(byte[] bytes, boolean toUpper) {
        if (ArrayUtils.isNotEmpty(bytes)) {
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                String hexStr = Integer.toHexString(b & 0xFF);
                if (hexStr.length() < 2) {
                    builder.append(0);
                }
                builder.append(hexStr);
            }
            return toUpper ? builder.toString().toUpperCase() : builder.toString();
        }
        return null;
    }

    /**
     * 十六进制字符串转字节数组
     * @param hexStr 十六进制字符串
     * @return
     */
    public static byte[] hexStringToByteArray(String hexStr) {
        byte[] bytes = null;
        if (isHexStr(hexStr)) {
            int length = hexStr.length() / 2;
            bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes[i] = (byte)Integer.parseInt(hexStr.substring(i*2, (i+1) * 2), 16);
            }
        }
        return bytes;
    }


    /**
     * 判断是否为十六进制字符
     * @param c 十六进制字符
     * @return
     */
    public static boolean isHexChar(char c) {
        for (char hChar : hexChars) {
            if (c == hChar) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为十六进制字符串
     * @param hexStr 十六进制字符串
     * @return
     */
    public static boolean isHexStr(String hexStr) {
        if (StringUtils.isNotEmpty(hexStr)) {
            char[] hexChars = hexStr.toCharArray();
            if (hexChars.length % 2 != 0) {
                System.out.println(hexStr + "[奇数位十六进制字符串]");
                return false;
            }
            for (char hexChar : hexChars) {
                if (!isHexChar(hexChar)) {
                    return false;
                }
            }
        }
        return true;
    }


}
