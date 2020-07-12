package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.adapter.AttemptsAdapter;
import lk.applife.english.wordchain.adapter.WordsAdapter;
import lk.applife.english.wordchain.model.UserAttempt;
import lk.applife.english.wordchain.model.Word;
import lk.applife.english.wordchain.utill.DatabaseHelper;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class WordsActivity extends AppCompatActivity {

    TextView attemptView;
    RecyclerView wordsRecyclerView;
    ArrayList<UserAttempt> userAttemptArrayList;
    ArrayList<Word> wordArrayList;
    WordsAdapter wordsAdapter;
    int attemptId;
    DatabaseHelper db;
    String LANG_CURRENT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        attemptView = (TextView) findViewById(R.id.tv_attempt_no);
        wordsRecyclerView = (RecyclerView) findViewById(R.id.wordsRecyclerView);
        db = new DatabaseHelper(this);

        getIncomingData();
    }

    private void getIncomingData() {
        attemptId = getIntent().getIntExtra("attempt_id", 1);
        attemptView.setText(String.valueOf(attemptId));
        initializeRecycler();
    }

    private void initializeRecycler() {
        userAttemptArrayList = new ArrayList<>();
        wordArrayList = new ArrayList<>();
        Cursor attempt = db.getMarksByAttempt(attemptId);
        if (attempt.getCount() != 0){
                ArrayList<String> apWrdList = null;
                ArrayList<String> usrWrdList = null;
                Cursor attemptWords = db.getAllWordsByAttempt(attemptId);
                if (attemptWords.getCount() != 0){
                    apWrdList = new ArrayList<>();
                    usrWrdList = new ArrayList<>();
                    while (attemptWords.moveToNext()){
                        apWrdList.add(attemptWords.getString(attemptWords.getColumnIndex("word_app")));
                        usrWrdList.add(attemptWords.getString(attemptWords.getColumnIndex("word_user")));
                        wordArrayList.add(new Word(1, attemptWords.getString(attemptWords.getColumnIndex("word_app"))));
                        wordArrayList.add(new Word(2, attemptWords.getString(attemptWords.getColumnIndex("word_user"))));
                    }
                }
            wordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            wordsAdapter = new WordsAdapter(this, wordArrayList);
            wordsRecyclerView.setAdapter(wordsAdapter);
            wordsAdapter.setWords(wordArrayList);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }
}