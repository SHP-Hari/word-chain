package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIMEOUT = 5000;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView main, slogan;
    String LANG_CURRENT = "";
    AppEventsLogger logger;
    SharedPreferences userpreference;
    String android_id;

    @SuppressLint("HardwareIds")
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
        userpreference = this.getSharedPreferences("userPreference", Context.MODE_PRIVATE);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor editor = userpreference.edit();
        editor.putString("deviceId", android_id);
        editor.putString("deviceModel", Build.MANUFACTURER + " " +Build.MODEL);
        editor.putString("deviceOS", "Android" + " " +Build.VERSION.RELEASE);
        editor.putString("deviceOSName", "Android" + " " +Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName());
        editor.apply();

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

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSentFriendRequestEvent () {
        logger.logEvent("sentFriendRequest");
    }
}
