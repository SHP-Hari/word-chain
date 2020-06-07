package lk.applife.english.wordchain.utill;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.ui.ProfileActivity;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 07-Jun-20
 * Time: 3:23 PM
 */
public class UserNameDialog extends DialogFragment {

    TextInputEditText nameet;
    TextInputLayout usernameLayout;
    Button negativeBtn;
    Button positiveBtn;
    private UserNameDialogListener userNameDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_alertdialog_username, null);

        nameet = view.findViewById(R.id.name);
        usernameLayout = (TextInputLayout) view.findViewById(R.id.usernameLayout);
        negativeBtn = (Button) view.findViewById(R.id.dialog_neutral_btn);
        positiveBtn = (Button) view.findViewById(R.id.dialog_positive_btn);

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void validateInput() {
        boolean cancel = false;
        View focusView = null;

        if (ProfileActivity.isEmpty(nameet)) {
            usernameLayout.setError(getResources().getString(R.string.emptyUsernaName));
            focusView = nameet;
            cancel = true;
        } else if (!ProfileActivity.isLettersOnly(nameet)) {
            usernameLayout.setError(getResources().getString(R.string.invalidUserName));
            focusView = nameet;
            cancel = true;
        } else {
            usernameLayout.setError(null);
            usernameLayout.setErrorEnabled(false);
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            insertUserDetails();
        }
    }

    private void insertUserDetails() {
        String un = nameet.getText().toString();
        userNameDialogListener.applyInfo(un);
    }

    public interface UserNameDialogListener{
         void applyInfo(String name);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            userNameDialogListener = (UserNameDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
