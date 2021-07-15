package com.example.android.sportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EquipmentListFragment extends Fragment {

    ListView equipment_lv;

    DatabaseReference databaseEquipments;

    List<Equipment> equipmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);

        equipment_lv = view.findViewById(R.id.equipment_lv);
        databaseEquipments = FirebaseDatabase.getInstance().getReference("equipments");
        equipmentList = new ArrayList<>();

        equipment_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), BookEquipmentActivity.class);
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

        return view;
    }

    @Override
    public void onStart() {
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

                if(getActivity()!=null){
                    //ArrayAdapter adapter = new EquipmentList(getActivity(),equipmentList);
                    ArrayAdapter adapter = new EquipmentList(getActivity(),equipmentList);
                    equipment_lv.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
