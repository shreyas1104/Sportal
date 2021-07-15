package com.example.android.sportal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

/**
 * Created by Zamaan on 06-04-2018.
 */

public class MySportal extends Fragment {

    ImageView profile_iv;
    TextView display_name_tv;

    TextView degree_tv;
    TextView branch_tv;
    TextView student_id_tv;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseEquipment;

    String user_name;
    String user_degree;
    String user_branch;
    String user_student_id;
    Boolean user_booked;
    String user_booking_id;
    String user_booking_date;
    String user_issue_date;
    Boolean user_received;

    TextView equipment_issued_tv;
    ImageView equipment_photo;
    RelativeLayout equipment_info_rl;
    TextView equipment_name;
    TextView equipment_sport;
    TextView equipment_contents;
    TextView equipment_bookeddate;
    TextView equipment_booked_date;
    TextView equipment_status;
    TextView equipment_fine;
    TextView equipment_issue_date;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mysportal, container, false);

        user_name=this.getArguments().getString("user_name").toString();
        user_degree=this.getArguments().getString("user_degree").toString();
        user_branch=this.getArguments().getString("user_branch").toString();
        user_student_id=this.getArguments().getString("user_student_id").toString();
        user_booked = this.getArguments().getBoolean("user_booked");
        user_booking_id=this.getArguments().getString("user_booking_id").toString();
        user_booking_date=this.getArguments().getString("user_booking_date").toString();
        user_received =this.getArguments().getBoolean("user_received");
        user_issue_date=this.getArguments().getString("user_issue_date").toString();

        profile_iv = view.findViewById(R.id.profile_iv);
        display_name_tv = view.findViewById(R.id.display_name_tv);
        degree_tv = view.findViewById(R.id.degree_tv);
        branch_tv = view.findViewById(R.id.branch_tv);
        student_id_tv = view.findViewById(R.id.student_id_tv);

        equipment_issued_tv = view.findViewById(R.id.equipment_issued_tv);
        equipment_photo = view.findViewById(R.id.equipment_photo);
        equipment_info_rl = view.findViewById(R.id.equipment_info_rl);
        equipment_name = view.findViewById(R.id.equipment_name);
        equipment_sport = view.findViewById(R.id.equipment_sport);
        equipment_contents = view.findViewById(R.id.equipment_contents);
        equipment_bookeddate = view.findViewById(R.id.equipment_bookeddate);
        equipment_booked_date = view.findViewById(R.id.equipment_booked_date);
        equipment_status = view.findViewById(R.id.equipment_status);
        equipment_fine = view.findViewById(R.id.equipment_fine);
        equipment_issue_date = view.findViewById(R.id.equipment_issue_date);


        firebaseAuth = FirebaseAuth.getInstance();

        databaseEquipment = FirebaseDatabase.getInstance().getReference("equipments");


        display_name_tv.setText(firebaseAuth.getCurrentUser().getDisplayName());

        if(firebaseAuth.getCurrentUser().getPhotoUrl()!=null)
            Glide.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl().toString()).into(profile_iv);

        degree_tv.setText(user_degree);
        branch_tv.setText(user_branch);
        student_id_tv.setText(user_student_id);



        //System.out.println(dateFormat.format(date));






        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(user_booked){
            databaseEquipment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Equipment correct_equipment = new Equipment();
                    for(DataSnapshot equipmentSnapshot : dataSnapshot.getChildren()){
                        Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                        if(equipment.id.equals(user_booking_id)){
                            correct_equipment = equipment;
                        }
                    }

                    Glide.with(getContext()).load(correct_equipment.photo_url).into(equipment_photo);
                    equipment_name.setText(correct_equipment.name);
                    equipment_sport.setText(correct_equipment.sport);

                    int i;
                    String contents_string = "1. "+correct_equipment.contents.get(0);
                    for(i=1;i< correct_equipment.contents.size();i++){
                        contents_string = contents_string + "\n" +(i+1)+". "+correct_equipment.contents.get(i) ;
                    }
                    equipment_contents.setText(contents_string);
                    //String test_string = "1.A\n2.B";
                    //equipment_contents.setText(test_string);


                    if (user_received){
                        equipment_issue_date.setText(user_issue_date);
                        equipment_status.setText("Received");

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


                    equipment_booked_date.setText(user_booking_date);



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            equipment_issued_tv.setText("You have not issued any equipment.");
            equipment_info_rl.setVisibility(View.INVISIBLE);
            equipment_photo.setVisibility(View.INVISIBLE);
        }

    }
}
