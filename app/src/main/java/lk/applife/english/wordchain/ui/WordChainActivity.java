package lk.applife.english.wordchain.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.utill.FCS;

public class WordChainActivity extends AppCompatActivity {

    public static final String WORDS_TEXT_FILE_PATH = "words/words_alpha.txt";
    EditText userInputEditText;
    Button submit;
    HorizontalScrollView appWordHorizontalScroll;
    LinearLayout appWordLayout;
    LinearLayout userWordLayout;
    LinearLayout loadingLayout;
    LinearLayout mainLayout;
    LinearLayout correctAnimationLayout;
    TextView appWord;
    FrameLayout animationFrame;

    ArrayList<String> wordList;
    String currentWordApp;
    String currentWordUser;
    String lastLetterOfCurrentWordApp;
    String lastLetterOfCurrentWordUser;

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
        correctAnimationLayout = (LinearLayout) findViewById(R.id.correctAnimationLayout);
        appWord = (TextView) findViewById(R.id.tvSuggestedWord);
        animationFrame = (FrameLayout) findViewById(R.id.animationFrame);
        wordList = new ArrayList<>();

        new InitialWord().execute();
    }

    private void showLoadingAnimation() {
        animationFrame.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    private void closeLoadingAnimation(){
        animationFrame.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            closeLoadingAnimation();
            selectTheDefaultStaringWord();
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
        Log.d("TAG", "wordlist size : "+wordList.size());
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
            spanString.setSpan(new Ani, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        appWord.setText(spanString);
        Log.d("TAG", "start Word : "+suggestedWord);
        currentWordApp = suggestedWord;
        wordList.remove(suggestedWord);
        String[] appWordArr = new String[suggestedWord.length()];
        for (int i = 0; i < suggestedWord.length(); i++) {
            appWordArr[i] = String.valueOf(suggestedWord.charAt(i));
            lastLetterOfCurrentWordApp = String.valueOf(suggestedWord.charAt(i));
        }
        for (String key : appWordArr){
            addViewAppWord((LinearLayout) findViewById(R.id.appWordLayout), key);
        }
        Log.d("TAG", "last letter app : "+lastLetterOfCurrentWordApp);
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
        Log.d("TAG", "start Word : "+startWord);
        currentWordApp = startWord;
        wordList.remove(randomNumber);
        String[] appWordArr = new String[startWord.length()];
        for (int i = 0; i < startWord.length(); i++) {
            appWordArr[i] = String.valueOf(startWord.charAt(i));
            lastLetterOfCurrentWordApp = String.valueOf(startWord.charAt(i));
        }
        for (String key : appWordArr){
            addViewAppWord((LinearLayout) findViewById(R.id.appWordLayout), key);
        }
        Log.d("TAG", "last letter app : "+lastLetterOfCurrentWordApp);
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
//        YoYo.with(Techniques.SlideInUp)
//                .duration(1000)
//                .repeat(2)
//                .playOn(textView);

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
        Log.d("TAG", "Last letter user : "+lastLetterOfCurrentWordUser);
        Log.d("TAG", "Current word user : "+currentWordUser);
    }

    public class CorrectWord extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
            Log.d("TAG", "generated list size : "+generated.size());
            suggestNewWordFromApp(generated);
        }else {
            Toast.makeText(this, "list empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkUserWord(String userWord, String s) {
        if (s.equals(lastLetterOfCurrentWordApp)){
            if (wordList.contains(userWord)){
                Log.d("TAG", "index is : "+wordList.indexOf(userWord));
                wordList.remove(userWord);
                Log.d("TAG", "word list is : "+getWordListSize());
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
}
