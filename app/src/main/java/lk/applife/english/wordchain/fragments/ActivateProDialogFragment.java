package lk.applife.english.wordchain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.ui.EnterMobileNumberActivity;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) S.Hariprasanth
 * Date: 17-Nov-20
 * Time: 12:39 AM
 */
public class ActivateProDialogFragment extends DialogFragment {

    private static final String TAG = "ACTIVATE_PRO_DIALOG";

    Button neutralBtn;
    Button positiveBtn;

    public static ActivateProDialogFragment showActivateProDialog(FragmentManager fragmentManager){
        ActivateProDialogFragment activateProDialogFragment = new ActivateProDialogFragment();
        activateProDialogFragment.show(fragmentManager, TAG);
        return activateProDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_activate_pro_dialog, container, false);
        neutralBtn = (Button) view.findViewById(R.id.dialog_neutral_btn);
        positiveBtn = (Button) view.findViewById(R.id.dialog_positive_btn);

        neutralBtn.setOnClickListener(neutralBtnClickListener);
        positiveBtn.setOnClickListener(positiveBtnClickListener);
        return view;
    }

    private View.OnClickListener neutralBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private View.OnClickListener positiveBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent enter = new Intent(getActivity(), EnterMobileNumberActivity.class);
            startActivity(enter);
        }
    };
}
