package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    boolean female = false;

    private EditText etGender;
    private EditText etName;
    private EditText etBreed;
    private EditText etBirthday;
    private EditText etDescription;

    private String email;
    private String password;
    private String profilepic;

    private ProgressBar pbEdit;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private Button btnEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.initComponents();
        this.initFirebase();
    }

    private void initComponents(){
        this.etGender = findViewById(R.id.et_gender_edit);
        this.etName = findViewById(R.id.et_name_edit);
        this.etBreed = findViewById(R.id.et_breed_edit);
        this.etBirthday = findViewById(R.id.et_birthday_edit);
        this.etDescription = findViewById(R.id.et_description_edit);
        this.btnEdit = findViewById(R.id.btn_edit);
        this.pbEdit = findViewById(R.id.pb_edit);

        this.btnEdit.setOnClickListener(view->{
            String gender = etGender.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            try {
                if(!checkEmpty(email, password, gender, name, breed, birthday)){
                    User user = new User(email, password, gender, name, breed, birthday, description, profilepic);
                    storeUser(user);
                    Intent i = new Intent(EditActivity.this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


        etBirthday.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    EditActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        etBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            EditActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year, month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });

        mDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            Log.d("Main", "onDateSet: mm/dd/yyy: " + month + "/" + dayOfMonth + "/" + year);

            String date = month + "/" + dayOfMonth + "/" + year;
            etBirthday.setText(date);
        };

        etGender.setOnClickListener(view -> {
            if (female){
                etGender.setText("Male");
                female = false;
            }
            else{
                etGender.setText("Female");
                female = true;
            }
        });
        etGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if (female) {
                        etGender.setText("Male");
                        female = false;
                    } else {
                        etGender.setText("Female");
                        female = true;
                    }
                }
            }
        });
    }
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");

        database.getReference().child(Collections.users.name()).child(this.mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String birthday = snapshot.child("birthday").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String breed = snapshot.child("breed").getValue().toString();
                String description = snapshot.child("description").getValue().toString();


                etName.setText(name);
                etBirthday.setText(birthday);
                etGender.setText(gender);
                etBreed.setText(breed);
                etDescription.setText(description);

                email = snapshot.child("email").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                profilepic = snapshot.child("profilepic").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storeUser(User user){
        this.pbEdit.setVisibility(View.VISIBLE);

        database.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    EditActivity.this.pbEdit.setVisibility(View.GONE);
                    Toast.makeText(EditActivity.this, "User Registration Registered", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    EditActivity.this.pbEdit.setVisibility(View.GONE);
                    Toast.makeText(EditActivity.this, "User Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkEmpty(String email, String password, String gender, String name, String breed, String birthday) throws ParseException {
        boolean hasEmpty = false;

        if(!birthday.isEmpty()){
            Date birthdayConv = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(birthday);
            Date today = new Date();
            if(birthdayConv.after(today)){
                this.etBirthday.setError("Birthday is invalid.");
                this.etBirthday.requestFocus();
                hasEmpty = true;
            }
        }
        if(breed.isEmpty()){
            this.etBreed.setError("Field is required");
            this.etBreed.requestFocus();
            hasEmpty = true;
        }
        if(name.isEmpty()){
            this.etName.setError("Field is required");
            this.etName.requestFocus();
            hasEmpty = true;
        }
        if(gender.isEmpty()){
            this.etGender.setError("Field is required");
            this.etGender.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }
}