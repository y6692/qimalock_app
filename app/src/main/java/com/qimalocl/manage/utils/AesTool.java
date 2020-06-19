package com.qimalocl.manage.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 蓝牙密钥工具
 * @author Leon
 * 2018�?3�?16�? 上午11:05:28
 */

public class AesTool {
	/**
	 * 获取密钥
	 * @param keyStr
	 * @param tid
	 * @return
	 */
    public static String Genkey(String keyStr, String tid) {
        JavaAesContent aescxt = new JavaAesContent();
        byte[] key = null;
        byte[] data = new byte[32];
        try {
            byte[] temp = tid.getBytes("ascii");
            for(int i = 0; i < temp.length; i++)
                data[i] = temp[i];
            key = keyStr.getBytes("ascii");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int[] keyi = new int[key.length];
        for(int i = 0; i < keyi.length;i++)
        {
            keyi[i] = key[i] >= 0 ? key[i] : 256 + key[i];
        }
        int[] datai = new int[data.length];
        for(int i = 0; i < datai.length;i++)
        {
            datai[i] = data[i] >= 0 ? data[i] : 256 + data[i];
        }
        aescxt.setKey(keyi, keyi.length);
        int[] out = new int[16];
        JavaAes.AesDecrypt(datai, out, aescxt);
        List<Byte> byteList = new ArrayList<>();
        for(int i = 0; i < out.length; i++)
            byteList.add((byte) out[i]);
        int[] newdata = Arrays.copyOfRange(datai, 16, datai.length);
        JavaAes.AesDecrypt(newdata, out, aescxt);
        for(int i = 0; i < out.length; i++)
            byteList.add((byte) out[i]);
        int length = byteList.size();
        byte[] bytes = new byte[length];
        for (int j = 0; j < length; j++) {
            bytes[j] = byteList.get(j);
        }

        String gened = bytesToHexString(bytes);
        return gened;
    }

    private static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X ", b));
        }
        return builder.toString();
    }
    
    public static void main(String[] args){
    	System.out.println(Genkey("test","008600227"));
    }
}
