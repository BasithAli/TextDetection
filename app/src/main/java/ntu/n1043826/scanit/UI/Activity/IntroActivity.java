package ntu.n1043826.scanit.UI.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import ntu.n1043826.scanit.Adapter.IntroViewPagerAdapter;
import ntu.n1043826.scanit.Model.WelcomeScreenItem;
import ntu.n1043826.scanit.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class );
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        final List<WelcomeScreenItem> mList = new ArrayList<>();
        mList.add(new WelcomeScreenItem("Scan Documents","Scan documents, photos, receipts, reports, or just about anything",R.drawable.img1));
        mList.add(new WelcomeScreenItem("Extract Text","Scan and Extract text from image & save Document as text and PDF file.",R.drawable.img2));
        mList.add(new WelcomeScreenItem("Organized Documents"," Easily manage saved documents with name and category.",R.drawable.img3));
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        tabIndicator.setupWithViewPager(screenPager);
        btnNext.setOnClickListener(v -> {

            position = screenPager.getCurrentItem();
            if (position < mList.size()) {

                position++;
                screenPager.setCurrentItem(position);


            }

            if (position == mList.size()-1) { // when we rech to the last screen

                // TODO : show the GETSTARTED Button and hide the indicator and the next button

                loaddLastScreen();


            }



        });
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        btnGetStarted.setOnClickListener(v -> {
            Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(mainActivity);
            savePrefsData();
            finish();
        });
        tvSkip.setOnClickListener(v -> screenPager.setCurrentItem(mList.size()));
    }
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();


    }
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
    }
}
