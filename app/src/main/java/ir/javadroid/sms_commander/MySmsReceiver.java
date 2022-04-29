package ir.javadroid.sms_commander;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class MySmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    public Integer Code = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "sms received");
        Map<String, String> smsmap = getMessages(intent);
        for (String phone : smsmap.keySet()) {
            String msg = smsmap.get(phone);
            Log.e(TAG, "<" + phone + "> :\n" + msg + "\n");
            //telphone = msg + "  " + phone;
            assert msg != null;
           /* if (msg.contains("17") || msg.contains("18") || msg.contains("13") || msg.contains("14") || msg.contains("15")) {
                //context.startActivity(new Intent(context,MainActivity.class));
                Code = Integer.parseInt(msg);
                if (!MainActivity.isActivityMainUp) {
                    Intent i = new Intent(context, SmsGetterActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("phone", phone);
                    App.smsCode = Code;
                    context.startActivity(i);
                } else {
                    MainActivity.Recive(Code, MainActivity.imageCarOFF);
                }

            }*/
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


    private void getSmsFromIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            String text = "sms from " + messages[i].getDisplayOriginatingAddress() +
                    " :  " + messages[i].getMessageBody() + "\n";


            Log.i(TAG, text);
        }
    }

}
