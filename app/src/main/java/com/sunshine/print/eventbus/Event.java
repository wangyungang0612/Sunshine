package com.sunshine.print.eventbus;

import android.widget.VideoView;

/**
 * Created by wangyungang
 * on 2016/6/10.
 */
public class Event {
    public static final int SPINNER1 = 5;
    public static final int SPINNER2 = 6;
    public static final int CODE_TWO = 7;
    public static final int CODE_R3 = 8;
    public static final int RFID = 9;
    public static final int SCAN_MATERIAL = 10;
    public static final int PLAY_VIDEO = 11;
    public static final int SCAN_EAN = 12;
    public final static int ACTION_SEND_PCI = 13;  //
    public final static int ACTION_GET_PCI = 16;  //
    public static final int ACTION_SEND_SMQ = 17;
    public final static int ACTION_SHOWPIC = 100;
    public final static int ACTION_SHOWPIC_NULL = 101;
    public static final int RFID2 = 1;
    protected String message;
    protected int actionType;
    public VideoView vv;
    protected String boschtype;
    protected String edit_code;
    protected String planInfo;
    protected String mideaCode;
    private byte[] bytes;

    public Event(int actionType) {
        this.actionType = actionType;
    }

    public Event(String message, int actionType) {
        this.message = message;
        this.actionType = actionType;
    }

    public Event(byte[] message, int actionType){
        this.bytes = message;
        this.actionType = actionType;
    }

    public Event(String message, int actionType, VideoView vv) {
        this.message = message;
        this.actionType = actionType;
        this.vv = vv;
    }

    public Event(String message, String boschtype, String planInfo, String mideaCode, int actionType) {
        this.message = message;
        this.boschtype = boschtype;
        this.planInfo = planInfo;
        this.actionType = actionType;
        this.mideaCode = mideaCode;
    }

    public int getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }

    public VideoView getVideoView() {
        return vv;
    }

    public String getBoschtype() {
        return boschtype;
    }

    public String getPlanInfo() {
        return planInfo;
    }

    public String getMideaCode() {
        return mideaCode;
    }

    public byte[] getBytes(){
        return bytes;
    }
}
