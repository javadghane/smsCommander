package ir.javadroid.sms_commander;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class DeliveredMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "Delivered", Toast.LENGTH_SHORT).show();
                onDelivered();
                break;
            default:
                onFailure();
                break;
        }
    }


    protected void onDelivered() {
    }

    protected void onFailure() {
    }
}
