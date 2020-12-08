package lk.applife.english.wordchain.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lk.applife.english.wordchain.BuildConfig;
import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.interfaces.OtpReceivedInterface;
import lk.applife.english.wordchain.ui.EnterMobileNumberActivity;
import lk.applife.english.wordchain.ui.HomeActivity;
import lk.applife.english.wordchain.utill.CustomSnackbar;
import lk.applife.english.wordchain.utill.GlabalValues;
import lk.applife.english.wordchain.utill.SmsBroadcastReceiver;


public class ActivationCodeFragment extends Fragment implements OtpReceivedInterface {

    public static final String TAG = "ActivationCodeFragment";
    public static final String CODE_NOT_FOUND = "E1850";
    public static final String CODE_EXPIRED = "EC1001";
//    public static final String SUBSCRIPTION_INACTIVE = "E1850";
    public static final String INSERT_MOBILENUM_FRAGMENT = "ENTER_MOBILE_NUM";
    private static final long START_TIME_IN_MILLIS = 60000;

    SmsBroadcastReceiver mSmsBroadcastReceiver;
    FrameLayout fragmentPlaceholder;
    LinearLayout mobileNumLayout;
    Button submitBtn;
    Button verifyCode;
    private EditText et1, et2, et3, et4, et5, et6;
    private EditText[] editTexts;
    TextView didnotGetCode;
    TextView subscriptionCodeError;
    TextView countdown;
    String activationCode = null;
    LinearLayout insertCodeLayout;
    LinearLayout messageDetailsLayout;
    LinearLayout didnotGetCodeLayout;
    LinearLayout coundownLayout;
    SharedPreferences userpreference;
    private String android_id;
    private String referenceNumber = null;
    private String userCarrier = null;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    CustomSnackbar snackbarNoConnection;

    public ActivationCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_activation_code, container, false);
        referenceNumber = this.getArguments().getString(EnterMobileNumberActivity.REFERENCE_CODE);
        userCarrier = this.getArguments().getString(EnterMobileNumberActivity.USER_CARRIER);
        fragmentPlaceholder = getActivity().findViewById(R.id.activationCodeFragment);
        mobileNumLayout = getActivity().findViewById(R.id.insertMobileNumLayout);
        submitBtn = getActivity().findViewById(R.id.submitNumber);
        verifyCode = view.findViewById(R.id.activateCode);
        didnotGetCode = view.findViewById(R.id.didnotGetCode);
        countdown = view.findViewById(R.id.countdown);
        subscriptionCodeError = view.findViewById(R.id.subscriptionCodeError);
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        editTexts = new EditText[]{et1, et2, et3, et4, et5, et6};
        insertCodeLayout = (LinearLayout) view.findViewById(R.id.insertCodeLayout);
        didnotGetCodeLayout = (LinearLayout) view.findViewById(R.id.didnotGetCodeLayout);
        coundownLayout = (LinearLayout) view.findViewById(R.id.coundownLayout);

        didnotGetCodeLayout.setVisibility(View.GONE);
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        startSMSListener();

        userpreference = getActivity().getSharedPreferences("userPreference", Context.MODE_PRIVATE);
        android_id = userpreference.getString("deviceId", "Android device");

        SpannableString content = new SpannableString(getString(R.string.notReceivedActivationCode));
        content.setSpan(new UnderlineSpan(), 0, getString(R.string.notReceivedActivationCode).length(), 0);
        didnotGetCode.setText(content);

        et1.addTextChangedListener(new PinTextWatcher(0));
        et2.addTextChangedListener(new PinTextWatcher(1));
        et3.addTextChangedListener(new PinTextWatcher(2));
        et4.addTextChangedListener(new PinTextWatcher(3));
        et5.addTextChangedListener(new PinTextWatcher(4));
        et6.addTextChangedListener(new PinTextWatcher(5));

        et1.setOnKeyListener(new PinOnKeyListener(0));
        et2.setOnKeyListener(new PinOnKeyListener(1));
        et3.setOnKeyListener(new PinOnKeyListener(2));
        et4.setOnKeyListener(new PinOnKeyListener(3));
        et5.setOnKeyListener(new PinOnKeyListener(4));
        et6.setOnKeyListener(new PinOnKeyListener(5));

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((activationCode == null) || (activationCode.length() != 6)) {
                    subscriptionCodeError.setVisibility(View.VISIBLE);
                    subscriptionCodeError.setText(R.string.classifiedSubscriptionCodeError);
                } else {
                    sendOTPToServer();
                }
            }
        });

        didnotGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetach();
            }
        });

        startTimer();
        updateCountDownText();

        return view;
    }

    public void startTimer() {
        coundownLayout.setVisibility(View.VISIBLE);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                didnotGetCodeLayout.setVisibility(View.VISIBLE);
                coundownLayout.setVisibility(View.GONE);
            }
        }.start();
        mTimerRunning = true;
        didnotGetCodeLayout.setVisibility(View.GONE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdown.setText(timeLeftFormatted);
    }

    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
            activationCode = getTypedCode();
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                activationCode = getTypedCode();
//                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

