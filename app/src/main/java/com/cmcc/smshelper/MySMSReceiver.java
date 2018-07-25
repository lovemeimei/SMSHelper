package com.cmcc.smshelper;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;


public class MySMSReceiver extends BroadcastReceiver {


    //处理返回的发送状态
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    PendingIntent sentPI = null;
    //处理返回的接收状态
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
    // create the deilverIntent parameter
    PendingIntent deliverPI = null;


    @Override
    public void onReceive(Context context, Intent intent) {


//        Toast.makeText(context,"aaa",Toast.LENGTH_SHORT).show();

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {


            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            sentPI = PendingIntent.getBroadcast(context, 0, sentIntent,
                    0);
            Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
            deliverPI = PendingIntent.getBroadcast(context, 0,
                    deliverIntent, 0);


            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            StringBuffer sb = new StringBuffer();
            String number = "";
            for (Object pdu : pdus) {
                //封装短信参数的对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                number = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                sb.append(body);

            }

            //0发送的短信,1二次确认的，2是订购成功，3是扣费内容,4订购失败

            Logger.d("接收到的号码：" + number + "接收到的短信内容：" + sb);
            int receiverType = 0;
            if (sb.toString().contains("请在24小时内回复“是”确认订购") || sb.toString().contains("输入密码确认点播")) {
                receiverType = 1;
            } else if (sb.toString().contains("如需退订，请发送0000到10086") || sb.toString().contains("次，由中国移动代收")) {
                receiverType = 2;
            } else if (sb.toString().contains("如需帮助，请咨询10086") || sb.toString().contains("如需帮助请咨询10086")) {
                receiverType = 3;
            } else if (sb.toString().contains("您未能成功订制")) {
                receiverType = 4;

            }

            //保存数据
            SmsInfo info = new SmsInfo(null, 1, receiverType, 0, sb.toString(), number, "self", DateUtil.getCurrentTime());

            if (receiverType == 1) {
                if (sb.toString().contains("请在24小时内回复“是”确认订购")) {
                    sendSMS(number, "是");

                } else if (sb.toString().contains("输入密码确认点播")) {
                    if (sb.toString().contains("，输")) {
                        String temp = sb.toString().substring(sb.toString().indexOf("本次密码") + 4, sb.toString().indexOf("，输"));
                        sendSMS(number, temp);
                        Logger.d(temp);

                    } else if (sb.toString().contains(",输")) {
                        String temp = sb.toString().substring(sb.toString().indexOf("本次密码") + 4, sb.toString().indexOf(",输"));
                        sendSMS(number, temp);
                        Logger.d(temp);

                    }

                }
            }


            SmsInfoUtil.Instance(context).addInfo(info);

        }else {
            Logger.d("aaaaabroadcast");

        }

    }

    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
        }
    }
}
