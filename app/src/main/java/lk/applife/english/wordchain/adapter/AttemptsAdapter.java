package lk.applife.english.wordchain.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.model.UserAttempt;
import lk.applife.english.wordchain.ui.EnterMobileNumberActivity;
import lk.applife.english.wordchain.ui.HomeActivity;
import lk.applife.english.wordchain.ui.WordsActivity;
import lk.applife.english.wordchain.utill.CustomSnackbar;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 07-Jun-20
 * Time: 9:18 PM
 */
public class AttemptsAdapter extends RecyclerView.Adapter<AttemptsAdapter.AttemptViewHolder> {

    private Context context;
    private ArrayList<UserAttempt> userAttempts;
    private int checkedPosition = 0;
    SharedPreferences userPreference;

    public AttemptsAdapter(Context context, ArrayList<UserAttempt> userAttempts) {
        this.context = context;
        this.userAttempts = userAttempts;
        this.userPreference = context.getSharedPreferences(HomeActivity.USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setUserAttempts(ArrayList<UserAttempt> userAttempts) {
        this.userAttempts = new ArrayList<>();
        this.userAttempts = userAttempts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttemptsAdapter.AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_attempt, parent, false);
        return new AttemptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttemptsAdapter.AttemptViewHolder holder, int position) {
        holder.bindDetails(userAttempts.get(position));
    }

    @Override
    public int getItemCount() {
        return userAttempts.size();
    }

    class AttemptViewHolder extends RecyclerView.ViewHolder {

        private TextView attemptNumber;
        private TextView attemptMarks;
        private ImageView openWordsBtn;
        int userSubscriptionStatus = userPreference.getInt("userSubscriptionStatus", 0);
        CustomSnackbar snackbarSubscribed;
        public AttemptViewHolder(@NonNull View itemView) {
            super(itemView);
            attemptNumber = (TextView) itemView.findViewById(R.id.attemptNumber);
            attemptMarks = (TextView) itemView.findViewById(R.id.attemptMarks);
            openWordsBtn = (ImageView) itemView.findViewById(R.id.openWordsBtn);
            openWordsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userSubscriptionStatus != 1) {
                        Snackbar.make(attemptMarks, "This feature is only available for the Pro user", Snackbar.LENGTH_INDEFINITE).setAction("Activate", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, EnterMobileNumberActivity.class);
                                context.startActivity(intent);
                            }
                        }).show();
                    }else {
                        openWordsBtn.setVisibility(View.VISIBLE);
                        if (checkedPosition != getAdapterPosition()) {
                            notifyItemChanged(checkedPosition);
                            checkedPosition = getAdapterPosition();
                        }
                        Intent words = new Intent(context, WordsActivity.class);
                        words.putExtra("attempt_id", getSelected().getId());
                        context.startActivity(words);
                    }
                }
            });
        }

        void bindDetails(UserAttempt userAttempt){
            attemptNumber.setText(String.valueOf(userAttempt.getId()));
            attemptMarks.setText(String.valueOf(userAttempt.getMarks()));
        }
    }

    public UserAttempt getSelected() {
        if (checkedPosition != -1) {
            return userAttempts.get(checkedPosition);
        }
        return null;
    }
}
