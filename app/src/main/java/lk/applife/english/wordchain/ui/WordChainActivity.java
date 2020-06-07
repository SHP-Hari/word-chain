package lk.applife.english.wordchain.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.DatabaseHelper;

public class WordChainActivity extends AppCompatActivity {

    public static final String WORDS_TEXT_FILE_PATH = "words/words_alpha.txt";
    public static final long COUNTDOWN_IN_MILLIS = 30000;
    public static final int MARKS_PER_CORRECT_WORD = 5;
    public static final long MAXIMUM_GAME_PLAY_TIME = 120000;
    EditText userInputEditText;
    Button submit;
    HorizontalScrollView appWordHorizontalScroll;
    LinearLayout appWordLayout;
    LinearLayout userWordLayout;
    LinearLayout loadingLayout;
    LinearLayout mainLayout;
    LinearLayout correctAnimationLayout;
    LinearLayout scoreLayout;
    TextView appWord;
    FrameLayout animationFrame;
    TextView score;
    TextView wordsCompleted;
    TextView duration;

    private ColorStateList textColorDefaultCountdown;

    ArrayList<String> wordList;
    ArrayList<String> wordsByApp;
    ArrayList<String> wordsByUser;
    String currentWordApp;
    String currentWordUser;
    String lastLetterOfCurrentWordApp;
    String lastLetterOfCurrentWordUser;
    private int wordsCounter = 0;
    private int gameScore = 0;
    private CountDownTimer countDownTimer;
    private CountDownTimer mainGamePlayTimer;
    private long timeLeftInMillis;
    private long timeLeftInMillisGamePlay;
    DatabaseHelper db;
    private int attemptId;
    Activity wordChainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_chain);

        if (savedInstanceState != null){
            currentWordApp = savedInstanceState.getString("CurrentAppWordKey");
            currentWordUser = savedInstanceState.getString("CurrentUserWordKey");
            lastLetterOfCurrentWordApp = savedInstanceState.getString("CurrentAppWordLastLetterKey");
            lastLetterOfCurrentWordUser = savedInstanceState.getString("CurrentUserWordLastLetterKey");
        }else {
            currentWordApp = null;
            currentWordUser = null;
            lastLetterOfCurrentWordApp = null;
            lastLetterOfCurrentWordUser = null;
        }

        userInputEditText = (EditText) findViewById(R.id.userInputEditText);
        userInputEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        submit = (Button) findViewById(R.id.submitBtn);
        appWordHorizontalScroll = (HorizontalScrollView) findViewById(R.id.appWordHorizontalScroll);
        appWordLayout = (LinearLayout) findViewById(R.id.appWordLayout);
        userWordLayout = (LinearLayout) findViewById(R.id.userWordLayout);
        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        mainLayout = (LinearLayout) findViewById(R.id.mainWordsLayout);
        scoreLayout = (LinearLayout) findViewById(R.id.scoreLayout);
        correctAnimationLayout = (LinearLayout) findViewById(R.id.correctAnimationLayout);
        appWord = (TextView) findViewById(R.id.tvSuggestedWord);
        score = (TextView) findViewById(R.id.tv_score);
        wordsCompleted = (TextView) findViewById(R.id.tv_words_completed);
        duration = (TextView) findViewById(R.id.tv_duration);
        animationFrame = (FrameLayout) findViewById(R.id.animationFrame);
        wordList = new ArrayList<>();
        wordsByApp = new ArrayList<>();
        wordsByUser = new ArrayList<>();
        textColorDefaultCountdown = duration.getTextColors();
        db = new DatabaseHelper(this);
        wordChainActivity = (Activity) WordChainActivity.this;

