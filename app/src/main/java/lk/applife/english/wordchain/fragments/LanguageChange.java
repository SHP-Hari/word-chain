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
    String LANG_CURRENT = "";
    private String languageCodeEnglish = "en";
    private String languageCodeSinhala = "si";
    private String languageCodeTamil = "ta";
    String selectedLang = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_language, container, false);
        spinner = view.findViewById(R.id.spinner);
        SharedPreferences mSettings = Objects.requireNonNull(getActivity()).getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        LANG_CURRENT = mSettings.getString("Language", "en");

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<>();
        categories.add("English");
        categories.add("සිංහල");
        categories.add("தமிழ்");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        setDefaultSelectedItem(LANG_CURRENT);
        view.findViewById(R.id.changeLanguageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLang != null){
                    swichLanguage(selectedLang);
                }
            }
        });

        return view;
    }

    private void setDefaultSelectedItem(String lang_current) {
        switch (lang_current){
            case "en" : {
                spinner.setSelection(0);
                break;
            }
            case "si" : {
                spinner.setSelection(1);
                break;
            }
            case "ta" : {
                spinner.setSelection(2);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + lang_current);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedLang = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedLang = spinner.getSelectedItem().toString();
        Toast.makeText(getActivity(), "onNothingSelected", Toast.LENGTH_SHORT).show();
    }

    private void swichLanguage(String selectedLang) {
        switch (selectedLang){
            case "English" : {
                changeLang(getActivity(), languageCodeEnglish);
                break;
            }
            case "සිංහල" : {
                changeLang(getActivity(), languageCodeSinhala);
                break;
            }
            case "தமிழ்" : {
                changeLang(getActivity(), languageCodeTamil);
                break;
            }
        }
    }

    public void changeLang(Context context, String lang) {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", lang);
        editor.apply();
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }

}
