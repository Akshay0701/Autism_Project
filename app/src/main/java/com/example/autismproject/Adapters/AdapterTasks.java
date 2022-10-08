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

import com.example.autismproject.Models.Child;
import com.example.autismproject.Models.Task;
import com.example.autismproject.Parent.ChildProfile;
import com.example.autismproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// this class is going to be in use to display child's to respective parents
public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.MyHolder>  {

    Context context;
    List<Task> taskList;
    // if true then give access to parent as deleting task or editing it.
    // else give child access like completing the task
    Boolean isParent;

    public AdapterTasks(Context context, List<Task> taskList, Boolean isParent) {
        this.context = context;
        this.taskList = taskList;
        this.isParent = isParent;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_child_task, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String cId= taskList.get(position).getcID();
        final String pId= taskList.get(position).getpID();
        final String tId= taskList.get(position).gettID();
        final String imgUrl= taskList.get(position).getImgUrl();
        final String isComplete= taskList.get(position).getIsComplete();
        //description of task
        final String text= taskList.get(position).getText();
        final String timestamp= taskList.get(position).getTimestamp();

        // setting data
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        try {
            String _24HourTime = hours + ":" + minutes;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            holder.time.setText(_12HourSDF.format(_24HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //set image
        try{
            Picasso.get().load(imgUrl).placeholder(R.drawable.childlogo).into(holder.image);
        }catch (Exception e){
            Picasso.get().load(R.drawable.childlogo).into(holder.image);
        }

        holder.itemView.setOnClickListener(view -> {

            if (isParent) {
                // show dialog of deleting task
                showDeleteTaskDialog(taskList.get(position));
            } else {
                // start Task playing for child
            }

        });

    }

    private void showDeleteTaskDialog(Task task) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteTask(task);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete task?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteTask(Task task) {
        final ProgressDialog pd=new ProgressDialog(context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        pd.setMessage("Deleting..");

        //image
        try {
            StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(task.getImgUrl());
            picRef.delete().addOnSuccessListener(aVoid -> {
                Query fquery= FirebaseDatabase.getInstance().getReference("Tasks").orderByChild("tID").equalTo(task.gettID());
                fquery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Deleted Message", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } catch (IllegalArgumentException e){
            Query fquery= FirebaseDatabase.getInstance().getReference("Tasks").orderByChild("tID").equalTo(task.gettID());
            fquery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                    Toast.makeText(context, "Deleted Task", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        ImageView image, completionImg;
        TextView time;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.row_child_task_img);
            completionImg=itemView.findViewById(R.id.row_child_task_img);
            time=itemView.findViewById(R.id.row_child_task_time);
        }
    }

}