package ir.javadroid.sms_commander;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    public Integer Code = 0;
    //public String txtsms = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "sms received");
        Map<String, String> smsmap = getMessages(intent);
        for (String phone : smsmap.keySet()) {
            String msg = smsmap.get(phone);
            Log.e(TAG, "<" + phone + "> :\n" + msg + "\n");
            assert msg != null;

////  function check sms is run

            //اگر پیام استاتوس بود باشد


            if (msg.contains("LTIME")) {
                String[] comList = msg.split("#");
                ///  String com0 = comList[0];
                String deviceId = comList[0];
                String deviceStatus = comList[1];
                EventBus_SMS sms = new EventBus_SMS();
                sms.sender = phone;
                sms.message = msg;
                sms.extraDeviceName = deviceId;
                sms.extraDeviceStatus = deviceStatus;


                EventBus.getDefault().post(sms);
            }

            if (msg.contains("Status_") || msg.contains("status_")) { //sample: status_1_on     status_3_off
                String[] commandList = msg.split("_");
                String command = commandList[0];
                String deviceId = commandList[1];
                String deviceStatus = commandList[2];
                EventBus_SMS sms = new EventBus_SMS();
                sms.sender = phone;
                sms.message = msg;
                sms.extraDeviceName = deviceId;
                sms.extraDeviceStatus = deviceStatus;

                boolean deviceStatusBoolean = (deviceStatus.equalsIgnoreCase("on"));
                if (deviceId.equalsIgnoreCase("1")) {
                    HelperSharedPreferences.SaveBoolean("device_1", deviceStatusBoolean);
                } else if (deviceId.equalsIgnoreCase("2")) {
                    HelperSharedPreferences.SaveBoolean("device_2", deviceStatusBoolean);
                } else if (deviceId.equalsIgnoreCase("3")) {
                    HelperSharedPreferences.SaveBoolean("device_3", deviceStatusBoolean);
                } else if (deviceId.equalsIgnoreCase("4")) {
                    HelperSharedPreferences.SaveBoolean("eftTime", deviceStatusBoolean);
                    ///
                }


                EventBus.getDefault().post(sms);


            } else if (msg.contains("19") || msg.contains("COMEBACK")) {
                //voice
                //res -> rawfor vice file
                MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.car_alarm_1);
                mPlayer.start();
                ///
                /////////////
            } else {
                EventBus_SMS sms = new EventBus_SMS();
                sms.sender = phone;
                sms.message = msg;
                sms.extraDeviceName = "";
                sms.extraDeviceStatus = "";

                EventBus.getDefault().post(sms);
            }
        }

    }

    private Map<String, String> getMessages(Intent intent) {
        Map<String, String> map = new HashMap<>();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return map;
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return map;

        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            if (map.containsKey(messages[i].getDisplayOriginatingAddress())) {
                String body = map.get(messages[i].getDisplayOriginatingAddress());
                body += messages[i].getDisplayMessageBody();
                map.put(
                        messages[i].getDisplayOriginatingAddress(),
                        body
                );
            } else {
                map.put(

                        messages[i].getDisplayOriginatingAddress(),
                        messages[i].getDisplayMessageBody()
                );

            }
        }


        return map;
    }


}

