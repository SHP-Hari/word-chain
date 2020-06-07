package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

import lk.applife.english.wordchain.R;

public class WinningActivity extends AppCompatActivity {

    TextView congratsView;
    TextView wonView;
    TextView scoreView;
    int score;
    int attempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        congratsView = (TextView) findViewById(R.id.tv_congrats);
        wonView = (TextView) findViewById(R.id.tv_won);
        scoreView = (TextView) findViewById(R.id.tv_score);

        getIncomingData();
        YoYo.with(Techniques.Swing)
                .duration(1500)
                .repeat(2000)
                .playOn(wonView);

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
            scoreView.setText("Your Score : "+score);
        }
    }

    public void startTypedWords(View view) {
        Intent words = new Intent(WinningActivity.this, WordsActivity.class);
        words.putExtra("attempt_id", attempt);
        startActivity(words);
    }
}