package lk.applife.english.wordchain.utill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 07-Jun-20
 * Time: 10:42 AM
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "word_chain_db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS quiz_attempts" + " (attempt_id INTEGER PRIMARY KEY AUTOINCREMENT, marks INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS quiz_attempt_words" + " (quiz_attempt_words_id INTEGER PRIMARY KEY AUTOINCREMENT, attempt_id INTEGER, word_app text, word_user text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS quiz_attempts");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS quiz_attempt_words");

        onCreate(sqLiteDatabase);
    }

    public boolean insertQuizAttemptMarks(int marks){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("marks", marks);
        db.insert("quiz_attempts", null, cv);
        return true;
    }

    public boolean insertQuizAttemptWords(int attemptId, String appWord, String userWord){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("attempt_id", attemptId);
        cv.put("word_app", appWord);
        cv.put("word_user", userWord);
        db.insert("quiz_attempt_words", null, cv);
        return true;
    }

    public Cursor getMarksByAttempt(int attemptId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM quiz_attempts WHERE attempt_id = " + attemptId, null);
        return res;
    }

    public Cursor getAllAttempts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM quiz_attempts", null);
        return res;
    }

    public Cursor getAllWordsByAttempt(int attemptId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM quiz_attempt_words WHERE attempt_id = " + attemptId, null);
        return res;
    }

    public Cursor getAllAttemptWords(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM quiz_attempt_words", null);
        return res;
    }

    public Cursor getLastRowInMarks(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM quiz_attempts ORDER BY attempt_id DESC LIMIT 1", null);
        return res;
    }
}
