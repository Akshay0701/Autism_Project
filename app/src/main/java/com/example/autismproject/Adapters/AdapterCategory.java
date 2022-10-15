package com.example.autismproject.Adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autismproject.Models.Category;
import com.example.autismproject.Models.Video;
import com.example.autismproject.R;
import com.example.autismproject.VideoPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// this class is going to be in use to display child's to respective parents
public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyHolder>  {

    Context context;
    List<Category> categoryList;
    // if true then give access to parent as deleting videos.
    // else give child access like opening the video
    Boolean isParent;

    public AdapterCategory(Context context, List<Category> categoryList, Boolean isParent) {
        this.context = context;
        this.categoryList = categoryList;
        this.isParent = isParent;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_clickboard_category,parent,false);
        return new AdapterCategory.MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String cId= categoryList.get(position).getcID();
        final String pId= categoryList.get(position).getpID();
        final String url= categoryList.get(position).getImgUrl();

        //setdata
        try{
            Picasso.get().load(url).placeholder(R.drawable.childlogo).into(holder.categoryImageView);
        }catch (Exception e){
            Picasso.get().load(R.drawable.childlogo).into(holder.categoryImageView);
        }

        holder.itemView.setOnClickListener(view -> {
            SharedPreferences.Editor editor;
            editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("selectedCategoryID", cId);
            editor.apply();
        });
    }

    private void showDeleteVideoDialog(Video video) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    deleteVideo(video);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete Video?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    // on long click delete the category with same pID
    private void deleteVideo(Video video) {
        final ProgressDialog pd=new ProgressDialog(context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        pd.setMessage("Deleting..");
        Query fquery = FirebaseDatabase.getInstance().getReference("Categories").orderByChild("cID").equalTo(video.getvID());
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted Video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        ImageView categoryImageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageView = itemView.findViewById(R.id.category_img);
        }
    }

}