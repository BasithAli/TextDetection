package ntu.n1043826.scanit.UI.Fragment.OCR;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ntu.n1043826.scanit.Adapter.NotesAdapter;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Helper.ImageOrientation;
import ntu.n1043826.scanit.Helper.RecyclerItemTouchHelper;
import ntu.n1043826.scanit.Model.NotesModel;
import ntu.n1043826.scanit.R;

import static android.app.Activity.RESULT_OK;

public class OCRFragment extends Fragment  implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Image";
    private FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage image;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    ImageView nodata;
    List<NotesModel> list = new ArrayList<>();
    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    NotesAdapter notesAdapter;
    private DataFire dataFire;

    private Bitmap bitmapNew;
    Spinner dropdown;
    String selectedCategory;
    String[] category_items = new String[]{"Others", "Books", "Medicals", "Shopping", "Vehicle", "Notes", "Food", "Banking", "Recipts", "Manuals", "Travel", "Id", "Legal"};

    String TEMP_PHOTO_FILE="/holdphoto123.jpeg";
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
        View view1 = inflater.inflate(R.layout.fragment_o_c_r, container, false);

        dataFire = new DataFire();

        initView(view1);

        FirebaseApp.initializeApp(getContext());




        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, getContext());
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        notesAdapter = new NotesAdapter(list, getContext());
        recyclerView.setAdapter(notesAdapter);

        dataFire.getDocumentsRef().child(dataFire.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {

                    fab.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);

                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    NotesModel notesModel = dataSnapshot1.getValue(NotesModel.class);
                    list.add(notesModel);

                }
                notesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(view -> chooseImage());

        return view1;
    }
    //NEW METHOD
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

    private void initView(View view) {
      fab= view.findViewById(R.id.fab);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        recyclerView = view.findViewById(R.id.recyclerview);
        nodata = view.findViewById(R.id.empty);
        layout = view.findViewById(R.id.layout);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PICK_FROM_CAMERA:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    final ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Detecting...",
                            true);
                    dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }}, 7000);

                    Uri resultUri = getImageUri(getContext(),imageBitmap);
                    recognizeText(imageBitmap,resultUri);
                        break;
                    case PICK_FROM_GALLERY:

                        File tempFile = getTempFile();
                        String filePath = Environment.getExternalStorageDirectory() + "/" + TEMP_PHOTO_FILE;
                        Log.d(TAG, "path " + filePath);
                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        if (selectedImage != null) {
                            final ProgressDialog dialog1 = ProgressDialog.show(getContext(), "", "Detecting...",
                                    true);
                            dialog1.show();
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                public void run() {
                                    dialog1.dismiss();
                                }}, 7000);

                            Uri resultUri1 = getImageUri(getContext(),selectedImage);
                            recognizeText(selectedImage,resultUri1);
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
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Random rand = new Random();
        int randNo = rand.nextInt(1000);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + randNo, null);
        return Uri.parse(path);
    }
    private void recognizeText(Bitmap bitmap,Uri uri) {

        try {
            image  = FirebaseVisionImage.fromBitmap(bitmap);
            textRecognizer = FirebaseVision
                    .getInstance()
                    .getOnDeviceTextRecognizer();
        } catch (Exception e) {
            e.printStackTrace();
        }


        textRecognizer.processImage(image)
                .addOnSuccessListener(firebaseVisionText -> {
                    String resultText = firebaseVisionText.getText();

                    if (resultText.isEmpty()){
                        Toast.makeText(getContext(), "NO TEXT FOUND", Toast.LENGTH_SHORT).show();
                    }else {

                        uploadImage(uri,resultText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void uploadImage(Uri uri,String resultText)
    {

        DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
        String getDateYears = dfYears.format(Calendar.getInstance().getTime());
        setImage(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] data1 = byteArrayOutputStream.toByteArray();
        final StorageReference filePath = dataFire.getStorageref()
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
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotesAdapter.MyHolder) {
            // remove the item from recycler view
            notesAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

    private void savenotes_dialog(String resultText, String photoStringLink) {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getContext());
        View mView=getLayoutInflater().inflate(R.layout.join_meeting_dialog,null);
        Button btn_join;
        TextInputEditText title;

        title=mView.findViewById(R.id.title_edittext);
        TextInputLayout til = mView.findViewById(R.id.text_input_layout);
        til.setError("You need to enter a title");
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
                AddNotes(title.getText().toString(),resultText,photoStringLink);
            }
            else
            {
                Toast.makeText(getContext(), "Enter title", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory=category_items[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    private void AddNotes(String title, String resulttext, String imagelink)
    {   String key=dataFire.getKey();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        NotesModel notesModel = new NotesModel(key,title, resulttext,selectedCategory,date,imagelink);
        dataFire.getDocumentsRef().child(dataFire.getUserID()).child(key).setValue(notesModel).
                addOnCompleteListener(task -> {
                    Toast.makeText(getContext(), "Notes Added", Toast.LENGTH_SHORT).show();

                });

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