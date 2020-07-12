package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.MyBounceInterpolator;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class WinningActivity extends AppCompatActivity {

    TextView congratsView;
    TextView wonView;
    TextView scoreView;
    int score;
    int attempt;
    String LANG_CURRENT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        animation.setInterpolator(interpolator);
        animation.setRepeatCount(Animation.INFINITE);
        congratsView = (TextView) findViewById(R.id.tv_congrats);
        wonView = (TextView) findViewById(R.id.tv_won);
        scoreView = (TextView) findViewById(R.id.tv_score);

        getIncomingData();
        wonView.startAnimation(animation);
        YoYo.with(Techniques.RubberBand)
                .duration(1500)
                .repeat(2)
                .playOn(scoreView);
    }

    @SuppressLint("SetTextI18n")
    private void getIncomingData() {
        if ((getIntent().getStringExtra("finished")) != null && (Objects.equals(getIntent().getStringExtra("finished"), "yes"))){
            score = getIntent().getIntExtra("score", 0);
            attempt = getIntent().getIntExtra("attempt", 1);
            scoreView.setText(getString(R.string.your_score) + score);
        }
    }

    public void startTypedWords(View view) {
        Intent words = new Intent(WinningActivity.this, WordsActivity.class);
        words.putExtra("attempt_id", attempt);
        startActivity(words);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}