package com.example.autismproject.Child;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autismproject.Models.Task;
import com.example.autismproject.Parent.CreateTask;
import com.example.autismproject.Parent.ParentRegister;
import com.example.autismproject.Parent.ParentTasksActivity;
import com.example.autismproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChildTaskActivity extends AppCompatActivity {


    // child id for retrieving all tasks
    String cID, tID;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String mUid,mEmail;
    private FirebaseAuth mAuth;

    Task task;

    ImageView childImageView;
    TextView childTime, childTimer, childDescription;

//    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_task);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Tasks");

        // TODO poor implementation
//        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//
//                // if No error is found then only it will run
//                if(i!=TextToSpeech.ERROR){
//                    // To Choose language of speech
//                    textToSpeech.setLanguage(Locale.UK);
//                    textToSpeech.setSpeechRate((float)0.7);
//                }
//            }
//        });

        childDescription = findViewById(R.id.child_task_description);
        childTime = findViewById(R.id.child_task_time);
        childTimer = findViewById(R.id.child_task_timer);
        childImageView = findViewById(R.id.child_task_image);

        childDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textToSpeech.speak(childDescription.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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
            startActivity(new Intent(ChildTaskActivity.this, ParentRegister.class));
            finish();
        }

        // get selected child id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        cID=prefs.getString("selectedChildID","");
        tID=prefs.getString("selectedTaskId","");

        if(cID.isEmpty() || tID.isEmpty()) {
            startActivity(new Intent(ChildTaskActivity.this, ChildHome.class));
            finish();
        } else {
            loadTaskDetails();
        }
    }

    private void loadTaskDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    task = ds.getValue(Task.class);
                    if(task != null && task.gettID().equals(tID)){
                        // insert all values of task
                        setTimeText(task.getTimestamp());
                        childDescription.setText(task.getText());
//                        textToSpeech.speak(task.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

                        int time = Integer.parseInt(task.getTimer()) * 1000;
                        new CountDownTimer(time, 1000) { // adjust the milli seconds here

                            public void onTick(long millisUntilFinished) {
                                childTimer.setText(String.format("%d min, %d sec",
                                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                            }
                            public void onFinish() {
                                childTimer.setText("Times Up!");
                                showCompletetionDialog();
                            }
                        }.start();

                        //set image
                        try{
                            Picasso.get().load(task.getImgUrl()).placeholder(R.drawable.childlogo).into(childImageView);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.childlogo).into(childImageView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChildTaskActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompletetionDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        completeTaskAndExit();
                        dialog.dismiss();
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            builder.setMessage("Congratulation's!").setPositiveButton("Done", dialogClickListener).show();
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
        }

    }

    private void completeTaskAndExit() {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Task task = ds.getValue(Task.class);
                        if(task != null && task.gettID().equals(tID)){
                            Map<String, Object> hashMap = new HashMap<>();
                            //put info
                            hashMap.put("tID", tID);
                            hashMap.put("text", task.getText());
                            hashMap.put("timestamp",task.getTimestamp());
                            hashMap.put("timer", task.getTimer());
                            hashMap.put("isComplete", "1");
                            hashMap.put("pID", task.getpID());
                            hashMap.put("cID", task.getcID());
                            hashMap.put("imgUrl", task.getImgUrl());
                            databaseReference.child(tID).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                                Toast.makeText(ChildTaskActivity.this, "Task Uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(ChildTaskActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChildTaskActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    void setTimeText(String timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        try {
            String _24HourTime = hours + ":" + minutes;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            childTime.setText(_12HourSDF.format(_24HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}