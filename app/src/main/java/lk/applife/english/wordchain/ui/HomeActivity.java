package lk.applife.english.wordchain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lk.applife.english.wordchain.BuildConfig;
import lk.applife.english.wordchain.R;
import lk.applife.english.wordchain.fragments.ActivateProDialogFragment;
import lk.applife.english.wordchain.fragments.LanguageChange;
import lk.applife.english.wordchain.utill.CustomSnackbar;
import lk.applife.english.wordchain.utill.DatabaseHelper;
import lk.applife.english.wordchain.utill.GlabalValues;
import lk.applife.english.wordchain.utill.MyBounceInterpolator;
import lk.applife.english.wordchain.utill.MyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    public static final String USER_INFO_PREFERENCES = "userInformation";
    public static final String USER_PREFERENCES = "userPreference";
    public static final String SUBSCRIBED = "SUBSCRIBED";
    LinearLayout mainLayout;
    ViewGroup rootView;
    Button openBottomSheet;
    TextView userWelcome;
    SharedPreferences userInfoPreference, userPreference;
    String userName;
    DatabaseHelper db;
    String LANG_CURRENT = "";
    CustomSnackbar snackbarNoConnection, snackbarSubscribed;
    String userMobile;
    ImageView userProfile;
    Animation animation;
    int userSubscriptionStatus;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        animation.setInterpolator(interpolator);
        Animation connectingAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_scale_animation);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        rootView = findViewById(android.R.id.content);
        openBottomSheet = (Button) findViewById(R.id.openLanguageSheetBtn);
        userWelcome = (TextView) findViewById(R.id.userWelcome);
        userProfile = (ImageView) findViewById(R.id.userProfile);
        userProfile.startAnimation(connectingAnimation);
        userInfoPreference = getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        userPreference = this.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        userName = userInfoPreference.getString("username", null);
        userMobile = userPreference.getString("userMobileNumber", null);
        db = new DatabaseHelper(this);

        if (userName != null){
            userWelcome.setText(getString(R.string.hello) + userName);
        }
        openBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageChange languageChangeSheet = new LanguageChange();
                languageChangeSheet.show(getSupportFragmentManager(), "CHANGE_LANGUAGE");
            }
        });
        initial();
    }

    private void initial() {
        if (checkConnection()){
            if (userMobile != null){
                fetchUserSubscriptionStatus();
            }
        }else {
            snackbarNoConnection = CustomSnackbar.make(rootView, CustomSnackbar.LENGTH_INDEFINITE).setText("No internet connection.");
            snackbarNoConnection.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initial();
                }
            });
        }
    }

    private void fetchUserSubscriptionStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BuildConfig.SERVER_URL + "v1/subscription/" + userMobile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                JSONObject initial = jsonObject.getJSONObject("data");
                                SharedPreferences.Editor editor = userPreference.edit();
                                if (initial.getString("subscription_status").equals(SUBSCRIBED)){
                                    editor.putInt("userSubscriptionStatus", 1);
                                }else {
                                    editor.putInt("userSubscriptionStatus", 0);
                                }
                                editor.apply();
                            }
                        } catch (JSONException e) {
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", GlabalValues.api_key);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void startHowToPlay(View view) {
        Intent howToPlay = new Intent(HomeActivity.this, HowToPlay.class);
        startActivity(howToPlay);
    }

    public void startWordChain(View view) {
        if (snackbarNoConnection != null){
            Toast.makeText(this, getResources().getString(R.string.unableToGetSubsStatus), Toast.LENGTH_SHORT).show();
        }else {
            Intent wordChain = new Intent(HomeActivity.this, WordChainActivity.class);
            startActivity(wordChain);
        }
    }

    public void startActivatePro(View view) {
        if (checkConnection()) {
            int userSubscriptionStatus = userPreference.getInt("userSubscriptionStatus", 0);
            if (userSubscriptionStatus != 1) {
                Intent intent = new Intent(HomeActivity.this, EnterMobileNumberActivity.class);
                startActivity(intent);
            } else {
                Snackbar.make(rootView, getResources().getString(R.string.alreadyPremiumUser), Snackbar.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        ActivateProDialogFragment.showActivateProDialog(fragmentManager);
//        Intent intent = new Intent(HomeActivity.this, WinningActivity.class);
//        startActivity(intent);
    }

    public void startProfile(View view) {
        view.startAnimation(animation);
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        userName = userInfoPreference.getString("username", null);
        if (userName != null){
            userWelcome.setText(getString(R.string.hello) + userName);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("userinfo", MODE_PRIVATE);
        LANG_CURRENT = preferences.getString("Language", "en");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT));
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) HomeActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }
}
