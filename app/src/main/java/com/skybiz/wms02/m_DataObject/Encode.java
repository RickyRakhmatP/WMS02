package com.skybiz.wms02.m_DataObject;

import java.io.UnsupportedEncodingException;

public class Encode {
    public static String setChar(String EncodeType, String Text){
        String newText="";
        try{
            byte[] b=Text.getBytes("ISO-8859-1");
            if(EncodeType.equals("UTF-8")) {
                newText = new String(b, "utf-8");
            }else if(EncodeType.equals("GBK")){
                newText = new String(b, "GBK");
            }
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
