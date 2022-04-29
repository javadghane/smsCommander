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


        ImageView img1 = findViewById(R.id.imgGif1);
        ImageView img2 = findViewById(R.id.imgGif2);
        ImageView img3 = findViewById(R.id.imgGif3);
        Glide.with(this).asGif().load(R.drawable.off_anim).into(img1);
        Glide.with(this).asGif().load(R.drawable.on_anim).into(img2);
        Glide.with(this).asGif().load(R.drawable.off_anim).into(img3);

    }

    void log(String msg) {
        android.util.Log.e("tag", msg);
    }

}