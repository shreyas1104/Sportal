package com.example.android.sportal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivityAdmin extends AppCompatActivity {

    Button scan_btn;
    Button add_equipment_btn;
    Button edit_equipment_btn;
    Button logout_btn;

    FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        scan_btn = findViewById(R.id.scan_btn);
        add_equipment_btn = findViewById(R.id.add_equipment_btn);
        edit_equipment_btn = findViewById(R.id.edit_equipment_btn);
        logout_btn = findViewById(R.id.logout_btn);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityAdmin.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        add_equipment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityAdmin.this, AddEquipmentActivity.class);
                startActivity(intent);
            }
        });

        edit_equipment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityAdmin.this, EquipmentListAdminActivity.class);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder logout_alert_builder = new AlertDialog.Builder(MainActivityAdmin.this);
                logout_alert_builder.setMessage("Are you sure you want to log out?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                                Intent intent = new Intent(MainActivityAdmin.this, StudentLogin.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog logout_alert = logout_alert_builder.create();
                logout_alert.setTitle("Logout");
                logout_alert.show();
            }

        });
    }

    private void logout(){
        firebaseAuth.signOut();
        finish();
    }
}
