package lk.applife.english.wordchain.model;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 08-Jun-20
 * Time: 12:55 AM
 */
public class Word {
    private int wordType;
    private String word;

    public Word(int wordType, String word) {
        this.wordType = wordType;
        this.word = word;
    }

    public int getWordType() {
        return wordType;
    }

    public void setWordType(int wordType) {
        this.wordType = wordType;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
