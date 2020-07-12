package lk.applife.english.wordchain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.model.UserAttempt;
import lk.applife.english.wordchain.model.Word;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 08-Jun-20
 * Time: 12:43 AM
 */
public class WordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_APP_WORD = 1;
    private static int TYPE_USER_WORD = 2;
    private Context context;
    private ArrayList<Word> words;

    public WordsAdapter(Context context, ArrayList<Word> words) {
        this.context = context;
        this.words = words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = new ArrayList<>();
        this.words = words;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_APP_WORD){
            view = LayoutInflater.from(context).inflate(R.layout.item_app_word, parent, false);
            return new AppWordViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_user_word, parent, false);
            return new UserWordViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return words.get(position).getWordType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_APP_WORD){
            ((AppWordViewHolder) holder).setAppWordInfo(words.get(position));
        }else {
            ((UserWordViewHolder) holder).setUserWordInfo(words.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.words.size();
    }

    public class AppWordViewHolder extends RecyclerView.ViewHolder {

        TextView text_app_word;
        public AppWordViewHolder(@NonNull View itemView) {
            super(itemView);
            text_app_word = (TextView) itemView.findViewById(R.id.text_app_word);
        }

        void setAppWordInfo(Word word){
            text_app_word.setText(word.getWord());
        }
    }

    public class UserWordViewHolder extends RecyclerView.ViewHolder {

        TextView text_user_word;
        public UserWordViewHolder(@NonNull View itemView) {
            super(itemView);
            text_user_word = (TextView) itemView.findViewById(R.id.text_user_word);
        }

        void setUserWordInfo(Word word){
            text_user_word.setText(word.getWord());
        }
    }
}
