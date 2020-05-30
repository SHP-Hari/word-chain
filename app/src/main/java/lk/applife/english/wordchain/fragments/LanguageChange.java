package lk.applife.english.wordchain.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.ui.HomeActivity;

public class LanguageChange extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    boolean language = false;
    int check = 0;
    String LANG_CURRENT = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_language, container, false);
        spinner = view.findViewById(R.id.spinner);
        SharedPreferences mSettings = Objects.requireNonNull(getActivity()).getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        LANG_CURRENT = mSettings.getString("Language", "en");

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<>();
        //     categories.add(getString(R.string.selecthere));
        switch (LANG_CURRENT) {
            case "en":
                categories.add("English");
                categories.add("සිංහල");
                categories.add("சிங்களம்");
                break;
            case "sin":
                categories.add("සිංහල");
                categories.add("English");
                categories.add("சிங்களம்");
                break;
            default:
                categories.add("சிங்களம்");
                categories.add("English");
                categories.add("සිංහල");

                break;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (++check > 1) {
            // On selecting a spinner item
//            String item = parent.getItemAtPosition(position).toString();
            String item = "en";

            // Showing selected spinner item
            language = true;

            switch (item) {
                case "English": {
                    changeLang(getActivity(), "en");
                    break;
                }

                case "සිංහල": {
                    changeLang(getActivity(), "sin");
                    break;
                }

                case "சிங்களம்": {
                    changeLang(getActivity(), "ar");
                    break;
                }
            }

            startActivity(new Intent(getActivity(), HomeActivity.class));

        } else {
        }
    }

    public void changeLang(Context context, String lang) {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", lang);
        editor.apply();
    }

}
