package com.example.android.sportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookEquipmentActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;

    ArrayList<String> contents;
    String equipment_id;
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
    Button book_btn;
    Button back_btn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUsers;
    DatabaseReference databaseUsers1;
    DatabaseReference databaseEquipment;
    DatabaseReference databaseEquipment1;

    User user_obj;
    Equipment equipment_obj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_equipment);

        equipment_id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        sport = getIntent().getStringExtra("sport");
        photo_url = getIntent().getStringExtra("photo_url");
        contents = (ArrayList<String>)getIntent().getSerializableExtra("contents");
        booked_count = getIntent().getIntExtra("booked_count",0);
        total_count = getIntent().getIntExtra("total_count",1);

        name_tv = findViewById(R.id.name_tv);
        sport_tv = findViewById(R.id.sport_tv);
        equipment_iv = findViewById(R.id.equipment_iv);
        total_count_tv = findViewById(R.id.total_count_tv);
        booked_count_tv= findViewById(R.id.booked_count_tv);
        available_count_tv = findViewById(R.id.available_count_tv);
        contents_ll = findViewById(R.id.contents_ll);
        book_btn = findViewById(R.id.book_btn);
        back_btn = findViewById(R.id.back_btn);





        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users"); //.child(firebaseAuth.getCurrentUser().getUid());
        databaseUsers1 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid());
        databaseEquipment = FirebaseDatabase.getInstance().getReference("equipments");
        databaseEquipment1 = FirebaseDatabase.getInstance().getReference("equipments").child(equipment_id);
        user_obj = new User();
        equipment_obj = new Equipment();

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
                Intent intent = new Intent(BookEquipmentActivity.this, MainActivityStudent.class);
                startActivity(intent);
            }
        });

        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookEquipment();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if(user.UID.equals(firebaseAuth.getCurrentUser().getUid()) ){
                        user_obj = user;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseEquipment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot equipmentSnapshot : dataSnapshot.getChildren()){
                    Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                    if(equipment.id.equals(equipment_id) ){
                        equipment_obj = equipment;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bookEquipment(){
        if(user_obj.booked){
            Toast.makeText(this, "You have already made a booking", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(booked_count == total_count){
            Toast.makeText(this, "Item unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            user_obj.booked = true;
            user_obj.received = false;

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date current_date = new Date();
            user_obj.booking_date = dateFormat.format(current_date);

            user_obj.booking_id = equipment_id;

            databaseUsers1.setValue(user_obj);

            equipment_obj.booked_count++;

            databaseEquipment1.setValue(equipment_obj);

            Toast.makeText(this, "Equipment booked successfully", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(BookEquipmentActivity.this, MainActivityStudent.class);
            startActivity(intent);
        }


    }
}
