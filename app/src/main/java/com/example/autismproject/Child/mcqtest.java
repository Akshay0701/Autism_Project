package com.example.autismproject.Child;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autismproject.Models.Child;
import com.example.autismproject.Models.Task;
import com.example.autismproject.Parent.ChildProfile;
import com.example.autismproject.Parent.ParentHome;
import com.example.autismproject.Parent.ParentRegister;
import com.example.autismproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class mcqtest extends AppCompatActivity {


    String doBetter = "You can do better!" + System.getProperty("line.separator") + System.getProperty("line.separator") + "Try again?";
    String poor = "Brush up your knowledge, maybe?";
    String congrats = "Well done!" + System.getProperty("line.separator") + "You are awesome!";
    // Question 1
    RadioButton question1_choice1;
    RadioButton question1_choice2;
    RadioButton question1_choice3;
    RadioButton question1_choice4;
    // Question 2
    RadioButton question2_choice1;
    RadioButton question2_choice2;
    RadioButton question2_choice3;
    RadioButton question2_choice4;
    // Question 3
    CheckBox question3_choice1;
    CheckBox question3_choice2;
    CheckBox question3_choice3;
    CheckBox question3_choice4;
    // Question 4
    EditText question4_answer;
    // Question 5
    CheckBox question5_choice1;
    CheckBox question5_choice2;
    CheckBox question5_choice3;
    CheckBox question5_choice4;
    // Question 6
    EditText question6_answer;
    // Question 7
    RadioButton question7_choice1;
    RadioButton question7_choice2;
    RadioButton question7_choice3;
    RadioButton question7_choice4;
    // Question 8
    EditText question8_answer;
    // Question 9
    RadioButton question9_choice1;
    RadioButton question9_choice2;
    RadioButton question9_choice3;
    RadioButton question9_choice4;
    // Question 10
    RadioButton question10_choice1;
    RadioButton question10_choice2;
    RadioButton question10_choice3;
    RadioButton question10_choice4;
    int final_score = 0;
    private Boolean isFabOpen = false;
    Button submit;

    // child id for retrieving all tasks
    String cID;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String mUid,mEmail;
    private FirebaseAuth mAuth;
    Child childProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcqtest);
        submit = findViewById(R.id.submit);

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Childs");

        question1_choice1 = findViewById(R.id.question1_choice1);
        question1_choice2 = findViewById(R.id.question1_choice2);
        question1_choice3 = findViewById(R.id.question1_choice3);
        question1_choice4 = findViewById(R.id.question1_choice4);
        question2_choice1 = findViewById(R.id.question2_choice1);
        question2_choice2 = findViewById(R.id.question2_choice2);
        question2_choice3 = findViewById(R.id.question2_choice3);
        question2_choice4 = findViewById(R.id.question2_choice4);
        question3_choice1 = findViewById(R.id.question3_choice1);
        question3_choice2 = findViewById(R.id.question3_choice2);
        question3_choice3 = findViewById(R.id.question3_choice3);
        question3_choice4 = findViewById(R.id.question3_choice4);
        question4_answer = findViewById(R.id.question4_answer);
        question5_choice1 = findViewById(R.id.question5_choice1);
        question5_choice2 = findViewById(R.id.question5_choice2);
        question5_choice3 = findViewById(R.id.question5_choice3);
        question5_choice4 = findViewById(R.id.question5_choice4);
        question6_answer = findViewById(R.id.question6_answer);
        question7_choice1 = findViewById(R.id.question7_choice1);
        question7_choice2 = findViewById(R.id.question7_choice2);
        question7_choice3 = findViewById(R.id.question7_choice3);
        question7_choice4 = findViewById(R.id.question7_choice4);
        question8_answer = findViewById(R.id.question8_answer);
        question9_choice1 = findViewById(R.id.question9_choice1);
        question9_choice2 = findViewById(R.id.question9_choice2);
        question9_choice3 = findViewById(R.id.question9_choice3);
        question9_choice4 = findViewById(R.id.question9_choice4);
        question10_choice1 = findViewById(R.id.question10_choice1);
        question10_choice2 = findViewById(R.id.question10_choice2);
        question10_choice3 = findViewById(R.id.question10_choice3);
        question10_choice4 = findViewById(R.id.question10_choice4);
        ScrollView view = findViewById(R.id.scroll_view);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener((v, event) -> {
            v.requestFocusFromTouch();
            return false;
        });

        submit.setOnClickListener(view1 -> {
            if (question1_choice4.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 2 - Correct Answer is #1
            //------------------------------------------------------------------------------------------
            if (question2_choice1.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 3 - Correct Answer is #1 and #2
            //------------------------------------------------------------------------------------------
            if (question3_choice1.isChecked() && question3_choice2.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 4 - Correct Answer is Deoxyribo Nucleic Acid
            //------------------------------------------------------------------------------------------
            if (question4_answer.getText().toString().toLowerCase().equals("deoxyribo nucleic acid"))
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 5 - Correct Answer is #1, #2 and #3
            //------------------------------------------------------------------------------------------
            if (question5_choice1.isChecked() && question5_choice2.isChecked() && question5_choice3.isChecked() && !question5_choice4.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 6 - Correct Answer is Robert Brown
            //------------------------------------------------------------------------------------------
            if (question6_answer.getText().toString().toLowerCase().equals("robert brown"))
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 7 - Correct Answer is #2
            //------------------------------------------------------------------------------------------
            if (question7_choice2.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 8 - Correct Answer is Eyes/Eye
            //------------------------------------------------------------------------------------------
            if (question8_answer.getText().toString().toLowerCase().equals("eye") || question8_answer.getText().toString().toLowerCase().equals("eyes"))
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 9 - Correct Answer is #1
            //------------------------------------------------------------------------------------------
            if (question9_choice1.isChecked())
                final_score++;
            //------------------------------------------------------------------------------------------
            // Question 10 - Correct Answer is #2
            //------------------------------------------------------------------------------------------
            if (question10_choice2.isChecked())
                final_score++;
            //Gets the instance of the LayoutInflater, uses the context of this activity
            LayoutInflater inflater = (LayoutInflater) mcqtest.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
            View layout = inflater.inflate(R.layout.popup, null);

            if (final_score <= 3) {
                ((TextView) layout.findViewById(R.id.popup)).setText(poor);
            } else if (final_score >= 4 && final_score <= 9) {
                ((TextView) layout.findViewById(R.id.popup)).setText(doBetter);
            } else {
                ((TextView) layout.findViewById(R.id.popup)).setText(congrats);
            }
            //Get the devices screen density to calculate correct pixel sizes
            float density = mcqtest.this.getResources().getDisplayMetrics().density;
            // create a focusable PopupWindow with the given layout and correct size
            final PopupWindow pw = new PopupWindow(layout, (int) density * 350, (int) density * 400, true);
            pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pw.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        pw.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            pw.setOutsideTouchable(true);
            // display the pop-up in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            Context context = getApplicationContext();
            CharSequence text = "Your Score: " + final_score;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            String precentAutism = "";
            if (final_score == 10) {
                precentAutism = "0%";
            } else if (final_score <= 9 && final_score >= 5) {
                precentAutism = "40%";
            } else if (final_score <= 4 && final_score >= 2) {
                precentAutism = "80%";
            } else {
                precentAutism = "90%";
            }
            completeTaskAndExit(precentAutism + "");
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
            startActivity(new Intent(mcqtest.this, ParentRegister.class));
            finish();
        }

        // get selected child id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        cID=prefs.getString("selectedChildID","");

        if(cID.isEmpty()) {
            startActivity(new Intent(mcqtest.this, ParentHome.class));
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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mcqtest.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void completeTaskAndExit(String scores) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Child child = ds.getValue(Child.class);
                    if(child != null && child.getcID().equals(cID)){
                        Map<String, Object> hashMap = new HashMap<>();
                        //put info
                        hashMap.put("name", child.getName());
                        hashMap.put("age", child.getAge());
                        hashMap.put("scores", scores);
                        hashMap.put("cID", cID);
                        hashMap.put("imgUrl", child.getImgUrl());
                        hashMap.put("pID",child.getpID());


                        databaseReference.child(cID).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                            Toast.makeText(mcqtest.this, "Task Uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(mcqtest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mcqtest.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}