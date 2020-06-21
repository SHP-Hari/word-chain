package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class HowToPlay extends AppCompatActivity {
    String LANG_CURRENT = "";
    CardView step1, step2, step3, step4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        step1 = (CardView) findViewById(R.id.stepCard1);
        step2 = (CardView) findViewById(R.id.stepCard2);
        step3 = (CardView) findViewById(R.id.stepCard3);
        step4 = (CardView) findViewById(R.id.stepCard4);

        step1.startAnimation(animation);
        step2.startAnimation(animation);
        step3.startAnimation(animation);
        step4.startAnimation(animation);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}
