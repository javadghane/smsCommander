package ir.javadroid.sms_commander;
///

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

///

public class MainActivity extends AppCompatActivity {
    /////////for time
    TextView tvTimerHourCountDown1;
    TextView tvTimerHourCountDown2;
    TextView tvTimerHourCountDown3;
    TextView tvTimerHour1;
    TextView tvTimerHour2;
    TextView tvTimerHour3;
    //////////////
    //long getTime;

    private static final long START_TIME_IN_MILLIS = 600000;

    private TextView mTextViewCountDown;


    ///////
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
    EditText edtphonNm;
    TextView tTimer;

    ProgressDialog pgDialog;

    //شماره تلفن همراهی که پیامک ها به اون ارسال میشه
    String masterMobileNumber = "07502435057"; //07502435057

    boolean canCheckSwitch = true;
    private Object timers;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //////////////////////for time
        tvTimerHourCountDown1 = (TextView) findViewById(R.id.t_v_timeShow1);
        tvTimerHourCountDown2 = (TextView) findViewById(R.id.t_v_timeShow2);
        tvTimerHourCountDown3 = (TextView) findViewById(R.id.t_v_timeShow3);
        ///
        tvTimerHour1 = (TextView) findViewById(R.id.tTimerS1);
        tvTimerHour2 = (TextView) findViewById(R.id.tTimerS2);
        tvTimerHour3 = (TextView) findViewById(R.id.tTimerS3);


        ////////////
        //دیالوگ لودینگ . اگر لازم داشتید جایی لودینگ نشون بدید فقط کافیه خط زیر رو قرار بدید جایی
        // pgDialog.show();
        pgDialog = new ProgressDialog(this);
        pgDialog.setTitle("دلنيايت ده ته ويت ئيش كات");
        pgDialog.setMessage("جاوه رونى...");


