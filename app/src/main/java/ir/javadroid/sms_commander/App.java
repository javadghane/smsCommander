package ir.javadroid.sms_commander;

import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;

public class App extends Application {
    public static Context context;



    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }
}
