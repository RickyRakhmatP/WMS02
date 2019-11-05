package com.skybiz.wms02.m_Print.By_Wifi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.tscdll.TscWifiActivity;
import com.skybiz.wms02.m_Database.Local.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PrintingWifiTsc {
    TscWifiActivity TscEthernetDll = new TscWifiActivity();

    //String CusName1="",CusName2="",CusName3="",Address1="",Address2="",Address3="",Address4="";

    public boolean printwifiTsc(Context c, String IPAddress, String Port, Bitmap bitmap){
        try {
            if(connWifiTsc(IPAddress,Port)){
                try{
                    final Bitmap bmp=bitmap;
                    printbmp(bmp);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean printwifiTsc2(Context c, final String IPAddress, final String Port, final String ComName,
                                 final String CusName, final String Address, final String PrepackFactor,
                                 final String Barcode){
        try {
            if(connWifiTsc(IPAddress,Port)){
                try{
                    //final String toPrint=strPrint;
                    //final int Qty=iQty;
                    printText(ComName,CusName,Address,PrepackFactor,Barcode);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean printInvLabel(Context c, final String IPAddress, final String Port, final String Doc1No, final String UserCode){
        try {
            if(connWifiTsc(IPAddress,Port)){
                try{
                    printLabel1(Doc1No,UserCode);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean printwifiTsc3(Context c, final String IPAddress, final String Port, final String Doc1No,
                                 final String CusName, final String Address, final String PrepackFactor, final String D_ate,
                                 final String CusName1,final String CusName2,final String Address1, final String Address2,
                                 final String Address3, final String Address4){
        try {
            /*DBAdapter db=new DBAdapter(c);
            db.openDB();
            String queryDum="select CusName1,CusName2,CusName3," +
                    "Address1,Address2,Address3," +
                    "Address4 from tb_dumprint2 limit 1";
            Cursor rsDum=db.getQuery(queryDum);
            while(rsDum.moveToNext()){
                CusName1=rsDum.getString(0);
                CusName2=rsDum.getString(1);
                CusName3=rsDum.getString(2);
                Address1=rsDum.getString(3);
                Address2=rsDum.getString(4);
                Address3=rsDum.getString(5);
                Address4=rsDum.getString(6);
            }
            rsDum.close();
            db.closeDB();*/
            if(connWifiTsc(IPAddress,Port)){
                try{
                    printText3(Doc1No,CusName,Address,
                            PrepackFactor, D_ate, CusName1,
                            CusName2,Address1,Address2,
                            Address3,Address4);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    boolean connWifiTsc(String IPAddress, String Port) throws IOException {
        int port= Integer.parseInt(Port);
        try {
            TscEthernetDll.openport(IPAddress, port);
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    boolean printbmp(Bitmap bmp) throws IOException {
        try {
            //TscEthernetDll.setup("80", "38", "4", "15", "0", "3", "0");
            TscEthernetDll.setup(35, 25, 4, 4, 0, 0, 0);
            TscEthernetDll.clearbuffer();
            TscEthernetDll.sendcommand("SET TEAR ON\n");
            TscEthernetDll.sendcommand("SET COUNTER @1 1\n");
            TscEthernetDll.sendcommand("@1 = \"0001\"\n");
            TscEthernetDll.sendbitmap(35,25,bmp);
            TscEthernetDll.printlabel(1, 1);
            TscEthernetDll.closeport(3500);
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    boolean printText(String ComName,String CusName, String Address, String PrepackFactor, String Barcode) throws IOException {
        try {
            //TscEthernetDll.setup("80", "38", "4", "15", "0", "3", "0");
            TscEthernetDll.setup(35, 25, 4, 4, 0, 0, 0);
            TscEthernetDll.clearbuffer();
            TscEthernetDll.sendcommand("SET TEAR ON\n");
            TscEthernetDll.sendcommand("SET COUNTER @1 1\n");
            TscEthernetDll.sendcommand("@1 = \"0001\"\n");
            //TscEthernetDll.set(35,25,bmp);
           /* TscEthernetDll.sendcommand("BLOCK 12,8,320,230, \"3\",0,1,1,\""+strPrint+"\"\r\n");
            // TscUSB.printlabel(1,1);
            TscEthernetDll.sendcommand("PRINT 1,"+iQty+"\r\n");*/
            TscEthernetDll.sendcommand("TEXT 140, 35, \"2\", 0, 1, 1, 2, \""+ComName+"\"\r\n");
            //asli TscUSB.sendcommand("BLOCK 30, 60, 240, 80, \"0\", 0, 7, 7, \""+CusName+"\"\r\n");
            TscEthernetDll.sendcommand("BLOCK 30, 60, 120, 40, \"0\", 0, 7, 7, \""+CusName+"\"\r\n");
            TscEthernetDll.sendcommand("BLOCK 30, 60, 120, 40, \"0\", 0, 7, 7, \""+PrepackFactor+"\"\r\n");
            TscEthernetDll.sendcommand("BLOCK 30, 60, 240, 80, \"0\", 0, 7, 7, \""+Address+"\"\r\n");
            TscEthernetDll.barcode(60, 92, "128", 70, 2, 0, 2, 2, Barcode);
            TscEthernetDll.sendcommand("PRINT 1, 1 \r\n");
            TscEthernetDll.closeport(3500);
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }


    boolean printLabel1(String Doc1No,  String UserCode) throws IOException {
        try {
            TscEthernetDll.setup(35, 25, 4, 4, 0, 0, 0);
            TscEthernetDll.clearbuffer();
            TscEthernetDll.sendcommand("SIZE 35 mm,25 mm\r\n");
            TscEthernetDll.sendcommand("GAP 4 mm,0 mm\r\n");
            TscEthernetDll.sendcommand("DIRECTION 1 \r\n");
            TscEthernetDll.sendcommand("CLS\r\n");
            TscEthernetDll.barcode(35, 30, "128", 80, 2, 0, 2, 2, Doc1No);
            TscEthernetDll.sendcommand("TEXT 150, 160, \"2\", 0, 1, 1, 2, \"" + UserCode + "\"\r\n");
            TscEthernetDll.sendcommand("PRINT 1, 1 \r\n");
            TscEthernetDll.closeport(500);
           /* JSONArray ja    = new JSONArray(jsonData);
            JSONObject jo   = null;
            for (int i = 0; i < ja.length(); i++) {
                jo              = ja.getJSONObject(i);
                String UserCode = jo.getString("UserCode");
                String Doc1No   = jo.getString("Doc1No");
                TscEthernetDll.barcode(30, 30, "128", 80, 2, 0, 2, 2, Doc1No);
                TscEthernetDll.sendcommand("TEXT 150, 160, \"2\", 0, 1, 1, 2, \"" + UserCode + "\"\r\n");
                TscEthernetDll.sendcommand("PRINT 1, 1 \r\n");
            }*/

            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }



    boolean printText3(String Doc1No,String CusName, String vAddress,
                       String PrepackFactor, String D_ate, String CusName1,
                       String CusName2, String Address1, String Address2,
                       String Address3, String Address4) throws IOException {
        try {
            TscEthernetDll.setup(60, 40, 4, 4, 0, 0, 0);
            TscEthernetDll.clearbuffer();
            TscEthernetDll.sendcommand("SIZE 60 mm,40 mm\r\n");
            TscEthernetDll.sendcommand("GAP 4 mm,0 mm\r\n");
            TscEthernetDll.sendcommand("DIRECTION 1 \r\n");
            TscEthernetDll.sendcommand("CLS\r\n");
            //TscEthernetDll.sendcommand("TEXT 220, 30, \"2\", 0, 1, 1, 2, \""+CusName+"\"\r\n");
            //Log.d("ADDRESS ORI",vAddress);
           /* String newAddress = vAddress.substring(0, Math.min(vAddress.length(), 100));
            String AddressAll = newAddress.replaceAll("(.{31})", "$1;");
            String [] address=AddressAll.split(";");
            String tAddress="";
            int height=60;
            for (String add: address) {
                tAddress =str_pad(add, 31, " ", "STR_PAD_BOTH");
                //Log.d("ADDRESS",tAddress);
                TscEthernetDll.sendcommand("TEXT 10, "+height+", \"2\", 0, 1, 1, 0, \""+tAddress.trim()+"\"\r\n");
                Log.d("ADDRESS",tAddress.trim());
                //TscEthernetDll.sendcommand("CLS\r\n");
                height += height+2;
            }*/
            //TscEthernetDll.sendcommand("TEXT 10, 60, \"1\", 0, 1, 1, 0, \""+vAddress+"\"\r\n");
            //TscEthernetDll.sendcommand("BLOCK 30, 60, 380, 80, \"0\", 0, 7, 7, \""+Address+"\"\r\n");
            Log.d("ADDRESS FROM DUM",Address1+" "+Address2+" "+Address3+" "+Address4);
            TscEthernetDll.sendcommand("TEXT 15, 20, \"3\", 0, 1, 1, 0, \""+CusName1+"\"\r\n");
            TscEthernetDll.sendcommand("TEXT 15, 45, \"3\", 0, 1, 1, 0, \""+CusName2+"\"\r\n");
          //  TscEthernetDll.sendcommand("TEXT 7, 60, \"2\", 0, 1, 1, 0, \""+CusName3+"\"\r\n");
            TscEthernetDll.sendcommand("TEXT 15, 90, \"2\", 0, 1, 1, 0, \""+Address1+"\"\r\n");
            TscEthernetDll.sendcommand("TEXT 15, 110, \"2\", 0, 1, 1, 0, \""+Address2+"\"\r\n");
            TscEthernetDll.sendcommand("TEXT 15, 130, \"2\", 0, 1, 1, 0, \""+Address3+"\"\r\n");
            TscEthernetDll.sendcommand("TEXT 15, 150, \"2\", 0, 1, 1, 0, \""+Address4+"\"\r\n");
            TscEthernetDll.barcode(100, 190, "128", 70, 2, 0, 2, 2, Doc1No);
            TscEthernetDll.sendcommand("TEXT 210, 290, \"2\", 0, 1, 1, 2, \""+D_ate+" ["+PrepackFactor +"]\"\r\n");
            TscEthernetDll.sendcommand("PRINT 1, 1 \r\n");
            TscEthernetDll.closeport(500);
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    public String str_pad(String input, int length, String pad, String    sense) {
        int resto_pad = length - input.length();
        String padded = "";
        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT")) {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        } else if(sense.equals("STR_PAD_LEFT")) {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        } else {
            int pad_left  = (int) Math.ceil(resto_pad/2);
            int pad_right = resto_pad - pad_left;

            padded  = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        return padded;
    }

    protected String _fill_string(String pad, int resto ) {
        boolean first = true;
        String padded = "";
        if (resto >= pad.length()) {
            for (int i = resto; i >= 0; i = i - pad.length()) {
                if (i  >= pad.length()) {
                    if (first){ padded = pad; } else { padded += pad; }
                } else {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        } else {
            padded = pad.substring(0,resto);
        }
        return padded;
    }
}
