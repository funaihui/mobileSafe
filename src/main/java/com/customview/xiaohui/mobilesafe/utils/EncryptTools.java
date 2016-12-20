package com.customview.xiaohui.mobilesafe.utils;

/**
 * Created by wizardev on 2016/12/19.
 */

public class EncryptTools {
    /**
     *
     * @param seed 种子文件
     * @param str 要加密的字符串
     * @return 加密后的文件
     */
    public static String encrypt(int seed,String str){
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= seed;
        }
        return new String(bytes);
    }

    /**
     *
     * @param seed 种子
     * @param str 要解密的字符串
     * @return 解密后的文件
     */
    public static String deciphery(int seed,String str){
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= seed;
        }
        return new String(bytes);
    }
}
