package lk.applife.english.wordchain.model;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 07-Jun-20
 * Time: 9:22 PM
 */
public class UserAttempt {
    private int id;
    private int marks;
    private ArrayList<String> appWordArrayList;
    private ArrayList<String> userWordArrayList;

    public UserAttempt(int id, int marks, ArrayList<String> appWordArrayList, ArrayList<String> userWordArrayList) {
        this.id = id;
        this.marks = marks;
        this.appWordArrayList = appWordArrayList;
        this.userWordArrayList = userWordArrayList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public ArrayList<String> getAppWordArrayList() {
        return appWordArrayList;
    }

    public void setAppWordArrayList(ArrayList<String> appWordArrayList) {
        this.appWordArrayList = appWordArrayList;
    }

    public ArrayList<String> getUserWordArrayList() {
        return userWordArrayList;
    }

    public void setUserWordArrayList(ArrayList<String> userWordArrayList) {
        this.userWordArrayList = userWordArrayList;
    }
}
