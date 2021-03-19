package com.example.roadaccidentsafetysystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roadaccidentsafetysystem.Fragments.AboutUsFragment;
import com.example.roadaccidentsafetysystem.Fragments.AddAccidentFragment;
import com.example.roadaccidentsafetysystem.Fragments.EmergencyContactsFragment;
import com.example.roadaccidentsafetysystem.Fragments.MapsFragment;
import com.example.roadaccidentsafetysystem.Fragments.ReviewFragment;
import com.example.roadaccidentsafetysystem.Fragments.UserAccountkFragment;
import com.example.roadaccidentsafetysystem.Fragments.ViewAccidentFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String mUID = "uid";
    ActionBar actionBar;

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView)toolbar.findViewById(R.id.toolbarTextView);
        textView.setText("Road Safety System");

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        firebaseAuth = FirebaseAuth.getInstance();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmergencyContactsFragment()).commit();
        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                Fragment fragment=null;
                switch (id)
                {
                    case R.id.UserAccount:
                        fragment=new UserAccountkFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.shareLocation:
                        fragment=new MapsFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.addAccident:
                        fragment=new AddAccidentFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.viewAccident:
                        fragment=new ViewAccidentFragment();
                        loadFragment(fragment);
                        break;

                    case R.id.emergencyContacts:
                        fragment=new EmergencyContactsFragment();
                        loadFragment(fragment);
                        break;

                    case R.id.reviews:
                        fragment=new ReviewFragment();
                        loadFragment(fragment);
                        break;

                    case R.id.aboutUs:
                        fragment=new AboutUsFragment();
                        loadFragment(fragment);
                        break;

                    case R.id.logout:
                        firebaseAuth.signOut();
                        checkUserStatus();

                    default:
                        return true;
                }
                return true;
            }
        });


        //checkUserStatus();
        loadUserName();

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
        drawer.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }

    private void loadUserName() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("image").getValue();
                            String accountTye = "" + ds.child("accountTye").getValue();
                            String city = "" + ds.child("city").getValue();

                            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);
                            TextView navUsername = (TextView) headerView.findViewById(R.id.nameTv);
                            ImageView navProfileImage = (ImageView) headerView.findViewById(R.id.profileIv);
                            navUsername.setText(name);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.admin_person_bg).into(navProfileImage);
                            }catch (Exception e){
                                navProfileImage.setImageResource(R.drawable.admin_person_bg);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmergencyContactsFragment()).commit();
            finish();
        }
    }

//    @Override
//    protected void onResume() {
//        checkUserStatus();
//        super.onResume();
//    }
//
//    @Override
//    protected void onStart() {
//        checkUserStatus();
//        super.onStart();
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
                finishAndRemoveTask();
                //additional code
            } else {
                getSupportFragmentManager().popBackStack();
            }

//            finishAndRemoveTask();
//            super.onBackPressed();
        }
    }

}