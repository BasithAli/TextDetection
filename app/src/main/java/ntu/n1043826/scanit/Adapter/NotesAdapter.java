package ntu.n1043826.scanit.Adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import ntu.n1043826.scanit.Helper.DataFire;
import ntu.n1043826.scanit.UI.Activity.DocumentDetailsActivity;
import ntu.n1043826.scanit.Model.NotesModel;
import ntu.n1043826.scanit.R;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyHolder>
{

    List<NotesModel> noteslist;
    private Context context;
    private DatabaseReference mDatabase;

    public  NotesAdapter(List<NotesModel> noteslist, Context context)
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
        NotesModel data=noteslist.get(position);
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
                    NotesModel notesModel =noteslist.get(getAdapterPosition());
                    Intent i=new Intent(context, DocumentDetailsActivity.class);
                    i.putExtra("id", notesModel.id);
                    i.putExtra("title", notesModel.title);
                    i.putExtra("desc", notesModel.desc);
                    i.putExtra("date", notesModel.date);
                    i.putExtra("category", notesModel.category);
                    i.putExtra("imageurl", notesModel.imgurl);
                    context.startActivity(i);

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotesModel notesModel =noteslist.get(getAdapterPosition());
                    deleteNote(notesModel.id);


                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotesModel notesModel =noteslist.get(getAdapterPosition());
                    sharetext(notesModel.desc);

                }
            });

        }


    }

    private void sharetext(String desc) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Text Recognition");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, desc);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));


    }

    public void removeItem(int position) {
        /*noteslist.remove(position);*/
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()

        NotesModel notesModel =noteslist.get(position);
        deleteNote(notesModel.id);
        notifyItemRemoved(position);
    }
    private void deleteNote(String id) {
        DataFire dataFire;
        dataFire=new DataFire();
        dataFire.getDocumentsRef().child(dataFire.getUserID()).child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context,"Docs Deleted",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    Log.d("Dddddd",id);
                });
    }

}
