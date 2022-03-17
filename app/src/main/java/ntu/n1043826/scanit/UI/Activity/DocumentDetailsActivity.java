package ntu.n1043826.scanit.UI.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import ntu.n1043826.scanit.BuildConfig;
import ntu.n1043826.scanit.R;

public class DocumentDetailsActivity extends AppCompatActivity {

    ImageView docimage;
    TextView titletext,desctext,categorytext,datetext;
    FloatingActionButton sharebtn;
    String id,title,desc,category,date,imageurl;
    CollapsingToolbarLayout collapsing_toolbar;
    ImageView readdoc,downloaddoc,copdoc;
    TextToSpeech tts;
    private int STORAGE_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_details);
        collapsing_toolbar=findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setTitleEnabled(false);
        collapsing_toolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
        docimage=findViewById(R.id.doc_img);
        readdoc=findViewById(R.id.readdoc);
        downloaddoc=findViewById(R.id.download);
        copdoc=findViewById(R.id.copy);
        titletext=findViewById(R.id.title_text);
        desctext=findViewById(R.id.desc);
        categorytext=findViewById(R.id.category);
        datetext=findViewById(R.id.date_text);
        sharebtn=findViewById(R.id.share_doc);

        final Intent i=getIntent();
         title=i.getStringExtra("title");
         desc=i.getStringExtra("desc");
          id=i.getStringExtra("id");
        category=i.getStringExtra("category");
        date=i.getStringExtra("date");
        imageurl=i.getStringExtra("imageurl");

        titletext.setText(title);
        desctext.setText(desc);
        categorytext.setText(category);
        datetext.setText(date);
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.img1)
                .error(R.drawable.img1)
                .into(docimage);
        if (ContextCompat.checkSelfPermission(DocumentDetailsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestStoragePermission();
        }
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                } else {

                }
            } else {

            }
        });

        readdoc.setOnClickListener(v -> readdesc(desctext.getText().toString()));
        sharebtn.setOnClickListener(v -> shareItem(imageurl));
        downloaddoc.setOnClickListener(v -> {
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Downloading...",
                    true);
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }}, 3000);

            createPdf(desc,title);
        });
        copdoc.setOnClickListener(v -> copytoclipboard(desc));
    }
    public void readdesc(String text)
    {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Detecting...",
                true);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }}, 5000);
    }
    private void createPdf(String sometext,String filename) {
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.translate(10, 10);

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(8 * getResources().getDisplayMetrics().density);
        textPaint.setColor(0xFF000000);

        int width = (int) textPaint.measureText(sometext)-100;
        StaticLayout staticLayout = new StaticLayout(sometext, textPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        staticLayout.draw(canvas);
        // finish the page
        document.finishPage(page);
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/"+getString(R.string.app_name)+"/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + filename+".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "PDF Downloaded in "+getString(R.string.app_name), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Something wrong: " + e, Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }
    //request permission
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //show alert dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because you want to download file.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DocumentDetailsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void copytoclipboard(String text)
    {
        Toast.makeText(this, "text copied in clipboard", Toast.LENGTH_SHORT).show();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text copied", text);
        clipboard.setPrimaryClip(clip);
    }

    public void shareItem(String url) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                String shareMessage= "\n********\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                i.putExtra(android.content.Intent.EXTRA_TEXT, desc+shareMessage);
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();

    }
}