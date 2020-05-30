package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import lk.applife.english.wordchain.R;

public class HowToPlay extends AppCompatActivity {

    CardView step1, step2, step3, step4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        step1 = (CardView) findViewById(R.id.stepCard1);
        step2 = (CardView) findViewById(R.id.stepCard2);
        step3 = (CardView) findViewById(R.id.stepCard3);
        step4 = (CardView) findViewById(R.id.stepCard4);

        YoYo.with(Techniques.RotateIn)
                .duration(1500)
                .repeat(0)
                .playOn(step1);
        YoYo.with(Techniques.RotateInDownLeft)
                .duration(1500)
                .repeat(0)
                .playOn(step2);
        YoYo.with(Techniques.RotateInDownRight)
                .duration(1500)
                .repeat(0)
                .playOn(step3);
        YoYo.with(Techniques.RotateInUpLeft)
                .duration(1500)
                .repeat(0)
                .playOn(step4);
    }
}
