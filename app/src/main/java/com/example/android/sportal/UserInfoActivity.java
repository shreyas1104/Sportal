package com.example.android.sportal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private ImageView user_photo;
    private EditText student_id_et;
    private EditText first_name_et;
    private EditText last_name_et;
    private Button nextbtn;
    private EditText dob_et;
    private ImageView calender_iv;
    private Spinner spinner1;
    private Spinner spinner2;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressBar progressBar;

    Uri uriProfileImage;
    String profile_image_url;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        user_photo = findViewById(R.id.user_photo);
        student_id_et = findViewById(R.id.student_id_et);
        first_name_et = findViewById(R.id.first_name_et);
        last_name_et = findViewById(R.id.last_name_et);
        nextbtn = findViewById(R.id.nextbtn);
        dob_et = findViewById(R.id.dob_et);
        calender_iv = findViewById(R.id.calender_iv);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        calender_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UserInfoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog, mDateSetListener,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day + "/" + month + "/" + year;
                dob_et.setText(date);
            }
        };

        final ArrayAdapter<CharSequence> btech = ArrayAdapter.createFromResource(this,R.array.btech,android.R.layout.simple_spinner_item);
        btech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> barch = ArrayAdapter.createFromResource(this,R.array.barch,android.R.layout.simple_spinner_item);
        btech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> mtech = ArrayAdapter.createFromResource(this,R.array.mtech,android.R.layout.simple_spinner_item);
        btech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> msc = ArrayAdapter.createFromResource(this,R.array.msc,android.R.layout.simple_spinner_item);
        btech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> phd = ArrayAdapter.createFromResource(this,R.array.phd,android.R.layout.simple_spinner_item);
        btech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    spinner2.setAdapter(btech);
                }
                else if(i==1){
                    spinner2.setAdapter(barch);
                }
                else if(i==2){
                    spinner2.setAdapter(mtech);
                }
                else if(i==3){
                    spinner2.setAdapter(msc);
                }
                else if(i==4){
                    spinner2.setAdapter(phd);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            finish();
            Intent LoginIntent = new Intent(UserInfoActivity.this, StudentLogin.class);
            startActivity(LoginIntent);
        }

        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserInfoActivity.this, "Select new photo", Toast.LENGTH_SHORT).show();
                showImageChooser();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                user_photo.setImageBitmap(bitmap);
                applyImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile image"), CHOOSE_IMAGE);

    }

    private void applyImageToFirebaseStorage(){
        final StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("profilepics/"+firebaseAuth.getCurrentUser().getUid()+".jpg");
        if(uriProfileImage != null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);

                    profile_image_url = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUserInformation(){
        Toast.makeText(this, "saved user info", Toast.LENGTH_SHORT).show();
        String first_name = first_name_et.getText().toString();
        String last_name = last_name_et.getText().toString();

        if(TextUtils.isEmpty(first_name)){
            first_name_et.setError("First name required");
            first_name_et.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(last_name)){
            last_name_et.setError("Last name required");
            last_name_et.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(student_id_et.getText().toString())){
            student_id_et.setError("Student ID required");
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null && profile_image_url!=null){
           UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(first_name+" "+last_name)
                    .setPhotoUri(Uri.parse(profile_image_url)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        finish();
                        saveToRealtimeDatabase();
                        Intent intent = new Intent(UserInfoActivity.this, MainActivityStudent.class);
                        startActivity(intent);
                    }
                }
            });
        }

    }
    private void saveToRealtimeDatabase(){
        String UID = firebaseAuth.getCurrentUser().getUid();
        String name = firebaseAuth.getCurrentUser().getDisplayName();
        String email = firebaseAuth.getCurrentUser().getEmail();
        String photo_url = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
        String student_id = student_id_et.getText().toString();
        String dob = dob_et.getText().toString();
        String degree = spinner1.getSelectedItem().toString();
        String branch = spinner2.getSelectedItem().toString();

        User user = new User(UID,name,dob,email,student_id,photo_url,degree,branch,false,false,"","","");

        databaseUsers.child(UID).setValue(user);



        Toast.makeText(this, "Added to database", Toast.LENGTH_SHORT).show();

    }
}
