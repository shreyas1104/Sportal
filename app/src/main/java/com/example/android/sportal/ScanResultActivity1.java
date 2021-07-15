package com.example.android.sportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ScanResultActivity1 extends AppCompatActivity {

    ImageView user_iv;
    TextView name_tv;
    TextView degree_tv;
    TextView branch_tv;
    TextView student_id_tv;
    TextView message_tv;

    LinearLayout equipment_layout;
    TextView equipment_name;
    TextView equipment_sport;
    TextView equipment_contents;
    TextView equipment_status;
    TextView equipment_booking_date;
    TextView equipment_issue_date;
    TextView equipment_fine;

    Button action_btn;

    String user_id;
    String user_name;
    String user_dob;
    String user_email;
    String user_degree;
    String user_branch;
    String user_student_id;
    String user_photo_url;
    String user_booking_id;
    String user_booking_date;
    String user_issue_date;
    Boolean user_received;

    DatabaseReference databaseUsers;
    DatabaseReference databaseEquipment;
    DatabaseReference databaseEquipment1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result1);

        user_iv = findViewById(R.id.equipment_iv);
        name_tv = findViewById(R.id.name_tv);
        degree_tv = findViewById(R.id.degree_tv);
        branch_tv = findViewById(R.id.branch_tv);
        student_id_tv = findViewById(R.id.student_id_tv);
        message_tv = findViewById(R.id.message_tv);
        equipment_layout = findViewById(R.id.equipment_layout);
        equipment_name = findViewById(R.id.equipment_name);
        equipment_sport = findViewById(R.id.equipment_sport);
        equipment_contents = findViewById(R.id.equipment_contents);
        equipment_status = findViewById(R.id.equipment_status);
        equipment_booking_date = findViewById(R.id.equipment_booking_date);
        equipment_issue_date = findViewById(R.id.equipment_issue_date);
        equipment_fine = findViewById(R.id.equipment_fine);
        action_btn = findViewById(R.id.action_btn);

        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        user_dob = getIntent().getStringExtra("user_dob");
        user_email = getIntent().getStringExtra("user_email");
        user_degree = getIntent().getStringExtra("user_degree");
        user_branch = getIntent().getStringExtra("user_branch");
        user_student_id = getIntent().getStringExtra("user_student_id");
        user_photo_url = getIntent().getStringExtra("user_photo_url");
        user_booking_id = getIntent().getStringExtra("user_booking_id");
        user_received = getIntent().getBooleanExtra("user_received",false);
        user_booking_date = getIntent().getStringExtra("user_booking_date");
        user_issue_date = getIntent().getStringExtra("user_issue_date");

        databaseEquipment = FirebaseDatabase.getInstance().getReference("equipments");

        Glide.with(getApplicationContext()).load(user_photo_url).into(user_iv);
        name_tv.setText(user_name);
        degree_tv.setText(user_degree);
        branch_tv.setText(user_branch);
        student_id_tv.setText(user_student_id);


    }

    @Override
    protected void onStart() {
        super.onStart();


        databaseEquipment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Equipment equipment_obj = new Equipment();

                for(DataSnapshot equipmentSnapshot : dataSnapshot.getChildren()){
                    Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                    if(equipment.id.equals(user_booking_id))
                        equipment_obj = equipment;
                }
                equipment_name.setText(equipment_obj.name);
                equipment_sport.setText(equipment_obj.sport);

                int i;
                String contents_string = "1." + equipment_obj.contents.get(0);
                for(i=1;i< equipment_obj.contents.size();i++){
                    contents_string = contents_string + "\n" +(i+1)+". "+equipment_obj.contents.get(i);
                }

                equipment_contents.setText(contents_string);

                if(user_received){
                    equipment_status.setText("Received");
                    equipment_issue_date.setText(user_issue_date);
                    //set fine
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date current_date = new Date();
                        Date date2 = dateFormat.parse(user_issue_date);
                        long diff = current_date.getTime() - date2.getTime();
                        //System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                        long fine = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)-1)*20;
                        if(fine>0)
                            equipment_fine.setText("₹"+fine);
                        else
                            equipment_fine.setText("₹0");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                equipment_booking_date.setText(user_booking_date);

                if(user_received){
                    action_btn.setText("Return equipment");
                }

                final Equipment finalEquipment_obj = equipment_obj;
                action_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseUsers = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                        databaseEquipment1 = FirebaseDatabase.getInstance().getReference("equipments").child(finalEquipment_obj.id);

                        if(user_received){
                            //RETURN ITEM
                            User user = new User(user_id, user_name, user_dob, user_email, user_student_id, user_photo_url, user_degree, user_branch, false, false, "", "","");
                            databaseUsers.setValue(user);
                            Equipment equipment = finalEquipment_obj;
                            equipment.booked_count--;
                            databaseEquipment1.setValue(equipment);

                        }
                        else{
                            //ISSUE ITEM
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date current_date = new Date();
                            String issue_date = dateFormat.format(current_date);

                            User user = new User(user_id, user_name, user_dob, user_email, user_student_id, user_photo_url, user_degree, user_branch, true, true, user_booking_id,user_booking_date,issue_date);
                            databaseUsers.setValue(user);
                        }

                        finish();
                        Intent intent = new Intent(ScanResultActivity1.this, ScannerActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(ScanResultActivity1.this, ScannerActivity.class);
        startActivity(intent);
    }
}
