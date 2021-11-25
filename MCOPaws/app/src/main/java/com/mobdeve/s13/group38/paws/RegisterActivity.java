package com.mobdeve.s13.group38.paws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    boolean female = false;

    private TextView tvLogin;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etGender;
    private EditText etName;
    private EditText etBreed;
    private EditText etBirthday;
    private EditText etDescription;

    private Button btnRegister;
    private ProgressBar pbRegister;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    GoogleSignInOptions gso;
//    GoogleSignInClient

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.initFirebase();
        this.initComponents();
    }



    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance("https://mobdeve-paws-default-rtdb.asia-southeast1.firebasedatabase.app/");
;
    }

    private void initComponents(){

        this.tvLogin = findViewById(R.id.tv_login_register);

        this.etEmail = findViewById(R.id.et_email_register);
        this.etPassword = findViewById(R.id.et_password_register);
        this.etGender = findViewById(R.id.et_gender_register);
        this.etName = findViewById(R.id.et_name_register);
        this.etBreed = findViewById(R.id.et_breed_register);
        this.etBirthday = findViewById(R.id.et_birthday_register);
        this.etDescription = findViewById(R.id.et_description_register);

        this.btnRegister = findViewById(R.id.btn_register);
        this.pbRegister = findViewById(R.id.pb_register);

        this.etGender.setText("Male");

        this.tvLogin.setOnClickListener(view->{
            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        this.btnRegister.setOnClickListener(view->{
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            try {
                if(!checkEmpty(email, password, gender, name, breed, birthday)){
                    User user = new User(email, password, gender, name, breed, birthday, description, "none");
                    storeUser(user);
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
                    RegisterActivity.this,
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
                            RegisterActivity.this,
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

    private void storeUser(User user){
        this.pbRegister.setVisibility(View.VISIBLE);

        this.mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    database.getReference(Collections.users.name())
                            .child(mAuth.getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        RegisterActivity.this.pbRegister.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "User Registration Registered", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        RegisterActivity.this.pbRegister.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "User Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        if(password.isEmpty()){
            this.etPassword.setError("Field is required");
            this.etPassword.requestFocus();
            hasEmpty = true;
        }
        if(password.length() < 6){
            this.etPassword.setError("Minimum of 6 characters");
            this.etPassword.requestFocus();
            hasEmpty = true;
        }
        if(email.isEmpty()){
            this.etEmail.setError("Field is required");
            this.etEmail.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }
}