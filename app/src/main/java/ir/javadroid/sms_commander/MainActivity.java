package ir.javadroid.sms_commander;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    SentMessageReceiver sent;
    DeliveredMessageReceiver delivered;

    ImageView img1;
    ImageView img2;
    ImageView img3;

    SwitchButton sw1;
    SwitchButton sw2;
    SwitchButton sw3;

    EditText edtTimer1;
    EditText edtTimer2;
    EditText edtTimer3;

    ProgressDialog pgDialog;

    String masterMobileNumber = "09363667756";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pgDialog = new ProgressDialog(this);
        pgDialog.setTitle("please wait...");
        pgDialog.setMessage("Loading...");


        img1 = findViewById(R.id.imgGif1);
        img2 = findViewById(R.id.imgGif2);
        img3 = findViewById(R.id.imgGif3);
        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);
        edtTimer1 = findViewById(R.id.edtTimer1);
        edtTimer2 = findViewById(R.id.edtTimer2);
        edtTimer3 = findViewById(R.id.edtTimer3);


        sw1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                HelperSharedPreferences.SaveBoolean("sw_1_status", isChecked);
                HelperSharedPreferences.SaveString("sw_1_timer", edtTimer1.getText().toString());

                String timer = "1"; //default timer
                if (edtTimer1.getText().toString().length() > 0)
                    timer = edtTimer1.getText().toString();

                String status = "stop";
                if (isChecked) status = "start";

                String smsMessage = "change_1_" + status + "_" + timer; // sample: change_1_start_75
                SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);
            }
        });

        sw2.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                HelperSharedPreferences.SaveBoolean("sw_2_status", isChecked);
                HelperSharedPreferences.SaveString("sw_2_timer", edtTimer2.getText().toString());

                String timer = "1"; //default timer
                if (edtTimer2.getText().toString().length() > 0)
                    timer = edtTimer2.getText().toString();

                String status = "stop";
                if (isChecked) status = "start";

                String smsMessage = "change_2_" + status + "_" + timer; // sample: change_1_start_75
                SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);
            }
        });

        sw3.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                HelperSharedPreferences.SaveBoolean("sw_3_status", isChecked);
                HelperSharedPreferences.SaveString("sw_3_timer", edtTimer3.getText().toString());

                String timer = "1"; //default timer
                if (edtTimer3.getText().toString().length() > 0)
                    timer = edtTimer3.getText().toString();

                String status = "stop";
                if (isChecked) status = "start";

                String smsMessage = "change_2_" + status + "_" + timer; // sample: change_1_start_75
                SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);
            }
        });


        //todo check permission and give permissions
        //permission sms - permission draw overlay
        Intent start = new Intent(this, ForegroundServiceNotification.class);
        ContextCompat.startForegroundService(this, start);

        sent = new SentMessageReceiver();
        delivered = new DeliveredMessageReceiver();
        registerReceiver(sent, new IntentFilter(SmsSender.INTENT_SENT_MESSAGE));
        registerReceiver(delivered, new IntentFilter(SmsSender.INTENT_DELIVERED_MESSAGE));
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


    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    private void loadData() {
        boolean device1Status = HelperSharedPreferences.LoadBoolean("device_1", false);
        boolean device2Status = HelperSharedPreferences.LoadBoolean("device_2", false);
        boolean device3Status = HelperSharedPreferences.LoadBoolean("device_3", false);

        if (device1Status)
            Glide.with(this).asGif().load(R.drawable.on_anim).into(img1);
        else
            Glide.with(this).asGif().load(R.drawable.off_anim).into(img1);

        if (device2Status)
            Glide.with(this).asGif().load(R.drawable.on_anim).into(img2);
        else
            Glide.with(this).asGif().load(R.drawable.off_anim).into(img2);

        if (device3Status)
            Glide.with(this).asGif().load(R.drawable.on_anim).into(img3);
        else
            Glide.with(this).asGif().load(R.drawable.off_anim).into(img3);

        boolean statusSw1 = HelperSharedPreferences.LoadBoolean("sw_1_status", false);
        boolean statusSw2 = HelperSharedPreferences.LoadBoolean("sw_2_status", false);
        boolean statusSw3 = HelperSharedPreferences.LoadBoolean("sw_3_status", false);

        sw1.setChecked(statusSw1);
        sw2.setChecked(statusSw2);
        sw3.setChecked(statusSw3);

        edtTimer1.setText(HelperSharedPreferences.LoadString("sw_1_timer", ""));
        edtTimer2.setText(HelperSharedPreferences.LoadString("sw_2_timer", ""));
        edtTimer3.setText(HelperSharedPreferences.LoadString("sw_3_timer", ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceived(EventBus_SMS eventBusSms) {
        loadData();
        String stickyEvent = EventBus.getDefault().getStickyEvent(String.class);
        EventBus.getDefault().removeStickyEvent(stickyEvent);
    }


}