//        db.insertQuizAttemptMarks(20);
        new InitialWord().execute();

    }

    private void showLoadingAnimation() {
        animationFrame.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        scoreLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.GONE);
    }

    private void closeLoadingAnimation(){
        animationFrame.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        scoreLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public class InitialWord extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingAnimation();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            addWordsToList();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getUserAttempt();
            score.setText(String.format("Score : %d", gameScore));
            closeLoadingAnimation();
            selectTheDefaultStaringWord();
        }
    }

    private void getUserAttempt() {
        Cursor atId = db.getLastRowInMarks();
        if (atId.getCount() == 0){
            setAttemptId(1);
        }else {
            int lastAttempt = 0;
            while (atId.moveToNext()) {
                lastAttempt = atId.getInt(atId.getColumnIndex("attempt_id"));
            }
            setAttemptId(++lastAttempt);
        }
    }

    private void addWordsToList() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getAssets().open(WORDS_TEXT_FILE_PATH)));
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                wordList.add(sCurrentLine);
            }
        } catch (IOException e) {
            Log.e("Load Asset File",e.toString());
        }
    }

    public void suggestNewWordFromApp(ArrayList<String> stringArrayList){
        Random random = new Random();
        int randomNumber = random.nextInt(stringArrayList.size());
        String suggestedWord = stringArrayList.get(randomNumber);
        SpannableString spanString = new SpannableString(suggestedWord);
        Matcher matcher = Pattern.compile("([a-z])$").matcher(spanString);
        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.word_chain_user_input));

        while (matcher.find()) {
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor(colorHex)), matcher.start(), matcher.end(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new RelativeSizeSpan(1.5f), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        appWord.setText(spanString);
        currentWordApp = suggestedWord;
        wordsByApp.add(suggestedWord);
        wordList.remove(suggestedWord);
        String[] appWordArr = new String[suggestedWord.length()];

        for (int i = 0; i < suggestedWord.length(); i++) {
            appWordArr[i] = String.valueOf(suggestedWord.charAt(i));
            lastLetterOfCurrentWordApp = String.valueOf(suggestedWord.charAt(i));
        }

        for (String key : appWordArr){
            addViewAppWord((LinearLayout) findViewById(R.id.appWordLayout), key);
        }

        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDown();
    }

    private void selectTheDefaultStaringWord() {
        Random random = new Random();
        int randomNumber = random.nextInt(wordList.size());
        String startWord = wordList.get(randomNumber);
        SpannableString spanString = new SpannableString(startWord);
        Matcher matcher = Pattern.compile("([a-z])$").matcher(spanString);
        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.word_chain_user_input));

        while (matcher.find()) {
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor(colorHex)), matcher.start(), matcher.end(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new RelativeSizeSpan(1.5f), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        appWord.setText(spanString);
        currentWordApp = startWord;
        wordsByApp.add(startWord);
        wordList.remove(randomNumber);
        String[] appWordArr = new String[startWord.length()];

        for (int i = 0; i < startWord.length(); i++) {
            appWordArr[i] = String.valueOf(startWord.charAt(i));
            lastLetterOfCurrentWordApp = String.valueOf(startWord.charAt(i));
        }

        for (String key : appWordArr){
            addViewAppWord((LinearLayout) findViewById(R.id.appWordLayout), key);
        }

        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDown();
        timeLeftInMillisGamePlay = MAXIMUM_GAME_PLAY_TIME;
        startMainGamePlayCountDown();
    }

    private void addViewAppWord(LinearLayout viewAppWord, final String appWordChar) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.rightMargin = 2;
        layoutParams.leftMargin = 2;

        final TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams);
        textView.setBackground(this.getResources().getDrawable(R.drawable.ic_plus_icon));
        textView.setTextColor(this.getResources().getColor(R.color.word_chain_user_input));
        textView.setGravity(Gravity.CENTER);
        textView.setText(appWordChar);
        textView.setTextSize(32);
        textView.setAllCaps(true);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one.ttf");
        textView.setTypeface(typeface);
        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .repeat(2)
                .playOn(textView);

        viewAppWord.addView(textView);
        appWordHorizontalScroll.post(new Runnable() {
            public void run() {
                appWordHorizontalScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                 timeLeftInMillis = 0;
                 updateCountDownText();
                 timeUp();
            }
        }.start();
    }

    private void startMainGamePlayCountDown(){
        mainGamePlayTimer = new CountDownTimer(timeLeftInMillisGamePlay, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillisGamePlay = l;
            }

            @Override
            public void onFinish() {
                timeLeftInMillisGamePlay = 0;
                endAttempt();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) ((timeLeftInMillis / 1000) / 60);
        int seconds = (int) ((timeLeftInMillis / 1000) % 60);
        String timeFormattedText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        duration.setText(timeFormattedText);

        if (timeLeftInMillis < 10000){
            duration.setTextColor(Color.RED);
        }else {
            duration.setTextColor(textColorDefaultCountdown);
        }
    }

    private void timeUp(){
        countDownTimer.cancel();
        Toast.makeText(this, "Time Up Buddy!", Toast.LENGTH_SHORT).show();
        endAttempt();
    }

    private void endAttempt(){
        countDownTimer.cancel();
        mainGamePlayTimer.cancel();
        openTimeUpDialog();
    }

    private void openTimeUpDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_one_button, null);

        // Set the custom layout as alert dialog view
        alertDialog.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = dialogView.findViewById(R.id.dialog_positive_btn);
        TextView title = dialogView.findViewById(R.id.dialog_titile);
        TextView dialog_tv = dialogView.findViewById(R.id.dialog_tv);

        title.setText("Game Over!");
        dialog_tv.setText("Main Game Play Time has been Finished");

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertAttemptDetails(getAttemptId(), gameScore, wordsByApp, wordsByUser);
                alertDialog.cancel();
            }
        });

        new Dialog(getApplicationContext());
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        if (!wordChainActivity.isFinishing()) {
            alertDialog.show();
        }
    }

    private void insertAttemptDetails(int attemptId, int gameScore, ArrayList<String> wordsByApp, ArrayList<String> wordsByUser) {
        db.insertQuizAttemptMarks(gameScore);
        for (int i = 0; i < wordsByUser.size(); i++){
            db.insertQuizAttemptWords(attemptId, wordsByApp.get(i), wordsByUser.get(i));
        }
        Intent win = new Intent(WordChainActivity.this, WinningActivity.class);
        win.putExtra("attempt", getAttemptId());
        win.putExtra("score", gameScore);
        win.putExtra("finished", "yes");
        startActivity(win);
        this.finish();
    }

    private void addViewUserWord(LinearLayout viewUserWord, final String userWordChar) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.rightMargin = 2;
        layoutParams.leftMargin = 2;

        final TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams);
        textView.setBackground(this.getResources().getDrawable(R.drawable.ic_plus_icon));
        textView.setTextColor(this.getResources().getColor(R.color.primaryTextColor));
        textView.setGravity(Gravity.CENTER);
        textView.setText(userWordChar);
        textView.setTextSize(32);
        textView.setAllCaps(true);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one.ttf");
        textView.setTypeface(typeface);
        viewUserWord.addView(textView);
    }

    public void getUserInput(View view) {
        String userWord = userInputEditText.getText().toString();
        if (!userWord.equals("") && !userWord.contains(" ")){
            String[] userWordArr = new String[userWord.length()];
            for (int i = 0; i < userWord.length(); i++) {
                userWordArr[i] = String.valueOf(userWord.charAt(i));
            }

            boolean pass = checkUserWord(userWord, userWordArr[0]);
            if (pass){
                if (WordChainActivity.hasChildren(userWordLayout)){
                    userWordLayout.removeAllViews();
                }
                for (String key : userWordArr){
                    addViewUserWord((LinearLayout) findViewById(R.id.userWordLayout), key);
                }
                userInputEditText.setText("");
                lastLetterOfCurrentWordUser = userWordArr[userWord.length() -1];
                currentWordUser = userWord;
                wordsByUser.add(userWord);
                new CorrectWord().execute();
            }else {
                YoYo.with(Techniques.Shake)
                        .duration(1000)
                        .repeat(2)
                        .playOn(findViewById(R.id.rl_bottom));
            }
        }else {
            YoYo.with(Techniques.Shake)
                .duration(1000)
                .repeat(2)
                .playOn(findViewById(R.id.rl_bottom));
        }
    }

    public class CorrectWord extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (countDownTimer != null){
                countDownTimer.cancel();
            }
            showCorrectAnimation();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            wordsCounter++;
            wordsCompleted.setText(String.format("Words Completed : %d", wordsCounter));
            gameScore = gameScore + MARKS_PER_CORRECT_WORD;
            score.setText(String.format("Score : %d", gameScore));
            stopCorrectAnimation();
            generateNewAppWord();
        }
    }

    private void showCorrectAnimation() {
        animationFrame.setVisibility(View.VISIBLE);
        correctAnimationLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    private void stopCorrectAnimation(){
        animationFrame.setVisibility(View.GONE);
        correctAnimationLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void generateNewAppWord() {
        appWordLayout.removeAllViews();
        ArrayList<String> generated = getPossibleStrings(wordList, lastLetterOfCurrentWordUser);
        if (!generated.isEmpty()){
            suggestNewWordFromApp(generated);
        }else {
            gameScore = gameScore * 2;
            endAttempt();
        }
    }

    private boolean checkUserWord(String userWord, String s) {
        if (s.equals(lastLetterOfCurrentWordApp)){
            if (wordList.contains(userWord)){
                wordList.remove(userWord);
                return true;
            } else return false;
        }else return false;
    }

    public static ArrayList<String> getPossibleStrings(ArrayList<String> strings, String query) {
        ArrayList<String> result = new ArrayList<String>();
        for (String s: strings) {
            if (s.startsWith(query))
                result.add(s);
        }

        return result;
    }

    private int getWordListSize(){
        return wordList.size();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("CurrentAppWordKey", currentWordApp);
        outState.putString("CurrentUserWordKey", currentWordUser);
        outState.putString("CurrentAppWordLastLetterKey", lastLetterOfCurrentWordApp);
        outState.putString("CurrentUserWordLastLetterKey", lastLetterOfCurrentWordUser);
    }

    public static boolean hasChildren(ViewGroup viewGroup) {
        return viewGroup.getChildCount() > 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        if (mainGamePlayTimer != null){
            mainGamePlayTimer.cancel();
        }
    }
}
