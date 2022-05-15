package ir.javadroid.sms_commander;

import android.content.SharedPreferences;


/**
 * Created by JavaDroid on 4/23/2015.
 */
public class HelperSharedPreferences {


    public static boolean SaveString(String key, String Value) {
        SharedPreferences s = App.context.getSharedPreferences("SP", 0);
        SharedPreferences.Editor ee = s.edit();
        ee.putString(key, Value);
        ee.commit();
        return true;
    }

    public static String LoadString(String key, String default_value) {
        SharedPreferences SP = App.context.getSharedPreferences("SP", 0);
        return SP.getString(key, default_value);
    }

    public static int LoadInt(String key, int default_value) {
        SharedPreferences SP = App.context.getSharedPreferences("SP", 0);
        return SP.getInt(key, default_value);
    }

    public static boolean SaveInt(String key, int Value) {
        SharedPreferences s = App.context.getSharedPreferences("SP", 0);
        SharedPreferences.Editor ee = s.edit();
        ee.putInt(key, Value);
        ee.commit();
        return true;
    }

    public static boolean LoadBoolean(String key, boolean default_value) {
        SharedPreferences SP = App.context.getSharedPreferences("SP", 0);
        return SP.getBoolean(key, default_value);
    }

    public static boolean SaveBoolean(String key, boolean Value) {
        SharedPreferences s = App.context.getSharedPreferences("SP", 0);
        SharedPreferences.Editor ee = s.edit();
        ee.putBoolean(key, Value);
        ee.commit();
        return true;
    }


}