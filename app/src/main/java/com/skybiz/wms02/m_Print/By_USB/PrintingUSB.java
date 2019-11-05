package com.skybiz.wms02.m_Print.By_USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.example.tscdll.TSCUSBActivity;
import com.example.tscdll.TscWifiActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by 7 on 13/12/2017.
 */

public class PrintingUSB {
   // Context c;
    private UsbAdmin mUsbAdmin=null;
    TSCUSBActivity TscUSB = new TSCUSBActivity();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static UsbManager mUsbManager;
    private static PendingIntent mPermissionIntent;
    private static boolean hasPermissionToCommunicate = false;
    private static UsbDevice device;

    int iw = 294; // scale width in pixels
    int ih = 188; // scale height in pixels
    int ix = 1; // coordinat x
    int iy = 2; // coordinat y
    String vTotal="1";
    String gapX="0";
    String gapY="0";


    IntentFilter filterAttached_and_Detached = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    // Catches intent indicating if the user grants permission to use the USB device
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true;
                        }
                    }
                }
            }
        }
    };
    public boolean testTSC(Context c, Bitmap bitmap, Integer total){
        mUsbManager = (UsbManager)c.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(c, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        c.registerReceiver(mUsbReceiver, filter);

        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("Detect ", deviceList.size()+" USB device(s) found");
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext())
        {
            device = deviceIterator.next();
            if(device.getVendorId() == 4611)
            {
                //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                break;
            }
        }



        //-----------start-----------
        PendingIntent mPermissionIntent;
        mPermissionIntent = PendingIntent.getBroadcast(c, 0,
                new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
        mUsbManager.requestPermission(device, mPermissionIntent);
        if(mUsbManager.hasPermission(device)) {
            //iw=iWidth;
            //ih=iHeight;
            //ix=iX;
            //iy=iY;
            //gapX=gapX;
            //gapY=gapY;
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, iw, ih, true);
            TscUSB.openport(mUsbManager,device);
            //TscUSB.setup(35, 25, 4, 4, 0, 0, 0);
            TscUSB.setup(35, 25, 2, 10, 0, 0, 0);
            TscUSB.clearbuffer();
            //TscUSB.sendcommand("SET TEAR ON\n");
            //TscUSB.sendcommand("SET COUNTER @1 1\n");
            //TscUSB.sendcommand("@1 = \"0001\"\n");
            //TscUSB.sendcommand("GAP "+gapX+","+gapY+"\r\n");
            TscUSB.sendcommand("GAP 1.95 mm,0 mm\r\n");
            TscUSB.sendcommand("DIRECTION 0\r\n");
            TscUSB.sendcommand("CLS\r\n");
            TscUSB.sendpicture(ix,iy,scaled);
            TscUSB.printlabel(1,total);
            //TscUSB.sendcommand("PRINT 1\r\n");
            TscUSB.closeport(500);
            return true;
        }else{
            return false;
        }
    }

    public boolean printTSC(Context c, String ComName,String CusName, String Address, String PrepackFactor, String Barcode){
        mUsbManager = (UsbManager)c.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(c, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        c.registerReceiver(mUsbReceiver, filter);

        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("Detect ", deviceList.size()+" USB device(s) found");
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext())
        {
            device = deviceIterator.next();
            if(device.getVendorId() == 4611)
            {
                //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                break;
            }
        }

        //-----------start-----------
        PendingIntent mPermissionIntent;
        mPermissionIntent = PendingIntent.getBroadcast(c, 0,
                new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
        mUsbManager.requestPermission(device, mPermissionIntent);
        if(mUsbManager.hasPermission(device)) {
            TscUSB.openport(mUsbManager,device);
            TscUSB.setup(35, 25, 2, 10, 0, 0, 0);
            TscUSB.clearbuffer();
            TscUSB.sendcommand("SIZE 35 mm,25 mm\r\n");
            TscUSB.sendcommand("GAP 2 mm,0 mm\r\n");
            TscUSB.sendcommand("DIRECTION 1 \r\n");
            TscUSB.sendcommand("CLS\r\n");
            TscUSB.sendcommand("TEXT 140, 35, \"2\", 0, 1, 1, 2, \""+ComName+"\"\r\n");
            //asli TscUSB.sendcommand("BLOCK 30, 60, 240, 80, \"0\", 0, 7, 7, \""+CusName+"\"\r\n");
            TscUSB.sendcommand("BLOCK 30, 60, 120, 40, \"0\", 0, 7, 7, \""+CusName+"\"\r\n");
            TscUSB.sendcommand("BLOCK 30, 60, 120, 40, \"0\", 0, 7, 7, \""+PrepackFactor+"\"\r\n");
            TscUSB.sendcommand("BLOCK 30, 60, 240, 80, \"0\", 0, 7, 7, \""+Address+"\"\r\n");
            TscUSB.barcode(60, 92, "128", 70, 2, 0, 2, 2, Barcode);
            TscUSB.sendcommand("PRINT 1, 1 \r\n");
            TscUSB.closeport(500);
            return true;
        }else{
            return false;
        }
    }


    public boolean printusbTsc(Context c, Bitmap bmp){
        try {
            openUsb2(c,bmp);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean printusbTsc2(Context c, Bitmap bmp){
        try {
            openUsb3(c,bmp);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean fnprintusb(Context c, String txtPrint){
        try {
            if(openUsb(c)){
                try{
                    printUsb(txtPrint);
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
    boolean openUsb(Context c) throws IOException {
        try {
            //Intent intentPrint = new Intent();
            mUsbAdmin=new UsbAdmin(c);
            mUsbAdmin.Openusb();
            if(!mUsbAdmin.GetUsbStatus()) {
                Log.e("RESULT", "Error Open USB");
                return false;
                //ic_return false;
            } else {
                Log.e("RESULT", "Success Open USB");
                return true;
                //ic_return true;
            }

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    boolean openUsb2(Context c,Bitmap bmp) throws IOException {
        try {
            final Bitmap fbmp=bmp;
            //Intent intentPrint = new Intent();
            mUsbAdmin=new UsbAdmin(c);
            mUsbAdmin.Openusb();
            if(!mUsbAdmin.GetUsbStatus()) {
                Log.e("RESULT", "Error Open USB");
                //return false;
                //ic_return false;
            } else {
                Log.e("RESULT", "Success Open USB");
                return true;
                //ic_return true;
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        printBmp2(fbmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1500);

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    boolean openUsb3(Context c,Bitmap bmp) throws IOException {
        try {
            final Bitmap fbmp=bmp;
            mUsbAdmin=new UsbAdmin(c);
            mUsbAdmin.Openusb();
            if(!mUsbAdmin.GetUsbStatus()) {
                Log.e("RESULT", "Error Open USB");
                //return false;
            } else {
                Log.e("RESULT", "Success Open USB");
                return true;
                //ic_return true;
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        printBmpTsc(fbmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1500);

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    boolean printUsb(String msg) throws IOException {
        try {
            if(!mUsbAdmin.sendCommand(msg.getBytes())) {
                Log.e("RESULT", "Error Print Data Sent");
            } else {
                Log.e("RESULT", "Success Print Data Sent");
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mUsbAdmin.sendCommand(SendCut);
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
    boolean printBmpTsc(Bitmap ibmp) throws IOException {
        try {
            int h = 248; // height in pixels
            int w = 354; // width in pixels
            Bitmap scaled = Bitmap.createScaledBitmap(ibmp, w, h, true);
           // byte[] command = Utils.decodeBitmap(scaled);
            //TscUSB.openport(c,mUsbAdmin.set);
            TscUSB.sendcommand("SIZE 3,1\r\n");
            TscUSB.sendcommand("GAP 0,0\r\n");
            TscUSB.sendcommand("CLS\r\n");
            TscUSB.sendcommand("TEXT 100,100,\"3\",0,1,1,\"12345689\"\r\n");
            TscUSB.sendcommand("PRINT 1\r\n");
            TscUSB.closeport(3000);
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    boolean printBmp2(Bitmap ibmp) throws IOException {
        try {
           /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ibmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);*/
            //ibmp.recycle();
            int h = 248; // height in pixels
            int w = 354; // width in pixels
            Bitmap scaled = Bitmap.createScaledBitmap(ibmp, w, h, true);
            //byte[] command = Utils.decodeBitmap(scaled);
            byte[] command = null;
            /*byte[] command= BytesUtil.getBytesFromBitMap(ibmp);*/
            if(!mUsbAdmin.sendCommand(command)) {
                Log.e("RESULT", "Error Print Data Sent");
            } else {
                String vline="\r\n\r\n";
                mUsbAdmin.sendCommand(vline.getBytes());
                Log.e("RESULT", "Success Print Data Sent");
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mUsbAdmin.sendCommand(SendCut);
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
