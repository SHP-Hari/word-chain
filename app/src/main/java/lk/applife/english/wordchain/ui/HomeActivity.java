package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.fragments.LanguageChange;
import lk.applife.english.wordchain.utill.DatabaseHelper;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    public static final String USER_INFO_PREFERENCES = "userInformation";
    Button openBottomSheet;
    TextView userWelcome;
    SharedPreferences userInfoPreference;
    String userName;
    DatabaseHelper db;
    String LANG_CURRENT = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        openBottomSheet = (Button) findViewById(R.id.openLanguageSheetBtn);
        userWelcome = (TextView) findViewById(R.id.userWelcome);
        userInfoPreference = getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        userName = userInfoPreference.getString("username", null);
        db = new DatabaseHelper(this);

        if (userName != null){
            userWelcome.setText(getString(R.string.hello) + userName);
        }
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

    public void startActivatePro(View view) {
        Intent intent = new Intent(HomeActivity.this, WinningActivity.class);
        startActivity(intent);
    }

    public void startProfile(View view) {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        userName = userInfoPreference.getString("username", null);
        if (userName != null){
            userWelcome.setText(getString(R.string.hello) + userName);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}
