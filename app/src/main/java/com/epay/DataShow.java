package com.epay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class DataShow extends AppCompatActivity {
    TextView textTitle, dataContent, dataContentDate;
    private FirebaseAuth mAuth;
    private DatabaseReference billReference;
    String data;
    String name;
    DownloadManager downloadManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);

        textTitle = findViewById(R.id.textViewtitle);
        dataContent = findViewById(R.id.dataContent);
        dataContentDate = findViewById(R.id.dataContentDate);

        Intent intent = getIntent();
         id = intent.getStringExtra("id");

        mAuth = FirebaseAuth.getInstance();
        billReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid().toString()).child("Bills").child(id);

        billReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("Name").getValue();
                data = (String) dataSnapshot.child("Text").getValue();
                String date = (String) dataSnapshot.child("Created").getValue();
                textTitle.setText(name);
                dataContent.setText(data);
                dataContentDate.setText(date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menubar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.translate:

                Intent intent = new Intent(getApplicationContext(),DataTranslate.class);
                intent.putExtra("id",id);
                startActivity(intent);


                return (true);
            case R.id.delete:
                //add the function to perform here
                billReference.removeValue();
                Task<Void> docRef = db.collection(mAuth.getUid()).document(name).delete();


                Toast.makeText(this, R.string.Deleted, Toast.LENGTH_SHORT).show();
                finish();
                return (true);
            case R.id.download:
                //add the function to perform here
                try {
                    downloadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, R.string.Download, Toast.LENGTH_SHORT).show();
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }

    private void downloadFile() throws IOException {




        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(String.valueOf(getFilesDir())));
        request1.setDescription("Downloading File...");   //appears the same in Notification bar while downloading
        request1.setTitle(name);
        request1.setVisibleInDownloadsUi(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request1.setDestinationInExternalFilesDir(getApplicationContext(), "/File", name + ".txt");

        DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Toast.makeText(this, "Download Successfully ", Toast.LENGTH_SHORT).show();
        }
    }

}