package com.example.android.sportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class StudentLogin extends AppCompatActivity {

    private TextView register_tv;
    private Button login_btn;
    private EditText email_et;
    private EditText password_et;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            //start
            finish();
            Intent intent = new Intent(StudentLogin.this, MainActivityStudent.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);
        register_tv = findViewById(R.id.register_tv);
        login_btn = findViewById(R.id.login_btn);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentLogin.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void userLogin(){
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                    if(firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")){
                        Intent intent = new Intent(StudentLogin.this, MainActivityAdmin.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(StudentLogin.this, MainActivityStudent.class);
                        startActivity(intent);
                    }

                }
                else{
                    Toast.makeText(StudentLogin.this, "Could not login. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
