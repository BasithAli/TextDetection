package ntu.n1043826.scanit.UI.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Model.QRModel;
import ntu.n1043826.scanit.R;
import ntu.n1043826.scanit.UI.Fragment.Home.HomeFragment;
import ntu.n1043826.scanit.UI.Fragment.OCR.OCRFragment;
import ntu.n1043826.scanit.UI.Fragment.QRCode.QrCodeFragment;
import ntu.n1043826.scanit.UI.Fragment.about.AboutFragment;


public class HomeActivity extends AppCompatActivity {
    Toolbar myToolbar;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    MenuItem prevMenuItem;
    int pager_number = 4;
    public static final int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    private DataFire dataFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        myToolbar.setTitle(getTitle());
        FirebaseApp.initializeApp(getApplicationContext());
        initToolbarIcon();
        fn_permission();
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);
        dataFire=new DataFire();

        navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_favorite:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 0) {
                    myToolbar.setTitle(getResources().getString(R.string.app_name));

                } else if (viewPager.getCurrentItem() == 1) {
                    myToolbar.setTitle(getResources().getString(R.string.title_ocr));

                } else if (viewPager.getCurrentItem() == 2) {
                    myToolbar.setTitle(getResources().getString(R.string.title_qrcode));

                }
                else if (viewPager.getCurrentItem() == 3) {
                    myToolbar.setTitle(getResources().getString(R.string.title_about));

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void initToolbarIcon() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem((0), true);
        } else {
            finish();
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new OCRFragment();
                case 2:
                    return new QrCodeFragment();
                case 3:
                    return new AboutFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager_number;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFirebaseUser==null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            &&(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
//                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("messageText",intentResult.getContents());
                checkType(intentResult.getContents());


//                messageText.setText(intentResult.getContents());
//                messageFormat.setText(intentResult.getFormatName());
            }
        } else {
//            Toast.makeText(this, "intent result null"+"", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public  void checkType(String text) {

        if( Patterns.WEB_URL.matcher(text).matches())
        {
            StoreData("url",text);
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(text));
            startActivity(intent);
        }
        else if(text.startsWith("tel:"))
        {
            StoreData("phone",text.substring(text.indexOf(":") + 1));
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel: "+text.substring(text.indexOf(":") + 1)));
            startActivity(callIntent);
        }
        else if(text.startsWith("sms:"))
        {
            StoreData("sms",text.substring(text.indexOf(":") + 1));
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", text.substring(text.indexOf(":") + 1));
             startActivity(sendIntent);
        }
        else if(text.startsWith("mailto:"))
        {
            StoreData("mail",text.substring(text.indexOf(":") + 1));
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            mailIntent.setData(Uri.parse("mailto:"+text.substring(text.indexOf(":") + 1)));
           startActivity(mailIntent);
        }
        else

        {
            StoreData("text",text);
            AlertDialog.Builder builder0=new AlertDialog.Builder(this);
            builder0.setMessage(text);
            builder0.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog0=builder0.create();
            dialog0.show();
        }
    }

    public static boolean isEmail(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean isPhone(String text) {
        if(!TextUtils.isEmpty(text)){
            return TextUtils.isDigitsOnly(text);
        } else{
            return false;
        }
    }
    public void StoreData(String type, String text)
    {
        String key=dataFire.getKey();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        QRModel qrModel = new QRModel(key,type,text,date);
        dataFire.getDocumentsRef().child("QRCODE").child(dataFire.getUserID()).child(key).setValue(qrModel).
                addOnCompleteListener(task -> {
                    Toast.makeText(getApplicationContext(), "QRCODE Added", Toast.LENGTH_SHORT).show();

                });

    }
}