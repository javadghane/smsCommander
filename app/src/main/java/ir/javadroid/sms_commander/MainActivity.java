package ir.javadroid.sms_commander;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

    //شماره تلفن همراهی که پیامک ها به اون ارسال میشه
    String masterMobileNumber = "09363667756";

    boolean canCheckSwitch = true;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //دیالوگ لودینگ . اگر لازم داشتید جایی لودینگ نشون بدید فقط کافیه خط زیر رو قرار بدید جایی
        //  pgDialog.show();
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
                if (canCheckSwitch)
                    changeStatus(isChecked, "1", view);
            }
        });

        sw2.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (canCheckSwitch)
                    changeStatus(isChecked, "2", view);
            }
        });

        sw3.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (canCheckSwitch)
                    changeStatus(isChecked, "3", view);
            }
        });


        //بررسی دسترسی پیامک
        checkPerms();
        //بررسی دسترسی اسکرین - برای فعال ماندن همیشه اپ
        checkDrawOverlayPermission();

        Intent start = new Intent(this, ForegroundServiceNotification.class);
        ContextCompat.startForegroundService(this, start);

        sent = new SentMessageReceiver();
        delivered = new DeliveredMessageReceiver();
        registerReceiver(sent, new IntentFilter(SmsSender.INTENT_SENT_MESSAGE));
        registerReceiver(delivered, new IntentFilter(SmsSender.INTENT_DELIVERED_MESSAGE));
    }

    private void changeStatus(boolean isChecked, String switchNumber, SwitchButton switchItem) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Change status")
                .setMessage("Do you really want to change?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                    //ذخیره سازی وضعیت سویچ
                    HelperSharedPreferences.SaveBoolean("sw_" + switchNumber + "_status", isChecked);
                    //ذخیره سازی مقدار تایمر سویچ
                    HelperSharedPreferences.SaveString("sw_" + switchNumber + "_timer", edtTimer1.getText().toString());

                    //مقدار دیفالت تایمر اگر خالی بود
                    String timer = "1"; //default timer
                    if (edtTimer1.getText().toString().length() > 0)
                        timer = edtTimer1.getText().toString();

                    String status = "stop";
                    if (isChecked) status = "start";

                    String smsMessage = "change_" + switchNumber + "_" + status + "_" + timer; // sample: change_1_start_75
                    //ارسال پیامک
                    SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        canCheckSwitch=false;
                        switchItem.setChecked(false);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canCheckSwitch=true;
                            }
                        },500);
                    }
                }).show();
    }

    void log(String msg) {
        android.util.Log.e("tag", msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void checkPerms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.RECEIVE_SMS
                        }, 123);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) { // WHAT IF THIS EVALUATES TO FALSE.
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, 456);
        } else { // ADD THIS.
            // Add code to bind and start the service directly.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] != PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please give the permission", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
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
        //با دریافت پیام این متد کال میشود
        loadData();
        String stickyEvent = EventBus.getDefault().getStickyEvent(String.class);
        EventBus.getDefault().removeStickyEvent(stickyEvent);
    }


}