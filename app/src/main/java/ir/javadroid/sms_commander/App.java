package ir.javadroid.sms_commander;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static Context context;

    public static boolean isAlarmChecking = false;


    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }
}
