package com.sunshine.print.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.sunshine.print.R;
import com.sunshine.print.adapter.CodeAdapter;
import com.sunshine.print.db.DBHelper;
import com.sunshine.print.event.ComEvent;
import com.sunshine.print.object.QrCode;
import com.sunshine.print.util.PrintLabel;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android_serialport_api.ComBean;
import android_serialport_api.SerialHelper;
import de.greenrobot.event.EventBus;

/*
* 串口服务类
* 这里可以控制串口的启动和串口数据的分发
* */
public class ComService extends Service {

    protected final static String TAG = ComService.class.getSimpleName();

    private final static String STR_BAUD_RATE1 = "9600";
    private final static String STR_BAUD_RATE2 = "115200";
    private final static String STR_DEV_ONE = "/dev/ttyS1";//打印机
    private final static String STR_DEV_TWO = "/dev/ttyS2";//打印机
    private final static String STR_DEV_THREE = "/dev/ttyS3";//扫码枪9600
    private final static String STR_DEV_FOUR = "/dev/ttyS4";//扫码枪115200
    SerialControl comOne, comTwo, comThree, comFour;
    private DBHelper mDbHelper;
    private boolean sun = false;
    private String twoCode, twoCode2;
    private String print;
    private CodeAdapter cadapter;
    private List<QrCode> qrcodeList = new ArrayList<>();
    private int print_num;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        super.onCreate();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventBackgroundThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_PRINT) {
            String code = event.getMessage();
            Log.e(TAG,code);
            Cursor cur2 = mDbHelper.exeSql("SELECT * FROM dateplan2 WHERE sn = '" + code + "'");
            sun = false;
            //查询一维码sn对应的信息
            if (cur2.moveToFirst()) {
                sun = true;
                do {
                    String sn = cur2.getString(cur2.getColumnIndex("sn"));
                    String pass = cur2.getString(cur2.getColumnIndex("pass"));
                    String mac = cur2.getString(cur2.getColumnIndex("mac"));
                    String pno = cur2.getString(cur2.getColumnIndex("pno"));
                    String enc = cur2.getString(cur2.getColumnIndex("enc"));
                    String date = cur2.getString(cur2.getColumnIndex("date"));
                    String des = cur2.getString(cur2.getColumnIndex("des"));
                    String key = cur2.getString(cur2.getColumnIndex("key"));
                    String type = cur2.getString(cur2.getColumnIndex("type"));
                    String version = cur2.getString(cur2.getColumnIndex("version"));
                    print = cur2.getString(cur2.getColumnIndex("print"));
                    twoCode = sn + pass + type + version;
                    twoCode = "{sn:"+sn  +",pwd:"+pass  +",type:"+type +",version:"+version+"}";
                   // twoCode2 = twoCode.substring(0, 11) + "····" + twoCode.substring(32, 40);
                    Log.d("CC", sn);
                    Log.d("CC", pass);
                    Log.d("CC", mac);
                    Log.d("CC", pno);
                    Log.d("CC", enc);
                    Log.d("CC", date);
                    Log.d("CC", des);
                    Log.d("CC", key);
                } while (cur2.moveToNext());
            }
            cur2.close();

            //如果查询为真
            if (sun&&print.equals("未打印")) {
                //打印机打印二维码
                twoCode = PrintLabel.ZebraLabel(twoCode);
                Log.e(TAG,twoCode);
                comOne.sendTxt(twoCode);
                comTwo.sendTxt(twoCode);

                //加载数据到显示屏上
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String date = sdf.format(new Date());
                QrCode qrcode = new QrCode();
                qrcode.setTime(date);
                qrcode.setCode(code);
                qrcode.setQrcode(twoCode);
                qrcode.setStatus(true);
                qrcodeList.add(qrcode);
                cadapter.mDatas = qrcodeList;
                cadapter.notifyDataSetChanged();

                //修改数据库的打印状态
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("print","已打印");
                database.update("dateplan2", values, "sn = ?", new String[] { code });

                print_num = print_num + 1;
                //修改数据库的打印状态
                SQLiteDatabase database2 = mDbHelper.getWritableDatabase();
                ContentValues values2 = new ContentValues();
                values2.put("printnum",String.valueOf(print_num));
                database2.update("num", values2, null, null);
                EventBus.getDefault().post(new ComEvent(print_num, ComEvent.ACTION_PRINT_NUM));

            } else if(print.equals("已打印")){
                Toast.makeText(this, "已打印！！！", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "二维码不存在", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "二维码不存在", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void initData() {
        comOne = new SerialControl();
        comTwo = new SerialControl();
        comThree = new SerialControl();
        comFour = new SerialControl();
        comOne.setPort(STR_DEV_ONE);
        comTwo.setPort(STR_DEV_TWO);
        comThree.setPort(STR_DEV_THREE);
        comFour.setPort(STR_DEV_FOUR);
        comOne.setBaudRate(STR_BAUD_RATE1);
        comTwo.setBaudRate(STR_BAUD_RATE1);
        comThree.setBaudRate(STR_BAUD_RATE1);//3号口9600
        comFour.setBaudRate(STR_BAUD_RATE2);//4号口115200
        openComPort(comOne);
        openComPort(comTwo);
        openComPort(comThree);
        openComPort(comFour);
    }

    public class SerialControl extends SerialHelper {
        public SerialControl() {
        }
        //从串口收取数据
        @Override
        public void onDataReceived(final ComBean comRecData) {
            if (comRecData.sComPort.equals(STR_DEV_ONE)) {//
                Log.e("onDataReceived bRec 1", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 1", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            } else if (comRecData.sComPort.equals(STR_DEV_TWO)) {//
                Log.e("onDataReceived bRec 2", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 2", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            } else if (comRecData.sComPort.equals(STR_DEV_THREE)) {//
                Log.e("onDataReceived bRec 3", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 3", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            }else if (comRecData.sComPort.equals(STR_DEV_FOUR)) {//
                Log.e("onDataReceived bRec 4", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 4", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            }
        }

        @Override
        public void stopSend() {
            Log.e(TAG, "stopSend()");
            super.stopSend();
        }


    }

    private void openComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
            showMessage(R.string.com_start_success);
        } catch (SecurityException e) {
            showMessage(R.string.com_start_fail_security);
        } catch (IOException e) {
            showMessage(R.string.com_start_fail_io);
        } catch (InvalidParameterException e) {
            showMessage(R.string.com_start_fail_param);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showMessage(int sMsg) {
        Toast.makeText(this.getApplicationContext(),
                getString(sMsg), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
