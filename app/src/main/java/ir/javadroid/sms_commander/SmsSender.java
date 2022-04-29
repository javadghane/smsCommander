package ir.javadroid.sms_commander;


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SmsSender extends IntentService {
    private static final String TAG = "SmsSender";
    public static final String EXTRA_PHONE = "extra.phone";
    public static final String EXTRA_MSG = "extra.msg";

    public static final String INTENT_SENT_MESSAGE = "smssender.message.sent";
    public static final String INTENT_DELIVERED_MESSAGE = "smssender.message.delivered";

    public SmsSender() {
        super("SmsSender");
    }

    static class IDGenerator {
        public static AtomicInteger counter = new AtomicInteger();

        public static int nextValue() {
            return counter.getAndIncrement();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        String phone = intent.getStringExtra(SmsSender.EXTRA_PHONE);
        String msg = intent.getStringExtra(SmsSender.EXTRA_MSG);
        sendSms(phone, msg);
        Log.i(TAG, "sms sent to " + phone);
    }

    private void sendSms(String phone, String msg) {
        Toast.makeText(getApplicationContext(), "sendSms", Toast.LENGTH_SHORT).show();

        SmsManager sm = SmsManager.getDefault();
        ArrayList<String> parts = sm.divideMessage(msg);
        PendingIntent sentPI = PendingIntent.getBroadcast(
                this,
                IDGenerator.nextValue(),
                new Intent(SmsSender.INTENT_SENT_MESSAGE),
                PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(
                this,
                IDGenerator.nextValue(),
                new Intent(SmsSender.INTENT_DELIVERED_MESSAGE),
                PendingIntent.FLAG_CANCEL_CURRENT);


        if (parts.size() > 1) { //multipart
            ArrayList<PendingIntent> sentPIs = new ArrayList<>();
            ArrayList<PendingIntent> deliveredPIs = new ArrayList<>();
            for (String part : parts) {
                sentPIs.add(sentPI);
                deliveredPIs.add(deliveredPI);
            }
            sm.sendMultipartTextMessage(phone, null, parts, sentPIs, deliveredPIs);
        } else {
            sm.sendTextMessage(phone, null, parts.get(0), sentPI, deliveredPI);
        }
    }

    public static void startSmsSender(Context context, String phone, String msg) {
        Intent intent = new Intent(context, SmsSender.class);
        intent.putExtra(EXTRA_PHONE, phone);
        intent.putExtra(EXTRA_MSG, msg);
        context.startService(intent);
    }
}
