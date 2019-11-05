package com.skybiz.wms02.m_Print.By_Wifi;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by 7 on 13/12/2017.
 */

public class PrintingWifi {
    private Socketmanager mSockManager;
    public boolean fnprintwifi(Context c, String IPAddress, int Port, String txtPrint, String FontSize){
        try {
            if(connWifi(c,IPAddress,Port)){
               try{
                   printWifiM(txtPrint,FontSize);
               } catch (IOException e) {
                   Log.e("ERROR PRINT", e.getMessage());
                   e.printStackTrace();
               }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean fnprintwifi2(Context c, String IPAddress, int Port, String txtPrint){
        try {
            if(connWifi(c,IPAddress,Port)){
                try{
                    printWifi2(txtPrint);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    boolean connWifi(Context c, String IPAddress, int Port) throws IOException {
        mSockManager=new Socketmanager(c);
        mSockManager.mPort=Port;
        mSockManager.mstrIp=IPAddress;
        mSockManager.threadconnect();
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                Log.e("RESULT", "Success Open Connection");
                //callbackContext.success("Success Open Connection" +msg);
            }
            else {
                Log.e("RESULT", "Error Open Connection");
                //callbackContext.success("Error Open Connection" +msg);
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }
    boolean printWifi(String msg) throws IOException {
        mSockManager.threadconnectwrite(msg.getBytes("GBK"),"Standard");
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mSockManager.threadconnectwrite(SendCut,"Standard");
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //callbackContext.success("Error Print via Wifi");
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    boolean printWifiM(String msg, String FontSize) throws IOException {
        byte[] fs=new byte[]{29,42,60};
        byte[] bb2=new byte[]{0x1B,0x21,0x00};
        //mSockManager.threadconnectwrite(fs);
        //mSockManager.threadconnectwrite(bb2);
        mSockManager.threadconnectwrite(msg.getBytes("GBK"),FontSize);
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mSockManager.threadconnectwrite(SendCut,FontSize);
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //callbackContext.success("Error Print via Wifi");
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    boolean printWifi2(String msg) throws IOException {
        try {
            Thread.sleep(8000);
            if (mSockManager.getIstate()) {
                String[] address=msg.split("cutpaper");
                for (String add: address) {
                    Log.d("PRINT TEXT",add);
                    //mSockManager.threadconnectwrite(add.getBytes("GBK"));
                    //byte SendCut[] = {0x0a, 0x0a, 0x1d, 0x56, 0x01};
                    //mSockManager.threadconnectwrite(SendCut);
                    byte[] a = add.getBytes("GBK");
                    byte[] b = {0x0a,0x0a,0x1d,0x56,0x01};
                    byte[] c = new byte[a.length + b.length];
                    System.arraycopy(a, 0, c, 0, a.length);
                    System.arraycopy(b, 0, c, a.length, b.length);
                    mSockManager.threadconnectwrite(c,"Standard");
               /* byte[] combined = new byte[one.length + two.length];
                System.arraycopy(one,0,combined,0         ,one.length);
                System.arraycopy(two,0,combined,one.length,two.length);
                mSockManager.threadconnectwrite(combined);*/
                }
                //return true;
                //byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
               // mSockManager.threadconnectwrite(SendCut);
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //return false;

            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    boolean printWifiB(String msg) throws IOException {
        boolean bold=true;
        byte[] cmd=new byte[]{0x1B,0x21,bold?(byte)1:0};
        mSockManager.threadconnectwrite(cmd,"Standard");
        mSockManager.threadconnectwrite(msg.getBytes("GBK"),"Standard");
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                //byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
               // mSockManager.threadconnectwrite(SendCut);
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //callbackContext.success("Error Print via Wifi");
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }
}
//https://github.com/imrankst1221/Thermal-Printer-in-Android
//https://github.com/pradeepksingh/Android-USB-printer/blob/master/com/pradeep/usbprinter/MainActivity.java