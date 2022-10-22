package com.example.autismproject.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autismproject.Child.ChildHome;
import com.example.autismproject.Child.ChildLogin;
import com.example.autismproject.Models.Child;
import com.example.autismproject.Parent.ChildProfile;
import com.example.autismproject.Parent.ParentRegister;
import com.example.autismproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseError;

import java.util.List;

// this class is going to be in use to display child's to respective parents
public class AdapterChilds extends RecyclerView.Adapter<AdapterChilds.MyHolder>  {

    Context context;
    List<Child> childList;
    // if its parent panel then onclick of child open child profile
    // else child login and goto child home page
    Boolean isParentPanel;

    public AdapterChilds(Context context, List<Child> childList, Boolean isParentPanel) {
        this.context = context;
        this.childList = childList;
        this.isParentPanel = isParentPanel;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_child,parent,false);
        return new AdapterChilds.MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String cId= childList.get(position).getcID();
        final String pId= childList.get(position).getpID();
        final String name= childList.get(position).getName();
        final String age= childList.get(position).getAge();
        final String scores= childList.get(position).getScores();

        //setdata
        holder.childName.setText(name);
        holder.childAge.setText("Age : "+ age);
        holder.childScores.setText("Scores : " + scores);

        holder.itemView.setOnClickListener(view -> {
            SharedPreferences.Editor editor;
            editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("selectedChildID", cId);
            editor.apply();


            if (isParentPanel) {
                context.startActivity(new Intent(context, ChildProfile.class));
            } else {
                Toast.makeText(context, "Logged In: " + name, Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, ChildHome.class));
            }

        });

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        TextView childName, childAge, childScores;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            childName=itemView.findViewById(R.id.row_child_name);
            childAge=itemView.findViewById(R.id.row_child_age);
            childScores=itemView.findViewById(R.id.row_child_score);
        }
    }

}