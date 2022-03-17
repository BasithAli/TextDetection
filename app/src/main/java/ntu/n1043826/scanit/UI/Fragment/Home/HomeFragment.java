package ntu.n1043826.scanit.UI.Fragment.Home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ntu.n1043826.scanit.Adapter.PDFAdapter;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Helper.ImageOrientation;

import ntu.n1043826.scanit.Model.PdfModel;
import ntu.n1043826.scanit.R;

import static android.app.Activity.RESULT_OK;
import static ntu.n1043826.scanit.UI.Fragment.OCR.OCRFragment.calculateInSampleSize;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final int GALLERY_PICTURE = 1;

    FloatingActionButton btn_select;
    RecyclerView recyclerView;
    ImageView nodata;
    List<PdfModel> list = new ArrayList<>();
    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    PDFAdapter notesAdapter;
    private DataFire dataFire;

    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    private Bitmap bitmapNew;
    public static final int REQUEST_PERMISSIONS = 1;
    String[] category_items = new String[]{"Others", "Books", "Medicals", "Shopping", "Vehicle", "Notes", "Food", "Banking", "Recipts", "Manuals", "Travel", "Id", "Legal"};
    Spinner dropdown;
    String selectedCategory;
    String TEMP_PHOTO_FILE="/temporary_holder.jpeg";
    public static final int PICK_FROM_GALLERY = 111;
    public static final int PICK_FROM_CAMERA = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        dataFire=new DataFire();
        init(view);
        listener();
        fn_permission();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new PDFAdapter(list, getContext());
        recyclerView.setAdapter(notesAdapter);

        dataFire.getDocumentsPdfRef().child(dataFire.getUserID()).addValueEventListener(new ValueEventListener() {
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
                    PdfModel pdfModel = dataSnapshot1.getValue(PdfModel.class);
                    list.add(pdfModel);

                }
                notesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return  view;
    }
    private void init(View view) {
        btn_select =  view.findViewById(R.id.btn_select);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        recyclerView = view.findViewById(R.id.recyclerview);
        nodata = view.findViewById(R.id.empty);
        layout = view.findViewById(R.id.layout);

    }
    private void listener() {
        btn_select.setOnClickListener(this);

    }
    private void chooseImage() {
        new AlertDialog.Builder(getContext())
                .setTitle("Choose Image From ")
                .setPositiveButton("Gallery", (dialogInterface, i) -> {
                    //set what would happen when positive button is clicked

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    photoPickerIntent.putExtra("crop", "true");
                    photoPickerIntent.putExtra("scale", true);
                    photoPickerIntent.putExtra("outputX", 256);
                    photoPickerIntent.putExtra("outputY", 256);
                    photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                    startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
                })
                .setNegativeButton("Camera",  (dialogInterface, i) -> {
                    //set what would happen when positive button is clicked

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePictureIntent, 1);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                    }
                })
                .show();
    }
    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }
    private File getTempFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
            try {
                file.createNewFile();
            } catch (IOException e) {

            }

            return file;
        } else {

            return null;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                    chooseImage();
                break;



        }
    }
    private void createPdf(String filename){

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/"+getString(R.string.app_name)+"/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + filename+".pdf";
        File filePath = new File(targetPdf);
        Log.d("FIlePath",targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getContext(), "PDF Downloaded in "+getString(R.string.app_name), Toast.LENGTH_LONG).show();
            Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            uri = Uri.fromFile(filePath);
            pdfViewIntent.setDataAndType(uri,"application/pdf");
            pdfViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(pdfViewIntent, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(getContext(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    private void uploadImage(Uri uri,String resultText)
    {

        DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
        String getDateYears = dfYears.format(Calendar.getInstance().getTime());
        setImage(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] data1 = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = dataFire.getStorageref().child("PDF")
                .child(dataFire.getUserID())
                .child(getDateYears + ".jpg");
        UploadTask uploadTask = filePath.putBytes(data1);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return filePath.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                    Log.d("ImageUrl",photoStringLink);
                    savenotes_dialog(resultText,photoStringLink);
                }

            } else {

            }
        });

    }
    private void setImage(Uri imageUri){
        try {
            //..First convert the Image to the allowable size so app do not throw Memory_Out_Bound Exception
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), null, options);
            int resolution = 500;
            options.inSampleSize = calculateInSampleSize(options, resolution  , resolution);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), null, options);

            //...Now You have the 'bitmap' to rotate....
            //...Rotate the bitmap to its original Orientation...
            bitmapNew = ImageOrientation.modifyOrientation(getContext(),bitmap,imageUri);

        } catch (Exception e) {
            Log.d("Image_exception",e.toString());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult",resultCode+"");
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PICK_FROM_CAMERA:
                        Bundle extras = data.getExtras();
                        bitmap = (Bitmap) extras.get("data");
                        final ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Converting...",
                                true);
                        dialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }}, 7000);

                        Uri resultUri = getImageUri(getContext(),bitmap);
                        uploadImage(resultUri,"Sample");

                        break;
                    case PICK_FROM_GALLERY:

                        File tempFile = getTempFile();
                        String filePath = Environment.getExternalStorageDirectory() + "/" + TEMP_PHOTO_FILE;

                         bitmap = BitmapFactory.decodeFile(filePath);
                        if (bitmap != null) {
                            final ProgressDialog dialog1 = ProgressDialog.show(getContext(), "", "Converting...",
                                    true);
                            dialog1.show();
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                public void run() {
                                    dialog1.dismiss();
                                }}, 7000);

                            Uri resultUri1 = getImageUri(getContext(),bitmap);
                            uploadImage(resultUri1,"Sample");
                        }
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }

                        break;

                    default:
                        // Handle default case
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Converting...",
                        true);
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }}, 7000);
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //iv_image.setImageBitmap(bitmap);

                uploadImage(resultUri,"Sample");
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Random rand = new Random();
        int randNo = rand.nextInt(1000);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + randNo, null);
        return Uri.parse(path);
    }
    private void savenotes_dialog(String resultText, String photoStringLink) {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getContext());
        View mView=getLayoutInflater().inflate(R.layout.join_meeting_dialog,null);
        Button btn_join;
        TextInputEditText title;

        title=mView.findViewById(R.id.title_edittext);
        TextInputLayout til = mView.findViewById(R.id.text_input_layout);
        til.setError("Enter a title");
        dropdown = mView.findViewById(R.id.category);

        btn_join=mView.findViewById(R.id.join_now_btn);
        alertDialogBuilder.setView(mView);
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,category_items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        btn_join.setOnClickListener(v -> {
            if(title.getText().length()>0)
            {
                alertDialog.hide();
                createPdf(title.getText().toString());
                AddNotes(title.getText().toString(),photoStringLink);
            }
            else
            {
                Toast.makeText(getContext(), "Enter title", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void AddNotes(String title, String imagelink)
     {   String key=dataFire.getKey();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        PdfModel pdfModel = new PdfModel(key,title,selectedCategory,date,imagelink);
        dataFire.getDocumentsRef().child("PDF").child(dataFire.getUserID()).child(key).setValue(pdfModel).
                addOnCompleteListener(task -> {

                });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory=category_items[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }
    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}