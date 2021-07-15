package com.example.android.sportal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class EquipmentInfoActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;

    ArrayList<String> contents;
    String id;
    String name;
    String sport;
    String photo_url;
    int booked_count;
    int total_count;

    TextView name_tv;
    TextView sport_tv;
    ImageView equipment_iv;
    TextView total_count_tv;
    TextView available_count_tv;
    TextView booked_count_tv;
    LinearLayout contents_ll;
    Button edit_btn;
    Button delete_btn;
    Button back_btn;

    ImageView equipment_photo;
    EditText name_et;
    EditText sport_et;
    EditText quantity_et;
    LinearLayout contents_ll_update;
    ImageView add_contents;
    Button edit_btn_update;
    Button back_btn_update;
    ArrayList<String> contents_update;

    int[] content_ids;
    int contents_count;

    Uri uriEquipmentImage;
    String equipment_image_url;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_info);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        sport = getIntent().getStringExtra("sport");
        photo_url = getIntent().getStringExtra("photo_url");
        contents = (ArrayList<String>)getIntent().getSerializableExtra("contents");
        booked_count = getIntent().getIntExtra("booked_count",0);
        total_count = getIntent().getIntExtra("total_count",1);

        contents_update = new ArrayList<String>(10);

        equipment_image_url = photo_url;

        name_tv = findViewById(R.id.name_tv);
        sport_tv = findViewById(R.id.sport_tv);
        equipment_iv = findViewById(R.id.equipment_iv);
        total_count_tv = findViewById(R.id.total_count_tv);
        booked_count_tv= findViewById(R.id.booked_count_tv);
        available_count_tv = findViewById(R.id.available_count_tv);
        contents_ll = findViewById(R.id.contents_ll);
        edit_btn = findViewById(R.id.edit_btn);
        delete_btn = findViewById(R.id.delete_btn);
        back_btn = findViewById(R.id.back_btn);

        name_tv.setText(name);
        sport_tv.setText(sport);
        Glide.with(getApplicationContext()).load(photo_url).into(equipment_iv);
        total_count_tv.setText(""+total_count);
        booked_count_tv.setText(""+booked_count);
        available_count_tv.setText(""+(total_count-booked_count));
        int i;
        for(i=0;i<contents.size();i++){
            TextView textView = new TextView(getApplicationContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            textView.setText(""+(i+1)+". "+contents.get(i));
            contents_ll.addView(textView);
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(EquipmentInfoActivity.this, EquipmentListAdminActivity.class);
                startActivity(intent);
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (booked_count>0){
                    AlertDialog.Builder delete_alert_builder = new AlertDialog.Builder(EquipmentInfoActivity.this);
                    delete_alert_builder.setMessage("This equipment pack has been issued by users. Are you sure you want to delete it?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteData();
                                    Intent intent = new Intent(EquipmentInfoActivity.this, EquipmentListAdminActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog delete_alert = delete_alert_builder.create();
                    delete_alert.setTitle("Delete Equipment");
                    delete_alert.show();
                }
                else{
                    AlertDialog.Builder delete_alert_builder = new AlertDialog.Builder(EquipmentInfoActivity.this);
                    delete_alert_builder.setMessage("Are you sure you want to delete this item?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteData();
                                    Intent intent = new Intent(EquipmentInfoActivity.this, EquipmentListAdminActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog delete_alert = delete_alert_builder.create();
                    delete_alert.setTitle("Delete Equipment");
                    delete_alert.show();
                }

            }
        });

    }

    private void showUpdateDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog,null);

        dialogBuilder.setView(dialogView);

        equipment_photo = dialogView.findViewById(R.id.equipment_photo);
        name_et = dialogView.findViewById(R.id.name_et);
        sport_et = dialogView.findViewById(R.id.sport_et);
        quantity_et = dialogView.findViewById(R.id.quantity_et);
        contents_ll_update = dialogView.findViewById(R.id.contents_ll_update);
        add_contents = dialogView.findViewById(R.id.add_contents);
        edit_btn_update = dialogView.findViewById(R.id.updatebtn);
        back_btn_update = dialogView.findViewById(R.id.backbtn);
        progressBar = dialogView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        Glide.with(getApplicationContext()).load(photo_url).into(equipment_photo);
        name_et.setText(name);
        sport_et.setText(sport);
        quantity_et.setText(""+total_count);




        equipment_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EquipmentInfoActivity.this, "Select new photo", Toast.LENGTH_SHORT).show();
                showImageChooser();
            }
        });


        contents_count = 1;
        content_ids = new int[]{R.id.content1,R.id.content2,R.id.content3,R.id.content4,R.id.content5} ;

        add_contents.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (contents_count < 5) {
                    contents_count++;
                    EditText et = new EditText(getBaseContext());
                    et.setHint(contents_count + ".");
                    int id = content_ids[contents_count - 1];
                    et.setId(id);
                    //Toast.makeText(getBaseContext(), "id is"+id, Toast.LENGTH_SHORT).show();
                    contents_ll_update.addView(et);
                    } else {
                        Toast.makeText(EquipmentInfoActivity.this, "Only upto 5 items can be added", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        edit_btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDatabase(dialogView);
            }
        });

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        back_btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                    Toast.makeText(EquipmentInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateDatabase(View view){

        String name_update = name_et.getText().toString();
        String sport_update = sport_et.getText().toString();
        String photo_url_update = equipment_image_url;


        int i;
        for(i=0;i<contents_count;i++) {
            EditText contents_et = view.findViewById(content_ids[i]);
            String content_text = contents_et.getText().toString();
            if(TextUtils.isEmpty(content_text)){
                contents_et.setError("Contents required");
                contents.clear();
                return;
            }
            contents_update.add(contents_et.getText().toString());
        }

        int total_count_update = Integer.parseInt(quantity_et.getText().toString());

        if(TextUtils.isEmpty(name_update)){
            name_et.setError("Name required");
            name_et.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(sport_update)){
            sport_et.setError("Last name required");
            sport_et.requestFocus();
            return;
        }
        if(total_count_update<booked_count){
            quantity_et.setError("Quantity can't be less then booked quantity");
            quantity_et.requestFocus();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("equipments").child(id);
        Equipment equipment = new Equipment(id,name_update,sport_update,photo_url_update,contents_update,total_count_update,booked_count);
        databaseReference.setValue(equipment);
        Toast.makeText(this, "Equipment updated successfully", Toast.LENGTH_SHORT).show();
        finish();
        Intent intent = new Intent(EquipmentInfoActivity.this, EquipmentListAdminActivity.class);
        startActivity(intent);
    }

    private void deleteData(){
        DatabaseReference databaseEquipments = FirebaseDatabase.getInstance().getReference("equipments").child(id);
        databaseEquipments.removeValue();
        Toast.makeText(this, "Equipment was deleted", Toast.LENGTH_SHORT).show();
    }


}