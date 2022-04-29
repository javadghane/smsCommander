package ir.javadroid.sms_commander;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    SentMessageReceiver sent;
    DeliveredMessageReceiver delivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView img1 = findViewById(R.id.imgGif1);
        ImageView img2 = findViewById(R.id.imgGif2);
        ImageView img3 = findViewById(R.id.imgGif3);
        Glide.with(this).asGif().load(R.drawable.off_anim).into(img1);
        Glide.with(this).asGif().load(R.drawable.on_anim).into(img2);
        Glide.with(this).asGif().load(R.drawable.off_anim).into(img3);


        Intent start = new Intent(this, ForegroundServiceNotification.class);
        ContextCompat.startForegroundService(this, start);

        sent = new SentMessageReceiver();
        delivered = new DeliveredMessageReceiver();
        registerReceiver(sent, new IntentFilter(SmsSender.INTENT_SENT_MESSAGE));
        registerReceiver(delivered, new IntentFilter(SmsSender.INTENT_DELIVERED_MESSAGE));

        //SmsSender.startSmsSender(getApplicationContext(), phone, "$L3");
    }

    void log(String msg) {
        android.util.Log.e("tag", msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sent);
        unregisterReceiver(delivered);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportEdited(EventBus_SMS eventBusSms) {
        //todo update ui here after recieve sms
        String stickyEvent = EventBus.getDefault().getStickyEvent(String.class);
        EventBus.getDefault().removeStickyEvent(stickyEvent);
    }


}