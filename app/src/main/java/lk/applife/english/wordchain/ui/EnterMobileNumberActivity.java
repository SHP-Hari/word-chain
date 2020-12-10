package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.santalu.maskara.widget.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lk.applife.english.wordchain.BuildConfig;
import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.fragments.ActivationCodeFragment;
import lk.applife.english.wordchain.utill.CustomSnackbar;
import lk.applife.english.wordchain.utill.GlabalValues;

public class EnterMobileNumberActivity extends AppCompatActivity {

    public static final String REFERENCE_CODE = "reference_code";
    public static final String USER_CARRIER = "user_carrier";
    public static final String ACTIVATION_CODE_FRAGMENT_TAG = "VERIFY_CODE";
    public static final String USERPREFERENCE = "userPreference";
    public static final String REGISTERED_ALREADY = "E1351";
    private MaskEditText mPhoneNumber;
    Button submitBtn;
    private TextView mobileNumberError;
    FrameLayout activationCodeLayout;
    LinearLayout insertMobileNumLayout;
    FrameLayout fragmentPlaceholder;
    LinearLayout lessonInfoLayout;
    LinearLayout registrationWaitingLayout;
    String userMobileNumber;
    String userCarrier = null;
    SharedPreferences userpreference;
    private String android_id;
    private String device_model;
    private String device_os;
    private String os_name;
    CustomSnackbar snackbarNoConnection;
    ViewGroup rootView;
    int userSubscriptionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_number);
        rootView = findViewById(android.R.id.content);
        mPhoneNumber = (MaskEditText) findViewById(R.id.edit_text_phone);
        submitBtn = (Button) findViewById(R.id.submitNumber);
        mobileNumberError = (TextView) findViewById(R.id.mobileNumberError);
        activationCodeLayout = findViewById(R.id.activationCodeFragment);
        insertMobileNumLayout = (LinearLayout) findViewById(R.id.insertMobileNumLayout);
        registrationWaitingLayout = (LinearLayout) findViewById(R.id.registrationWaitingLayout);
        submitBtn.setOnClickListener(submitButtonClickListener);

        userpreference = this.getSharedPreferences(USERPREFERENCE, Context.MODE_PRIVATE);
        android_id = userpreference.getString("deviceId", "Android device");
        device_model = userpreference.getString("deviceModel", "Android device");
        device_os = userpreference.getString("deviceOS", "Android device");
        os_name = userpreference.getString("deviceOSName", "Android device");
        userSubscriptionStatus = userpreference.getInt("userSubscriptionStatus", 0);
        if (userSubscriptionStatus == 999){
            registrationWaitingLayout.setVisibility(View.VISIBLE);
        }else {
            registrationWaitingLayout.setVisibility(View.GONE);
        }
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
            if (!isValidSrilankanMobile(String.valueOf(mobile.charAt(2)))){
                showMobileNumError(true, "The number you have entered does not match any Srilankan Carrier");
            }else {
                selectMobileCarrier(mobile);
            }
        }
    }

    private void selectMobileCarrier(String mobile) {
        String carrier = mobile.substring(1,3);
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
        SharedPreferences.Editor editor = userpreference.edit();
        editor.putString("userMobileNumber", getString(R.string.mobileNumberPrefix)+userMobileNumber.substring(1));
        editor.apply();
        getOTP();
    }

    private void getOTP() {
        if (checkConnection()){
            submitBtn.setText(R.string.pleasewait);
            submitBtn.setEnabled(false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.SERVER_URL + "v1/pin-api/request",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean error = jsonObject.getBoolean("error");
                                if (!error) {
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    String refCode = dataObject.getString("reference_no");
                                    Bundle bundle = new Bundle();
                                    bundle.putString(REFERENCE_CODE, refCode);
                                    bundle.putString(USER_CARRIER, userCarrier);
                                    Fragment activationCodeFragment = new ActivationCodeFragment();
                                    activationCodeFragment.setArguments(bundle);
                                    loadFragment(activationCodeFragment, ACTIVATION_CODE_FRAGMENT_TAG);
                                } else {
                                    submitBtn.setText(R.string.submit);
                                    submitBtn.setEnabled(true);
                                    String err_code = jsonObject.getString("err_code");
                                    switch (err_code) {
                                        case REGISTERED_ALREADY :
                                            showMobileNumError(true, getString(R.string.already_registered));
                                            break;
                                        default:
                                            showMobileNumError(true, getString(R.string.err_something_wrong));
                                    }
                                    closeKeyboard();
                                }

                            } catch (JSONException e) {
                                Log.e("err", e.toString());
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EnterMobileNumberActivity.this, getString(R.string.pleaseTryAgain), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() {
                    String post_data;
                    post_data = "{\"subscriber_phone_no\" : \"" + getString(R.string.mobileNumberPrefix)+userMobileNumber.substring(1) + "\",\n" +
                            "\"application_hash\" : \"" + BuildConfig.APP_HASH + "\",\n" +
                            "\"provider\" : \"" + userCarrier + "\",\n" +
                            "\"device\" : \"" + device_model + "\",\n" +
                            "\"os\" : \"" + device_os + "\"}";
                    return post_data.getBytes();
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", GlabalValues.api_key);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }else {
            snackbarNoConnection = CustomSnackbar.make(rootView, CustomSnackbar.LENGTH_INDEFINITE).setText("No internet connection.");
            snackbarNoConnection.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOTP();
                }
            });
        }
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
        return ((String) contactNumber).matches("^[0][7][0-9]{8}$");
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

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) EnterMobileNumberActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }
}