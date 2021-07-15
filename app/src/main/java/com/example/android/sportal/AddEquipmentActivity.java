package com.example.android.sportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddEquipmentActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    ImageView equipment_photo;
    EditText name_et;
    EditText sport_et;
    EditText quantity_et;
    LinearLayout contents_ll;
    ImageView add_contents;
    Button addbtn;
    Button backbtn;

    int contents_count;

    Uri uriEquipmentImage;
    String equipment_image_url;

    private ProgressBar progressBar;

    DatabaseReference databaseEquipments;

    private ArrayList<String> contents;

    int[] content_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_equipment);

        equipment_photo = findViewById(R.id.equipment_photo);
        progressBar = findViewById(R.id.progressBar);
        name_et = findViewById(R.id.name_et);
        sport_et = findViewById(R.id.sport_et);
        quantity_et = findViewById(R.id.quantity_et);
        contents_ll = findViewById(R.id.contents_ll);
        add_contents = findViewById(R.id.add_contents);
        addbtn = findViewById(R.id.addbtn);
        backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(AddEquipmentActivity.this, MainActivityAdmin.class);
                startActivity(intent);
            }
        });

        contents_count = 1;

        databaseEquipments = FirebaseDatabase.getInstance().getReference("equipments");

        contents = new ArrayList<String>(10);



        //res = getResources();
        //content_ids = res.getIntArray(R.array.content_ids);

        //String test = res.getI

        content_ids = new int[]{R.id.content1,R.id.content2,R.id.content3,R.id.content4,R.id.content5} ;

        //Toast.makeText(this, "id is"+content_ids[0], Toast.LENGTH_SHORT).show();

        add_contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contents_count<5){
                    contents_count++;
                    EditText et = new EditText(getBaseContext());
                    et.setHint(contents_count+".");
                    int id = content_ids[contents_count-1];
                    et.setId(id);
                    //Toast.makeText(getBaseContext(), "id is"+id, Toast.LENGTH_SHORT).show();
                    contents_ll.addView(et);
                }
                else{
                    Toast.makeText(AddEquipmentActivity.this, "Only upto 5 items can be added", Toast.LENGTH_SHORT).show();
                }

            }
        });

        progressBar.setVisibility(View.GONE);

        equipment_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddEquipmentActivity.this, "Select new photo", Toast.LENGTH_SHORT).show();
                showImageChooser();
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToRealtimeDatabase();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriEquipmentImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriEquipmentImage);
                equipment_photo.setImageBitmap(bitmap);
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
        startActivityForResult(Intent.createChooser(intent,"Select equipment image"), CHOOSE_IMAGE);
    }

    private void applyImageToFirebaseStorage(){
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date dateobj = new Date();
        final StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("equipmentpics/"+df.format(dateobj)+".jpg");
        if(uriEquipmentImage != null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageReference.putFile(uriEquipmentImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);

                    equipment_image_url = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddEquipmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveToRealtimeDatabase(){
        String name = name_et.getText().toString();
        String sport = sport_et.getText().toString();
        String photo_url = equipment_image_url;

        int i;
        for(i=0;i<contents_count;i++) {

            EditText contents_et = findViewById(content_ids[i]);
            String content_text = contents_et.getText().toString();
            if(TextUtils.isEmpty(content_text)){
                contents_et.setError("Contents required");
                contents.clear();
                return;
            }
            contents.add(contents_et.getText().toString());
        }

        int total_count = Integer.parseInt(quantity_et.getText().toString());
        int booked_count = 0;

        if(TextUtils.isEmpty(name)){
            name_et.setError("Name required");
            name_et.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(sport)){
            sport_et.setError("Last name required");
            sport_et.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(photo_url) || equipment_image_url==null ){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseEquipments.push().getKey();
        Equipment equipment = new Equipment(id,name,sport,photo_url,contents,total_count,booked_count);

        databaseEquipments.child(id).setValue(equipment);
        Toast.makeText(this, "Equipment added", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddEquipmentActivity.this, MainActivityAdmin.class);
        startActivity(intent);
    }

}
