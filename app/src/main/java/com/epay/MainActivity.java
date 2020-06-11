package com.epay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private DatabaseReference billReference;
    private Activity context;
    private DisplayData displayData;
    private Data data1;
    ArrayList<Data> dataArrayList = new ArrayList<>();
    private FirebaseRecyclerOptions<Data> options;
    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        context = MainActivity.this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        billReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid().toString()).child("Bills");
        billReference.keepSynced(true);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // listView = findViewById(R.id.bills);


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = databaseReference.child("Users");
        DatabaseReference idreference = reference.child(mAuth.getCurrentUser().getUid());
        DatabaseReference profile = idreference.child("Profile");


        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = (String) dataSnapshot.child("Name").getValue();
                TextView navtitle = findViewById(R.id.nav_title);
                navtitle.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(billReference, Data.class).build();
        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull final Data data) {
                DatabaseReference reference1 = getRef(i);
                final String key = reference1.getKey();


                myViewHolder.textView.setText(data.getName());
                myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        billReference.child(key).removeValue();
                        Task<Void> docRef = db.collection(mAuth.getUid()).document(data.getName()).delete();

                    }
                });

                myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), DataShow.class);
                        intent.putExtra("id", key);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler, parent, false);

                return new MyViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        finishAffinity();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_bill) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (id == R.id.nav_cam_ocr) {
            startActivity(new Intent(getApplicationContext(), ScanActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.languageEnglish) {

                setLocale("en");
                recreate();

            }
        else if(id == R.id.languageFrench) {

                setLocale("fr");
                recreate();


            }
        else if (id == R.id.nav_logout) {
            DialogInterface.OnClickListener dialogClickListner = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mAuth.signOut();
                            Intent startAuthenticate = new Intent(MainActivity.this, Authenticate.class);
                            startAuthenticate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startAuthenticate);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.areyousure).setPositiveButton(R.string.yes, dialogClickListner).setNegativeButton(R.string.no, dialogClickListner).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
       getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
       


    }

    public void loadLocal(){
        SharedPreferences preferences = getSharedPreferences("Setting",Activity.MODE_PRIVATE);
        String language = preferences.getString("My Lang","");
        setLocale(language);
    }

    @Override
    protected void onStart() {

        super.onStart();
        adapter.startListening();

    }


}