        img1 = findViewById(R.id.imgGif1);
        img2 = findViewById(R.id.imgGif2);
        img3 = findViewById(R.id.imgGif3);
        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);
        edtTimer1 = findViewById(R.id.edtTimer1);
        edtTimer2 = findViewById(R.id.edtTimer2);
        edtTimer3 = findViewById(R.id.edtTimer3);
        edtphonNm = findViewById(R.id.phoneNm);


        edtTimer1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setTvNumberStyle(edtTimer1);
                }
            }
        });
        edtTimer2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setTvNumberStyle(edtTimer2);
                }
            }
        });
        edtTimer3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setTvNumberStyle(edtTimer3);
                }
            }
        });


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
                .setTitle("فرمان بيدان")
                .setMessage("دلنيايت ده ته وبت ئه م كؤرانكاريه ئه نجام بده يت")
                .setIcon(android.R.drawable.ic_lock_idle_charging)


                .setPositiveButton("به لى", (dialog, whichButton) -> {

                    //ذخیره سازی وضعیت سویچ
                    //HelperSharedPreferences.SaveBoolean("sw_" + switchNumber + "_status", isChecked);
                    //ذخیره سازی مقدار تایمر سویچ
                    // HelperSharedPreferences.SaveString("sw_" + switchNumber + "_timer", edtTimer1.getText().toString());


                    //مقدار دیفالت تایمر اگر خالی بود
                    String timer1 = "120"; //default timer
                    String timer2 = "120"; //default timer
                    String timer3 = "120"; //default timer
                    String timer = "";
                    //ali
                    if (switchNumber.equals("1")) {
                        timer = timer1;
                        if (edtTimer1.getText().toString().length() > 0)
                            timer = edtTimer1.getText().toString();
                        setHourOfTimer(timer, tvTimerHour1);

                        //اگر برابر 0 شود متد پس از اجرای پایان اجرا نمیشود . اگر یک باشد اجرا میشود
                        long endTime = 1;
                        if (isChecked) {
                            endTime = System.currentTimeMillis() + (Integer.parseInt(timer) * 60 * 1000);
                        }
                        HelperSharedPreferences.SaveString("timer1_end", endTime + "");

                    }
                    if (switchNumber.equals("2")) {
                        timer = timer2;
                        if (edtTimer2.getText().toString().length() > 0)
                            timer = edtTimer2.getText().toString();
                        setHourOfTimer(timer, tvTimerHour2);

                        //اگر برابر 0 شود متد پس از اجرای پایان اجرا نمیشود . اگر یک باشد اجرا میشود
                        long endTime = 1;
                        if (isChecked) {
                            endTime = System.currentTimeMillis() + (Integer.parseInt(timer) * 60 * 1000);
                        }
                        HelperSharedPreferences.SaveString("timer2_end", endTime + "");
                    }
                    if (switchNumber.equals("3")) {
                        timer = timer3;
                        if (edtTimer3.getText().toString().length() > 0)
                            timer = edtTimer3.getText().toString();
                        setHourOfTimer(timer, tvTimerHour3);

                        //اگر برابر 0 شود متد پس از اجرای پایان اجرا نمیشود . اگر یک باشد اجرا میشود
                        long endTime = 1;
                        if (isChecked) {
                            endTime = System.currentTimeMillis() + (Integer.parseInt(timer) * 60 * 1000);
                        }
                        HelperSharedPreferences.SaveString("timer3_end", endTime + "");
                    }
                    ///

                    //ذخیره سازی وضعیت سویچ  Ali
                    HelperSharedPreferences.SaveBoolean("sw_" + switchNumber + "_status", isChecked);
                    //ذخیره سازی مقدار تایمر سویچ
                    HelperSharedPreferences.SaveString("sw_" + switchNumber + "_timer", timer);
                    String smsMessage;
                    String status = "000";
                    //*RLY#1#200#
                    if (isChecked)
                        status = timer;
                    smsMessage = "*RLY#" + switchNumber + "#" + status + "#"; // sample: change_1_start_75

                    // if (status.equals("stop"));

                    //  smsMessage = "*RLY#" + switchNumber  +"#"+   timer +"#" ; // sample: change_1_start_75

                    // String smsMessage = "change_" + switchNumber + "_" + status + "$" + timer; // sample: change_1_start_75
                    //  smsMessage = "R" + switchNumber +  status + timer; // sample: change_1_start_75
                    masterMobileNumber = edtphonNm.getText().toString();


                    //SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);


                    checkingTimers();
                })
                .setNegativeButton("نه خير", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        canCheckSwitch = false;
                        switchItem.setChecked(!isChecked);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canCheckSwitch = true;
                            }
                        }, 500);


                    }
                }).show();

    }


    void setTvNumberStyle(TextView tv) {
        if (tv.getText().toString().length() > 0) {
            int number = Integer.parseInt(tv.getText().toString());
            tv.setText(String.format("%03d", number) + "");
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        checkingTimers();
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
        setTvNumberStyle(edtTimer1);
        setTvNumberStyle(edtTimer2);
        setTvNumberStyle(edtTimer3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewDataReceived(EventBus_SMS eventBusSms) {
        //با دریافت پیام این متد کال میشود
        loadData();
        String stickyEvent = EventBus.getDefault().getStickyEvent(String.class);
        EventBus.getDefault().removeStickyEvent(stickyEvent);
    }


    public void checkingTimers() {
        long now = System.currentTimeMillis();

        //---------------- timer1
        long timer1_end = Long.parseLong(HelperSharedPreferences.LoadString("timer1_end", "0"));
        if (timer1_end > now) {
            long diff = (now - timer1_end) / 1000;
            setCountTimerText(diff, tvTimerHourCountDown1);
        } else {
            if (timer1_end > 0) {
                onFinishTimer(1);
            } else {
                //timer not started yet
            }
        }


        //---------------- timer2
        long timer2_end = Long.parseLong(HelperSharedPreferences.LoadString("timer2_end", "0"));
        if (timer2_end > now) {
            long diff = (now - timer2_end) / 1000;
            setCountTimerText(diff, tvTimerHourCountDown2);
        } else {
            if (timer2_end > 0) {
                onFinishTimer(2);
            } else {
                //timer not started yet
            }
        }

        //---------------- timer3
        long timer3_end = Long.parseLong(HelperSharedPreferences.LoadString("timer3_end", "0"));
        if (timer3_end > now) {
            long diff = (now - timer3_end) / 1000;
            setCountTimerText(diff, tvTimerHourCountDown3);
        } else {
            if (timer3_end > 0) {
                onFinishTimer(3);
            } else {
                //timer not started yet
            }
        }


        if (timer1_end > now || timer2_end > now || timer3_end > now) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkingTimers();
                }
            }, 1000);
        }
    }


    //متد پس از اجرای پایان
    public void onFinishTimer(int timerNumber) {
        switch (timerNumber) {
            case 1:
                //اینجا میتوان پس از پایان تایمر 1 اعمال مورد نیاز را اجرا کرد.
                //SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);

                tvTimerHour1.setText("00:00:00");
                tvTimerHourCountDown1.setText("00:00:00");
                HelperSharedPreferences.SaveString("timer1_end", "0");
                break;
            case 2:
                //اینجا میتوان پس از پایان تایمر 2 اعمال مورد نیاز را اجرا کرد.
                //SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);

                tvTimerHour2.setText("00:00:00");
                tvTimerHourCountDown2.setText("00:00:00");
                HelperSharedPreferences.SaveString("timer2_end", "0");
                break;
            case 3:
                //اینجا میتوان پس از پایان تایمر 3 اعمال مورد نیاز را اجرا کرد.
                //SmsSender.startSmsSender(getApplicationContext(), masterMobileNumber, smsMessage);

                tvTimerHour3.setText("00:00:00");
                tvTimerHourCountDown3.setText("00:00:00");
                HelperSharedPreferences.SaveString("timer3_end", "0");
                break;
        }
    }

    public void setCountTimerText(long seconds, TextView counterTv) {
        if (seconds == 0) {
            counterTv.setText("00:00:00");
        } else {
            int hours = (int) (seconds / (60 * 60));
            int tempMint = (int) (seconds - (hours * 60 * 60));
            int minutes = tempMint / 60;
            seconds = tempMint - (minutes * 60);
            counterTv.setText(String.format("%02d", Math.abs(hours))
                    + ":" + String.format("%02d", Math.abs(minutes))
                    + ":" + String.format("%02d", Math.abs(seconds)));
        }
    }

    public void setHourOfTimer(String min, TextView tvThatShowTime) {
        if (min.equalsIgnoreCase("0")) {
            tvThatShowTime.setText("00:00:00");
        } else {
            int seconds = Integer.parseInt(min) * 60;
            int hours = seconds / (60 * 60);
            int tempMint = (seconds - (hours * 60 * 60));
            int minutes = tempMint / 60;
            seconds = tempMint - (minutes * 60);

            //   tshow1.setText("TIME : " + String.format("%02d", hours)
            tvThatShowTime.setText(String.format("%02d", hours)
                    + ":" + String.format("%02d", minutes)
                    + ":" + String.format("%02d", seconds));
        }

    }
    //for timer
    //Declare timer
    // CountDownTimer cTimer = null;
    //int check = 1;

   /* //start timer function
    void startTimer(long timeLeft) {
        cTimer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                //   tshow1.setText("TIME : " + String.format("%02d", hours)
                tshow1.setText(String.format("%02d", hours)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));

                if (check == 1) {
                    tTimerS1.setText(tshow1.getText());
                    check = 0;
                }
                //////

            }

            public void onFinish() {
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }*/
}

