package com.example.android.sportal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityStudent extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUsers;

    private TextView email_tv;
    private TextView name_tv;
    private ImageView user_image_view;

    private String UID;

    User correct_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        correct_user = new User();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            Intent LoginIntent = new Intent(MainActivityStudent.this, StudentLogin.class);
            startActivity(LoginIntent);
        }

        if(firebaseAuth.getCurrentUser().getDisplayName() == null || firebaseAuth.getCurrentUser().getPhotoUrl()==null){
            Intent adduserinfo = new Intent(MainActivityStudent.this, UserInfoActivity.class);
            startActivity(adduserinfo);
        }

        if(firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")){
            Intent admin = new Intent(MainActivityStudent.this, MainActivityAdmin.class);
            startActivity(admin);
        }

        UID = firebaseAuth.getCurrentUser().getUid();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        email_tv = header.findViewById(R.id.email_tv);
        name_tv = header.findViewById(R.id.name_tv);
        user_image_view = header.findViewById(R.id.user_image_view);

        email_tv.setText(firebaseAuth.getCurrentUser().getEmail());
        name_tv.setText(firebaseAuth.getCurrentUser().getDisplayName());

        if(firebaseAuth.getCurrentUser().getPhotoUrl()!=null)
            Glide.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl().toString()).into(user_image_view);


//        loadUserInformation();

        //Default fragment
        //Fragment fragment = new MySportal();
        Fragment fragment = new EquipmentListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.MyFrameLayout, fragment);
        ft.commit();
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
                        correct_user = user;
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if(id == R.id.nav_my_sportal){
            fragment = new MySportal();

            Bundle bundle = new Bundle();
            //bundle.putString("user_photo_url",correct_user.photo_url);
            bundle.putString("user_name",correct_user.name);
            bundle.putString("user_degree", correct_user.degree);
            bundle.putString("user_branch",correct_user.branch);
            bundle.putString("user_student_id",correct_user.student_id);
            bundle.putBoolean("user_booked",correct_user.booked);
            bundle.putString("user_booking_id",correct_user.booking_id);
            bundle.putString("user_booking_date",correct_user.booking_date);
            bundle.putBoolean("user_received",correct_user.received);
            bundle.putString("user_issue_date",correct_user.issue_date);

            fragment.setArguments(bundle);
        }
        else if (id == R.id.nav_show_qr_code) {

            fragment = new QRCode();

        }
        else if (id == R.id.nav_show_sports_equipment) {

            fragment = new EquipmentListFragment();

        }
        else if (id == R.id.nav_about){
            fragment = new About();
        }

        else if (id == R.id.nav_logout) {
           AlertDialog.Builder logout_alert_builder = new AlertDialog.Builder(MainActivityStudent.this);
            logout_alert_builder.setMessage("Are you sure you want to log out?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logout();
                            Intent intent = new Intent(MainActivityStudent.this, StudentLogin.class);
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

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.MyFrameLayout, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        firebaseAuth.signOut();
        finish();
    }
}
