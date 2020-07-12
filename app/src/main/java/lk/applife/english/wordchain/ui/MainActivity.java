package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIMEOUT = 5000;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView main, slogan;
    String LANG_CURRENT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        image = (ImageView) findViewById(R.id.imageView);
        main = (TextView) findViewById(R.id.mainText);
        slogan = (TextView) findViewById(R.id.sloganText);

        image.setAnimation(topAnim);
        main.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}
