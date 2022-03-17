package ntu.n1043826.scanit.UI.Fragment.QRCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import ntu.n1043826.scanit.Adapter.QRCodeAdapter;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Model.QRModel;
import ntu.n1043826.scanit.R;

public class QrCodeFragment extends Fragment implements  View.OnClickListener
{

    FloatingActionButton btn_select;
    RecyclerView recyclerView;
    ImageView nodata;
    List<QRModel> list = new ArrayList<>();
    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    QRCodeAdapter qrCodeAdapter;
    private DataFire dataFire;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    private Bitmap bitmapNew;
    public static final int REQUEST_PERMISSIONS = 1;
    String[] category_items = new String[]{"Others", "Books", "Medicals", "Shopping", "Vehicle", "Notes", "Food", "Banking", "Recipts", "Manuals", "Travel", "Id", "Legal"};
    Spinner dropdown;
    String selectedCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view=inflater.inflate(R.layout.fragment_qr_code, container, false);
        dataFire=new DataFire();

         init(view);
         listener();
        return view;
    }
    private void init(View view) {
        btn_select =  view.findViewById(R.id.btn_select);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        recyclerView = view.findViewById(R.id.recyclerview);
        nodata = view.findViewById(R.id.empty);
        layout = view.findViewById(R.id.layout);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        qrCodeAdapter = new QRCodeAdapter(list, getContext());
        recyclerView.setAdapter(qrCodeAdapter);


        dataFire.getQRDataRef().child(dataFire.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {

                    btn_select.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                } else {
                    btn_select.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);

                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    QRModel qrModel = dataSnapshot1.getValue(QRModel.class);
                    list.add(qrModel);

                }
                qrCodeAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void listener() {
        btn_select.setOnClickListener(this);
      
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:

                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setPrompt("Scan QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), resultCode+"", Toast.LENGTH_SHORT).show();
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), intentResult.getContents(), Toast.LENGTH_SHORT).show();

                // if the intentResult is not null we'll set
                // the content and format of scan message
                Log.d("messageText",intentResult.getContents());
                Log.d("messageFormat",intentResult.getFormatName());

//                messageText.setText(intentResult.getContents());
//                messageFormat.setText(intentResult.getFormatName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}