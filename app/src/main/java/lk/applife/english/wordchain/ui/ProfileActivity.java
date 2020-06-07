package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.adapter.AttemptsAdapter;
import lk.applife.english.wordchain.model.UserAttempt;
import lk.applife.english.wordchain.utill.DatabaseHelper;
import lk.applife.english.wordchain.utill.UserNameDialog;

public class ProfileActivity extends AppCompatActivity implements UserNameDialog.UserNameDialogListener {
    public static final String USER_INFO_PREFERENCES = "userInformation";
    SharedPreferences userInfoPreference;
    TextView nameTextView;
    LinearLayout noScores;
    LinearLayout scoreLayout;
    Button startGame;
    String userName;
    UserNameDialog userNameDialog;
    RecyclerView scoresRecyclerView;
    AttemptsAdapter attemptsAdapter;
    ArrayList<UserAttempt> userAttemptArrayList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = (TextView) findViewById(R.id.tv_userName);
        noScores = (LinearLayout) findViewById(R.id.noScores);
        scoreLayout = (LinearLayout) findViewById(R.id.scoresLayout);
        startGame = (Button) findViewById(R.id.playGameBtn);
        userInfoPreference = getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        userName = userInfoPreference.getString("username", null);
        scoresRecyclerView = (RecyclerView) findViewById(R.id.scoresRecyclerView);

        db = new DatabaseHelper(this);

        if (userName == null) {
            openDialog();
        }else {
            nameTextView.setText(userName);
        }
        
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent play = new Intent(ProfileActivity.this, WordChainActivity.class);
                startActivity(play);
            }
        });

        getScoreData();
    }

    private void getScoreData() {
        Cursor res = db.getAllAttemptWords();
        if (res.getCount() == 0) {
            scoreLayout.setVisibility(View.GONE);
            noScores.setVisibility(View.VISIBLE);
        }else {
            scoreLayout.setVisibility(View.VISIBLE);
            noScores.setVisibility(View.GONE);
            scoresRecycler();
        }
    }

    private void scoresRecycler() {
        userAttemptArrayList = new ArrayList<>();
        Cursor attempts = db.getAllAttempts();
        if (attempts.getCount() != 0){
            while (attempts.moveToNext()) {
                ArrayList<String> apWrdList = null;
                ArrayList<String> usrWrdList = null;
                int atmpt_id = attempts.getInt(attempts.getColumnIndex("attempt_id"));
                int atmpt_marks = attempts.getInt(attempts.getColumnIndex("marks"));
                Cursor attemptWords = db.getAllWordsByAttempt(atmpt_id);
                if (attemptWords.getCount() != 0){
                    apWrdList = new ArrayList<>();
                    usrWrdList = new ArrayList<>();
                    while (attemptWords.moveToNext()){
                        apWrdList.add(attemptWords.getString(attemptWords.getColumnIndex("word_app")));
                        usrWrdList.add(attemptWords.getString(attemptWords.getColumnIndex("word_user")));
                    }
                    userAttemptArrayList.add(new UserAttempt(atmpt_id, atmpt_marks, apWrdList, usrWrdList));
                }
            }
            scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            attemptsAdapter = new AttemptsAdapter(this, userAttemptArrayList);
            scoresRecyclerView.setAdapter(attemptsAdapter);
            attemptsAdapter.setUserAttempts(userAttemptArrayList);
        }else {
            scoreLayout.setVisibility(View.GONE);
            noScores.setVisibility(View.VISIBLE);
        }
    }

    public void openDialog() {
        userNameDialog = new UserNameDialog();
        userNameDialog.setCancelable(false);
        userNameDialog.show(getSupportFragmentManager(), "USERNAME_DIALOG");
    }

    public static boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public static boolean isLettersOnly(EditText text) {
        CharSequence name = text.getText().toString();
        return ((String) name).matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$");
    }

    @Override
    public void applyInfo(String name) {
        nameTextView.setText(name);
        if (userNameDialog != null){
            userNameDialog.dismiss();
        }
        SharedPreferences.Editor editor = userInfoPreference.edit();
        editor.putString("username", name);
        editor.apply();
    }
}