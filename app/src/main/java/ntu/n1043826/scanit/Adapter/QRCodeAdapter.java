package ntu.n1043826.scanit.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.Model.QRModel;
import ntu.n1043826.scanit.R;

public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.MyHolder>
{

    List<QRModel> noteslist;
    private Context context;
    private DatabaseReference mDatabase;

    public QRCodeAdapter(List<QRModel> noteslist, Context context)
    {
        this.context=context;
        this.noteslist=noteslist;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);

        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        QRModel data=noteslist.get(position);
        myHolder.title.setText(data.getText());
        myHolder.desc.setText(data.getType());
        myHolder.date.setText(data.getDate());
        String type=data.getType();
        int image=R.drawable.ic_baseline_qr_code_24;
        switch (type)
        {
            case "text":
                image=R.drawable.ic_baseline_text_format_24;
                break;
            case "url":
                image=R.drawable.ic_baseline_web_24;
                break;
            case "phone":
                image=R.drawable.ic_baseline_phone_24;
                break;
            case "event":
                image=R.drawable.ic_baseline_event_24;
                break;
            case "sms":
                image=R.drawable.ic_baseline_message_24;
                break;
            case "mail":
                image=R.drawable.ic_baseline_email_24;
                break;
            case "other":
                image=R.drawable.ic_baseline_qr_code_24;
                break;

        }

        myHolder.thumbnail.setImageResource(image);
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

                    QRModel qrModel =noteslist.get(getAdapterPosition());
                    switch (qrModel.type)
                    {
                        case "text":
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setMessage(qrModel.getText());
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                            break;
                        case "url":
                            //open browser
                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(qrModel.getText()));
                            context.startActivity(intent);
                            break;
                        case "phone":
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel: "+qrModel.text));
                            context.startActivity(callIntent);
                            break;
                        case "event":
                            AlertDialog.Builder builder0=new AlertDialog.Builder(context);
                            builder0.setMessage(qrModel.getText());
                            builder0.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog0=builder0.create();
                            dialog0.show();
                            break;
                        case "sms":
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse("sms:"));
                            sendIntent.putExtra("sms_body", qrModel.text);
                            context.startActivity(sendIntent);
                            break;
                        case "mail":
                            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                            mailIntent.setData(Uri.parse("mailto:"+qrModel.text));
                            context.startActivity(mailIntent);
                            break;
                        case "other":
                            AlertDialog.Builder builder1=new AlertDialog.Builder(context);
                            builder1.setMessage(qrModel.getText());
                            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog1=builder1.create();
                            dialog1.show();
                            break;

                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QRModel qrModel =noteslist.get(getAdapterPosition());
                    deleteNote(qrModel.id);


                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QRModel qrModel =noteslist.get(getAdapterPosition());
                    sharetext(qrModel.text);

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

        QRModel qrModel =noteslist.get(position);
        deleteNote(qrModel.id);
        notifyItemRemoved(position);
    }
    private void deleteNote(String id) {
        DataFire dataFire;
        dataFire=new DataFire();
        dataFire.getQRDataRef().child(dataFire.getUserID()).child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context," Deleted",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    Log.d("Dddddd",id);
                });
    }
}
