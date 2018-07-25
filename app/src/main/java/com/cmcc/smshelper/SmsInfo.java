package com.cmcc.smshelper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;



/**
 * Created by xuan on 2018/2/22.
 */
@Entity()
public class SmsInfo {

    @Id(autoincrement = true)
    private Long id;
    //0是发出去的，1为接收到的
    private int type;
    //0发送的短信,1二次确认的，2是订购成功，3是扣费内容,4订购失败
    private int receiverType;
    //0收到的短信,1,订购业务，2是二次确认
    private int sendType;
    private String content;
    private String sender;
    private String receiver;
    private String time;


    @Generated(hash = 1393069116)
    public SmsInfo(Long id, int type, int receiverType, int sendType,
            String content, String sender, String receiver, String time) {
        this.id = id;
        this.type = type;
        this.receiverType = receiverType;
        this.sendType = sendType;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }
    @Generated(hash = 1031826322)
    public SmsInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getReceiverType() {
        return this.receiverType;
    }
    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }
    public int getSendType() {
        return this.sendType;
    }
    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getSender() {
        return this.sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return this.receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }



}
