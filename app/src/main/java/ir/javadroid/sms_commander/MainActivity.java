package ir.javadroid.sms_commander;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView img2 = findViewById(R.id.imgGif2);
        Glide.with(this).asGif().load(R.drawable.on_anim).into(img2);

    }

    void log(String msg) {
        android.util.Log.e("tag", msg);
    }

}