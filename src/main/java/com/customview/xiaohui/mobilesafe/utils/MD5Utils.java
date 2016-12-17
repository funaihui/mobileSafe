package com.customview.xiaohui.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wizardev on 2016/12/17.
 */

public class MD5Utils {
    public static String md5(String md5){
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.getBytes();
            byte[] digest = messageDigest.digest(md5Bytes);
            for (byte b:digest) {
                int byteHex = b & 0xff;
                String toHexString = Integer.toHexString(byteHex);
                if (toHexString.length() == 1){
                    toHexString = "0"+toHexString;
                }
                builder.append(toHexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
