package ntu.n1043826.scanit.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ntu.n1043826.scanit.R;
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            if (restorePrefData())
            {
                if (isLoggedIn()) {
                    Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
                else {
                    Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
            else {
                startActivity(new Intent(SplashActivity.this.getApplicationContext(), IntroActivity.class));
                finish();
            }
        }, 500);
    }
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }
    private boolean isLoggedIn() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            return true;
        }
        return false;
    }
}