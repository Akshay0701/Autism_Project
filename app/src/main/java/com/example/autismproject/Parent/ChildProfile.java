package com.example.autismproject.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autismproject.Adapters.AdapterChilds;
import com.example.autismproject.Models.Child;
import com.example.autismproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// this activity helps parent get more details of child and provide facilities to add new task, games, videos for thier child.
public class ChildProfile extends AppCompatActivity {

    TextView childProfileDetails;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Child childProfile;

    String mPid,mEmail;
    private FirebaseAuth mAuth;

    Button childTasks, childGames, childVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Childs");

        childProfileDetails = findViewById(R.id.child_profile_name_age_scores);

        childTasks = findViewById(R.id.child_profile_task);
        childTasks.setOnClickListener(view -> {
            startActivity(new Intent(ChildProfile.this, ParentTasksActivity.class));
        });

        childGames = findViewById(R.id.child_profile_games);
        childVideos = findViewById(R.id.child_profile_videos);
        childVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChildProfile.this, ParentVideo.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            mPid = mAuth.getUid();
            mEmail = mAuth.getCurrentUser().getEmail();
        }else{
            startActivity(new Intent(ChildProfile.this, ParentRegister.class));
            finish();
        }

        // get selected child id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String cID=prefs.getString("selectedChildID","");

        if(cID.isEmpty()) {
            startActivity(new Intent(ChildProfile.this, ParentHome.class));
            finish();
        }
        else {
            Toast.makeText(this, ""+cID, Toast.LENGTH_SHORT).show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Child child = ds.getValue(Child.class);
                        if(child != null && child.getcID().equals(cID)) {
                            childProfile = child;
                            //adapter
                            childProfileDetails.setText("Name: " + childProfile.getName() + " Age: " + childProfile.getAge() + " Scores: " + childProfile.getScores());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChildProfile.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}