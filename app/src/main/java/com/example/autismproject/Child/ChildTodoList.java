package com.example.autismproject.Child;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.autismproject.Adapters.AdapterTasks;
import com.example.autismproject.Models.Task;
import com.example.autismproject.Parent.CreateTask;
import com.example.autismproject.Parent.ParentHome;
import com.example.autismproject.Parent.ParentRegister;
import com.example.autismproject.Parent.ParentTasksActivity;
import com.example.autismproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildTodoList extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_todo_list);

        // init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Tasks");

        // load recycleBook
        taskRecyclerView =(RecyclerView)findViewById(R.id.child_task_childrecyclerView);
        taskRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(ChildTodoList.this);
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
                adapterTasks = new AdapterTasks(ChildTodoList.this, taskList, false);
                taskRecyclerView.setLayoutManager(new LinearLayoutManager(ChildTodoList.this, LinearLayoutManager.VERTICAL, false));

                //set adapter to recycle
                taskRecyclerView.setAdapter(adapterTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChildTodoList.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(ChildTodoList.this, ParentRegister.class));
            finish();
        }

        // get selected child id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        cID = prefs.getString("selectedChildID","");

        if(cID.isEmpty()) {
            startActivity(new Intent(ChildTodoList.this, ParentHome.class));
            finish();
        } else {
            loadTasks();
        }
    }
}