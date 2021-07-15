package com.example.android.sportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EquipmentListAdminActivity extends AppCompatActivity {

    ListView equipment_lv;

    DatabaseReference databaseEquipments;

    List<Equipment> equipmentList;

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(EquipmentListAdminActivity.this, MainActivityAdmin.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list_admin);

        equipment_lv = findViewById(R.id.equipment_lv);

        databaseEquipments = FirebaseDatabase.getInstance().getReference("equipments");

        equipmentList = new ArrayList<>();

        equipment_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EquipmentListAdminActivity.this, EquipmentInfoActivity.class);
                intent.putExtra("name",equipmentList.get(i).name);
                intent.putExtra("id",equipmentList.get(i).id);
                intent.putExtra("photo_url",equipmentList.get(i).photo_url);
                intent.putExtra("sport",equipmentList.get(i).sport);
                intent.putExtra("booked_count",equipmentList.get(i).booked_count);
                intent.putExtra("total_count",equipmentList.get(i).total_count);
                intent.putExtra("contents",equipmentList.get(i).contents);

                startActivity(intent);
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseEquipments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                equipmentList.clear();

                for(DataSnapshot equipmentSnapshot : dataSnapshot.getChildren()){
                    Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                    equipmentList.add(equipment);
                }

                //Toast.makeText(EquipmentListAdminActivity.this, equipmentList.get(0).name, Toast.LENGTH_SHORT).show();


                ArrayAdapter adapter = new EquipmentList(EquipmentListAdminActivity.this,equipmentList);
                equipment_lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
