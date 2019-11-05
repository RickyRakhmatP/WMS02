package com.skybiz.wms02.m_Print.By_Wifi;

import android.content.Context;
import android.util.Log;

import com.example.tscdll.TscWifiActivity;

/**
 * Created by 7 on 12/12/2017.
 */

public class FnCheckWifi {
    String z;
    private Socketmanager mSockManager;
    TscWifiActivity tsc=new TscWifiActivity();

   public String fncheck(Context c, String IPAddress, int Port){
       mSockManager=new Socketmanager(c);
       mSockManager.mPort=Port;
       mSockManager.mstrIp=IPAddress;
       mSockManager.threadconnect();
       try {
           Thread.sleep(100);
           if (mSockManager.getIstate()) {

               z="success";
           }
           else {
              z="error";
           }
           Log.d("HASIL",z);
           return z;
       } catch (Exception e) {
           String errMsg = e.getMessage();
           Log.e("ERROR", errMsg);
           e.printStackTrace();

       }
       return z;
   }

    public String fnchecktsc(String IPAddress, int Port){
        try {
            tsc.openport(IPAddress, Port);
            z="success";
            Log.d("HASIL",z);
            return z;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();

        }
        return z;
    }


}
