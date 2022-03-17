package ntu.n1043826.scanit.UI.Fragment.about;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ntu.n1043826.scanit.BuildConfig;
import ntu.n1043826.scanit.Helper.Constant;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.R;
import ntu.n1043826.scanit.UI.Activity.LoginActivity;


public class AboutFragment extends Fragment {



   @BindView(R.id.btn_rate)
   MaterialRippleLayout rate;

   @BindView(R.id.btn_share)
   MaterialRippleLayout share;


    SignInButton siginbutton;
    @BindView(R.id.profile_card_view)
    CardView profile_card;
    @BindView(R.id.cardView_about)
    CardView about_card;
    @BindView(R.id.profile_img)
    CircleImageView profile_image;
    @BindView(R.id.name)
    TextView nametext;
    @BindView(R.id.email)
    TextView emailtext;
    @BindView(R.id.logout)
    TextView logout;
    DataFire dataFire;
    private static final int RC_SIGN_IN = 234;
    private FirebaseAuth mAuth;

    GoogleSignInClient mGoogleSignInClient;
    SharedPreferences preferences;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("About");
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        dataFire=new DataFire();
        siginbutton=root.findViewById(R.id.sign_in_button);
        ButterKnife.bind(this,root);
         preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        siginbutton.setVisibility(View.GONE);
        nametext.setText(dataFire.getCurrentUserName());
        emailtext.setText(dataFire.getCurrentUserEmail());
        Context context = getContext();
        Picasso.get()
                .load(dataFire.getCurrentUserProfile())
                .into(profile_image);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });


        rate.setOnClickListener(v -> {

            Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
            }
        });

        share.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
            }
        });
        return root;
    }

}