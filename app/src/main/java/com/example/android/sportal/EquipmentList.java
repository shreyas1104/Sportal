package com.example.android.sportal;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EquipmentList extends ArrayAdapter<Equipment> {
    private Activity context;
    private List<Equipment> equipmentList;


    public EquipmentList(Activity context,List<Equipment> equipmentList){
        super(context,R.layout.list_layout,equipmentList);
        this.context = context;
        this.equipmentList = equipmentList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        ImageView list_equipment_photo = listViewItem.findViewById(R.id.list_equipment_photo);
        TextView name_tv = listViewItem.findViewById(R.id.name_tv);
        TextView availible_tv = listViewItem.findViewById(R.id.availible_tv);

        Equipment equipment = equipmentList.get(position);

        Glide.with(getContext()).load(equipment.photo_url).into(list_equipment_photo);
        name_tv.setText(equipment.name);
        int available = equipment.total_count-equipment.booked_count;
        availible_tv.setText("Available: "+available);

        return listViewItem;
    }
}
