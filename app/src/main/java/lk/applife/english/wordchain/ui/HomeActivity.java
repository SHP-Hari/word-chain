package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.fragments.LanguageChange;

public class HomeActivity extends AppCompatActivity {

    Button openBottomSheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        openBottomSheet = (Button) findViewById(R.id.openLanguageSheetBtn);

        openBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageChange languageChangeSheet = new LanguageChange();
                languageChangeSheet.show(getSupportFragmentManager(), "CHANGE_LANGUAGE");
            }
        });
    }

    public void startHowToPlay(View view) {
        Intent howToPlay = new Intent(HomeActivity.this, HowToPlay.class);
        startActivity(howToPlay);
    }

    public void startWordChain(View view) {
        Intent wordChain = new Intent(HomeActivity.this, WordChainActivity.class);
        startActivity(wordChain);
    }
}