//        @SuppressLint("NewApi")
//        private void hideKeyboard() {
//            final InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//        }

        private String getTypedCode() {
            String code = "";
            for (EditText editText : editTexts) {
                code += editText.getText().toString().trim();
            }
            return code;
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            subscriptionCodeError.setVisibility(View.GONE);
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                activationCode = null;
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }

    private void sendOTPToServer() {
        if (checkConnection()){
            subscriptionCodeError.setVisibility(View.GONE);
            verifyCode.setText(R.string.pleasewait);
            verifyCode.setEnabled(false);
            final Activity activity = getActivity();
            String OTP_VERIFICATION_POST = BuildConfig.SERVER_URL + "v1/pin-api/verify";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, OTP_VERIFICATION_POST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (activity != null && isAdded()){
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Boolean error = jsonObject.getBoolean("error");

                                    if (!error) {
                                        Snackbar.make(insertCodeLayout, "Code Verified Successfully", Snackbar.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), HomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        verifyCode.setText(R.string.activateSubscription);
                                        verifyCode.setEnabled(true);
                                        subscriptionCodeError.setVisibility(View.VISIBLE);
                                        String errorCode = null;
                                        errorCode = jsonObject.getString("err_code");
                                        if (errorCode.equals(CODE_NOT_FOUND)) {
                                            subscriptionCodeError.setText(getString(R.string.subscriptionCodeNotFound));
                                        } else if (errorCode.equals(CODE_EXPIRED)) {
                                            subscriptionCodeError.setText(getString(R.string.subscriptionCodeExpired));
                                        } else {
                                            subscriptionCodeError.setText(getString(R.string.pleaseTryAgain));
                                        }
                                    }
                                } catch (JSONException e) {
                                }
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {

                @Override
                public byte[] getBody() {
                    String post_data;
                    post_data = "{\"reference_no\" : \"" + referenceNumber + "\",\n" +
                            "\"otp\" : \"" + activationCode + "\",\n" +
                            "\"provider\" : \"" + userCarrier + "\"}";
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
            RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            requestQueue.add(stringRequest);
        }else {
            snackbarNoConnection = CustomSnackbar.make(insertCodeLayout, CustomSnackbar.LENGTH_INDEFINITE).setText("No internet connection.");
            snackbarNoConnection.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendOTPToServer();
                }
            });
        }
    }

    @Override
    public void onOtpReceived(String otp) {
        getOtpFromMessage(otp);
    }

    @Override
    public void onOtpTimeout() {
        Log.d(TAG, "Time out, please resend");
    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(getActivity());
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void getOtpFromMessage(String message) {
        Pattern pattern = Pattern.compile("(Your PIN is) (.{6})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String subscriptionCode = matcher.group(2);
            if (subscriptionCode != null) {
                for (int i = 0; i < subscriptionCode.length(); i++){
                    editTexts[i].setText(String.valueOf(subscriptionCode.charAt(i)));
                }
                editTexts[editTexts.length-1].requestFocus();
                sendOTPToServer();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mobileNumLayout.setVisibility(View.VISIBLE);
        fragmentPlaceholder.setVisibility(View.GONE);
        submitBtn.setText(R.string.submit);
        submitBtn.setEnabled(true);
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }
}