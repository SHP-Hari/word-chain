package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santalu.maskara.widget.MaskEditText;

import java.util.Objects;

import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.fragments.ActivationCodeFragment;

public class EnterMobileNumberActivity extends AppCompatActivity {

    public static final String REFERENCE_CODE = "reference_code";
    public static final String USER_CARRIER = "user_carrier";
    public static final String ACTIVATION_CODE_FRAGMENT_TAG = "VERIFY_CODE";
    private MaskEditText mPhoneNumber;
    Button submitBtn;
    private TextView mobileNumberError;
    FrameLayout activationCodeLayout;
    LinearLayout insertMobileNumLayout;
    FrameLayout fragmentPlaceholder;
    LinearLayout lessonInfoLayout;
    String userMobileNumber;
    String userCarrier = null;
    SharedPreferences userpreference;
    private String android_id;
    private String device_model;
    private String device_os;
    private String os_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_number);
        mPhoneNumber = (MaskEditText) findViewById(R.id.edit_text_phone);
        submitBtn = (Button) findViewById(R.id.submitNumber);
        mobileNumberError = (TextView) findViewById(R.id.mobileNumberError);
        activationCodeLayout = findViewById(R.id.activationCodeFragment);
        insertMobileNumLayout = (LinearLayout) findViewById(R.id.insertMobileNumLayout);
        submitBtn.setOnClickListener(submitButtonClickListener);

        userpreference = this.getSharedPreferences("userPreference", Context.MODE_PRIVATE);
        android_id = userpreference.getString("deviceId", "Android device");
        device_model = userpreference.getString("deviceModel", "Android device");
        device_os = userpreference.getString("deviceOS", "Android device");
        os_name = userpreference.getString("deviceOSName", "Android device");
    }

    View.OnClickListener submitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mPhoneNumber.isDone()){
                showMobileNumError(true, getString(R.string.err_no_mobile_num));
            }else {
                showMobileNumError(false, "");
                userMobileNumber = mPhoneNumber.getUnMasked();
                checkMobileNumber(userMobileNumber);
            }
        }
    };

    private void checkMobileNumber(String mobile) {
        if (!isValidContactNumber(mobile)){
            showMobileNumError(true, "Please enter a valid Mobile Number");
        }else{
            if (!isValidSrilankanMobile(String.valueOf(mobile.charAt(1)))){
                showMobileNumError(true, "The number you have entered does not match any Srilankan Carrier");
            }else {
                selectMobileCarrier(mobile);
            }
        }
    }

    private void selectMobileCarrier(String mobile) {
        String carrier = mobile.substring(0,2);
        switch (carrier){
            case "77" :
            case "76" :
            case "74" :
                userCarrier = getString(R.string.DIALOG);
                break;
            case "71" :
            case "70" :
                userCarrier = getString(R.string.MOBITEL);
                break;
            case "78" :
            case "72" :
                userCarrier = getString(R.string.HUTCH);
                break;
            case "75" :
                userCarrier = getString(R.string.AIRTEL);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + carrier);
        }
        getOTP();
    }

    private void getOTP() {
        loadFragment(new ActivationCodeFragment(), ACTIVATION_CODE_FRAGMENT_TAG);
    }

    private void showMobileNumError(boolean error, String msg) {
        if (error){
            mobileNumberError.setVisibility(View.VISIBLE);
            mobileNumberError.setText(msg);
        }else {
            mobileNumberError.setVisibility(View.GONE);
        }
    }

    private void loadFragment(Fragment fragment, String fragmentTag) {
        insertMobileNumLayout.setVisibility(View.GONE);
        activationCodeLayout.setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.activationCodeFragment, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static boolean isValidContactNumber(String text) {
        CharSequence contactNumber = text;
        return ((String) contactNumber).matches("^[7][0-9]{8}$");
    }

    public static boolean isValidSrilankanMobile(String num){
        return ((String) num).matches("^[01245678]$");
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}