package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.GlabalValues;
import lk.applife.english.wordchain.utill.MyBounceInterpolator;
import lk.applife.english.wordchain.utill.MyContextWrapper;

import static lk.applife.english.wordchain.ui.HomeActivity.USER_PREFERENCES;

public class WinningActivity extends AppCompatActivity {

    TextView congratsView;
    TextView wonView;
    TextView scoreView;
    LinearLayout lowScore;
    LinearLayout winningLayout;
    int score;
    int attempt;
    String LANG_CURRENT = "";
    Animation animation;
    SharedPreferences userPreference;
    int userSubscriptionStatus;
    Activity winningActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        animation.setInterpolator(interpolator);
        animation.setRepeatCount(Animation.INFINITE);
        congratsView = (TextView) findViewById(R.id.tv_congrats);
        wonView = (TextView) findViewById(R.id.tv_won);
        scoreView = (TextView) findViewById(R.id.tv_score);
        lowScore = (LinearLayout) findViewById(R.id.lowScoreLayout);
        winningLayout = (LinearLayout) findViewById(R.id.winningLayout);
        userPreference = this.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        winningActivity = (Activity) WinningActivity.this;
        getUserSubscriptionStatus();

        getIncomingData();
    }

    private void getUserSubscriptionStatus() {
        userSubscriptionStatus = userPreference.getInt("userSubscriptionStatus", 0);
    }

    @SuppressLint("SetTextI18n")
    private void getIncomingData() {
        if ((getIntent().getStringExtra("finished")) != null && (Objects.equals(getIntent().getStringExtra("finished"), "yes"))){
            score = getIntent().getIntExtra("score", 0);
            attempt = getIntent().getIntExtra("attempt", 1);
            scoreView.setText(getString(R.string.your_score) + score);
            if (score > 15){
                showWonView();
            }else {
                showTryView();
            }
        }
    }

    private void showTryView() {
        winningLayout.setVisibility(View.GONE);
        lowScore.setVisibility(View.VISIBLE);
        wonView.setVisibility(View.GONE);
    }

    private void showWonView() {
        winningLayout.setVisibility(View.VISIBLE);
        lowScore.setVisibility(View.GONE);
        wonView.startAnimation(animation);
        YoYo.with(Techniques.RubberBand)
                .duration(1500)
                .repeat(2)
                .playOn(scoreView);
    }

    public void startTypedWords(View view) {
        if (userSubscriptionStatus == 1) { //premium user
            Intent words = new Intent(WinningActivity.this, WordsActivity.class);
            words.putExtra("attempt_id", attempt);
            startActivity(words);
        } else { //Non Premium user
            showActivateProDialog();
        }
    }

    private void showActivateProDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_card, null);
        // Set the custom layout as alert dialog view
        alertDialog.setView(dialogView);
        // Get the custom alert dialog view widgets reference
        Button btn_positive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = dialogView.findViewById(R.id.dialog_neutral_btn);
        TextView title = dialogView.findViewById(R.id.dialog_titile);
        TextView dialog_tv = dialogView.findViewById(R.id.dialog_tv);

        title.setText(R.string.winning_activity_dialog_title);
        dialog_tv.setText(R.string.winning_activity_dialog_des);

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Intent enter = new Intent(WinningActivity.this, EnterMobileNumberActivity.class);
                startActivity(enter);
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        new Dialog(getApplicationContext());
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        if (!winningActivity.isFinishing()) {
            alertDialog.show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}