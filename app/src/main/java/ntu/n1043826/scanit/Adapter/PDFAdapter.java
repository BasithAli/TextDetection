package ntu.n1043826.scanit.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Model.PdfModel;
import ntu.n1043826.scanit.R;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.MyHolder>
{

    List<PdfModel> noteslist;
    private Context context;
    private DatabaseReference mDatabase;

    public PDFAdapter(List<PdfModel> noteslist, Context context)
    {
        this.context=context;
        this.noteslist=noteslist;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);

        MyHolder myHolder=new MyHolder(view);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        PdfModel data=noteslist.get(position);
        myHolder.title.setText(data.getTitle());
        myHolder.desc.setText(data.category);
        myHolder.date.setText(data.getDate());
        Picasso.get()
                .load(data.getImgurl())
                .placeholder(R.drawable.img1)
                .error(R.drawable.img1)
                .into(myHolder.thumbnail);
        Random rand = new Random();
        int i = rand.nextInt(5) + 1;
        switch (i) {
            case 1:
                myHolder.desc.setTextColor(ContextCompat.getColor(context, R.color.red));
                break;
            case 2:
                myHolder.desc.setTextColor(ContextCompat.getColor(context, R.color.pink));
                break;
            case 3:
                myHolder.desc.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case 4:
                myHolder.desc.setTextColor(ContextCompat.getColor(context, R.color.blue));
                break;
            case 5:
                myHolder.desc.setTextColor(ContextCompat.getColor(context, R.color.orange));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return noteslist.size();
    }

    public class  MyHolder extends RecyclerView.ViewHolder  {
        TextView title,desc,date;
        public ImageView delete,share;
        CircleImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            desc=itemView.findViewById(R.id.desc);
            date=itemView.findViewById(R.id.date);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            delete = itemView.findViewById(R.id.delete);
            share = itemView.findViewById(R.id.share);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PdfModel data=noteslist.get(getAdapterPosition());

                    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/"+context.getString(R.string.app_name)+"/";
                    String targetPdf = directory_path + data.title+".pdf";
                    File filePath = new File(targetPdf);
                    Log.d("FIlePath",targetPdf);
                    if (filePath.exists()) //Checking if the file exists or not
                    {

                        Uri uri;
                        Log.i("SDK_VERSION",Build.VERSION.SDK_INT+"");
                        if (Build.VERSION.SDK_INT < 24) {
                            uri = Uri.fromFile(filePath);
                        } else {
                            uri = Uri.fromFile(filePath);
//                            uri = Uri.parse(filePath.getPath()); // My work-around for SDKs up to 29.
                        }
                        Intent pdfViewIntent = new Intent(Intent.ACTION_VIEW);
                        pdfViewIntent.setDataAndType(uri, "application/pdf");
                        pdfViewIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Intent intent = Intent.createChooser(pdfViewIntent, "Open File");
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }

                    }
                    else {

                        Toast.makeText(context, "The file not exists! ", Toast.LENGTH_SHORT).show();

                    }


                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PdfModel pdfModel =noteslist.get(getAdapterPosition());
                    deleteNote(pdfModel.id);


                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PdfModel pdfModel =noteslist.get(getAdapterPosition());
                    sharetext(pdfModel.title);

                }
            });

        }


    }

    private void sharetext(String desc) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Text Recognition");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, desc);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));


    }

    public void removeItem(int position) {
        /*noteslist.remove(position);*/
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()

        PdfModel pdfModel =noteslist.get(position);
        deleteNote(pdfModel.id);
        notifyItemRemoved(position);
    }
    private void deleteNote(String id) {
        DataFire dataFire;
        dataFire=new DataFire();
        dataFire.getDocumentsPdfRef().child(dataFire.getUserID()).child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,"Docs Deleted",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    Log.d("Dddddd",id);
                });
    }
}
