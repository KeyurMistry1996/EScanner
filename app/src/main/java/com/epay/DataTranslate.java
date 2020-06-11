package com.epay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import static com.epay.R.*;

public class DataTranslate extends AppCompatActivity {
    String id;
    private FirebaseAuth mAuth;
    private DatabaseReference billReference;
    String data;
    TextView title, dataValue;
    Button save, translate;
    String translatedValue;
    Spinner spinnerFrom, spinnerTo;
    String languageFrom;
    String languageTo;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_data_translate);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        title = findViewById(R.id.titleTranslate);
        dataValue = findViewById(R.id.dataValue);
        save = findViewById(R.id.save);
        translate = findViewById(R.id.translatebtn);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        progressBar = findViewById(R.id.progressBar);


        mAuth = FirebaseAuth.getInstance();
        billReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid().toString()).child("Bills").child(id);


        billReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data = (String) dataSnapshot.child("Text").getValue();
                Log.i("data", data);
                title.setText((String) dataSnapshot.child("Name").getValue());
                dataValue.setText((String) dataSnapshot.child("Text").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItem().toString()) {
                    case "English":
                        languageFrom = TranslateLanguage.ENGLISH;
                        break;
                    case "French":
                        languageFrom = TranslateLanguage.FRENCH;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItem().toString()) {
                    case "English":
                        languageTo = TranslateLanguage.ENGLISH;

                        break;

                    case "French":
                        languageTo = TranslateLanguage.FRENCH;
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Log.i("From", languageFrom);
                Log.i("To", languageTo);
                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(languageFrom)
                                .setTargetLanguage(languageTo)
                                .build();
                final Translator dataTranslator =
                        Translation.getClient(options);

                DownloadConditions conditions = new DownloadConditions.Builder()
                        .requireWifi()
                        .build();
                dataTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void v) {

                                        dataTranslator.translate(data).addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                translatedValue = s;
                                                dataValue.setText(s);
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DataTranslate.this, R.string.stringtranslationfail, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(DataTranslate.this, R.string.Languagedataunavailable, Toast.LENGTH_SHORT).show();
                                        // Model couldn’t be downloaded or other internal error.
                                        // ...
                                    }
                                });


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("Text").setValue(translatedValue);
                        Toast.makeText(DataTranslate.this, R.string.SuccessfullyStored, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }


}
