package com.epay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private ListView billList;
    private DatabaseReference billReference;
    private LinearLayoutManager billManager;
    private Activity context;
    private BillAdapter adapter;

    public MainActivity(){

    }

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        billReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid().toString()).child("Bills");
        billReference.keepSynced(true);
        billList = (ListView) findViewById(R.id.bills);

        billReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> bills = new ArrayList<>();

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    for(DataSnapshot subData : data.getChildren())
                    {
                        if(subData.getKey().toString().equalsIgnoreCase("Text")) bills.add(subData.getValue().toString());
                    }
                }

                ArrayAdapter adp = new ArrayAdapter(context,android.R.layout.simple_list_item_1, bills);
                billList.setAdapter(adp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        } else if (id == R.id.nav_email) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"harshshah112@icloud.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Regarding More Information!!");
            startActivity(emailIntent);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
        }else if(id == R.id.nav_logout){
            DialogInterface.OnClickListener dialogClickListner = new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            mAuth.signOut();
                            Intent startAuthenticate = new Intent(MainActivity.this,Authenticate.class);
                            startAuthenticate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startAuthenticate);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Are You Sure?").setPositiveButton("Yes",dialogClickListner).setNegativeButton("No",dialogClickListner).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class BillAdapter extends ArrayAdapter<Bill> {

        private final Activity context;
        private List<Bill> billL;

        public BillAdapter(Activity context, List<Bill> bill) {
            super(context, R.layout.bill);
            this.context = context;
            this.billL =  bill;
        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.bill, null,true);

            TextView text = (TextView) rowView.findViewById(R.id.data);
            TextView time = (TextView) rowView.findViewById(R.id.create);

            text.setText(billL.get(position).text);
            time.setText(billL.get(position).created);
            return rowView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Nullable
        @Override
        public Bill getItem(int position) {
            return billL.get(position);
        }
    }
}
