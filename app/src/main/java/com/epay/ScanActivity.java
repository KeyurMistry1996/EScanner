package com.epay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends Activity {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private Button scan, save;
    private static DatabaseReference mDatabase;
    private static DatabaseReference userReference;
    private static DatabaseReference billReference;
    private static FirebaseAuth mAuth;
    private String user_id;
    private String dataString;
    EditText ename;


    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "ScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        statusMessage = (TextView) findViewById(R.id.status_message);
        textValue = (TextView) findViewById(R.id.text_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        scan = (Button) findViewById(R.id.read_text);
        save = findViewById(R.id.saveData);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        user_id = mAuth.getCurrentUser().getUid();

        userReference = mDatabase.child(user_id);

        billReference = userReference.child("Bills");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                builder.setTitle("Enter Name");
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.alert_dialog_input, null);
                ename = dialogView.findViewById(R.id.username);
                builder.setView(dialogView);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = String.valueOf(ename.getText());
                        Log.i("name", name);
                        saveData(name);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null && !data.equals("")) {
                    dataString = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Log.i("data", dataString);
                    statusMessage.setText("Success");
                    textValue.setText(dataString);
                } else {
                    statusMessage.setText("Failure");
                    Toast.makeText(this, "No Text captured", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format("Error",
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveData(String data) {

          DatabaseReference bill = billReference.push();
          bill.child("Name").setValue(data);
          bill.child("Text").setValue(dataString);
          SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm");
          bill.child("Created").setValue(date.format(new Date()));
          Log.d(TAG, "Text read: " + data);
          Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
          textValue.setText("");

      }




    }

