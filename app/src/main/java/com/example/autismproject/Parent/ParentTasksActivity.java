package com.example.autismproject.Parent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.autismproject.Adapters.AdapterChilds;
import com.example.autismproject.Adapters.AdapterTasks;
import com.example.autismproject.Models.Child;
import com.example.autismproject.Models.Task;
import com.example.autismproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParentTasksActivity extends AppCompatActivity {

    Button addNewTask;

    RecyclerView taskRecyclerView;
    RecyclerView.LayoutManager  layoutManager;
    List<Task> taskList;
    AdapterTasks adapterTasks;

    // child id for retrieving all tasks
    String cID;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String mUid,mEmail;
    private FirebaseAuth mAuth;

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Tasks");

        addNewTask = findViewById(R.id.parent_tasks_addnewtask);

        addNewTask.setOnClickListener(view -> {
            // open create new task activity
            startActivity(new Intent(ParentTasksActivity.this, CreateTask.class));
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> finish());

        //load recycleBook
        taskRecyclerView =(RecyclerView)findViewById(R.id.parent_home_childrecyclerView);
        taskRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(ParentTasksActivity.this);
        taskRecyclerView.setLayoutManager(layoutManager);

        taskList = new ArrayList<>();

    }

    void loadTasks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Task task = ds.getValue(Task.class);
                    if(task != null && task.getcID().equals(cID))
                        taskList.add(task);
                }

                // sort the list with timestamp
                if (taskList.size() > 1) {
                    Collections.sort(taskList, (lhs, rhs) -> {
                        long lhstime = Long.parseLong(lhs.getTimestamp());
                        long rhstime = Long.parseLong(rhs.getTimestamp());
                        // return 1 if rhs should be before lhs
                        // return -1 if lhs should be before rhs
                        //  return 0 otherwise (meaning the order stays the same)
                        return Long.compare(lhstime, rhstime);
                    });
                }

                //adapter
                adapterTasks = new AdapterTasks(ParentTasksActivity.this, taskList, true);
                taskRecyclerView.setLayoutManager(new LinearLayoutManager(ParentTasksActivity.this, LinearLayoutManager.VERTICAL, false));

                //set adapter to recycle
                taskRecyclerView.setAdapter(adapterTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ParentTasksActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            mUid = mAuth.getUid();
            mEmail = mAuth.getCurrentUser().getEmail();
        }else{
            startActivity(new Intent(ParentTasksActivity.this, ParentRegister.class));
            finish();
        }

        // get selected child id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        cID=prefs.getString("selectedChildID","");

        if(cID.isEmpty()) {
            startActivity(new Intent(ParentTasksActivity.this, ParentHome.class));
            finish();
        } else {
            loadTasks();
        }
    }
}