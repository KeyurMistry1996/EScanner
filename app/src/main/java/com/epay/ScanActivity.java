package com.epay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                builder.setTitle(R.string.entername);
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.alert_dialog_input, null);
                ename = dialogView.findViewById(R.id.username);
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = String.valueOf(ename.getText());
                        Log.i("name", name);
                        try {
                            saveData(name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                    Toast.makeText(this, R.string.notaxt, Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveData(String data) throws IOException {

          DatabaseReference bill = billReference.push();
          bill.child("Name").setValue(data);
          bill.child("Text").setValue(dataString);
          SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm");
          bill.child("Created").setValue(date.format(new Date()));
          Log.d(TAG, "Text read: " + data);

//            createPDF(data,dataString);


//        FileOutputStream txt = openFileOutput(data+".txt", Context.MODE_PRIVATE);
//        txt.write(dataString.getBytes());
//        txt.close();
//
//        FileOutputStream pdf = openFileOutput(data+".pdf", Context.MODE_PRIVATE);
//        pdf.write(dataString.getBytes());
//        pdf.close();
//
//        FileOutputStream docx = openFileOutput(data+".docx", Context.MODE_PRIVATE);
//        docx.write(dataString.getBytes());
//        docx.close();



//        Map<String, Object> user = new HashMap<>();
//        user.put("TextFile", txt);
//        user.put("PDFFile", pdf);
//        user.put("DOCXFile", docx);
//
//
//
//        // Add a new document with a generated ID
//        db.collection(mAuth.getUid()).document(data)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot added with ID");
//                    }
//
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });




          Toast.makeText(this, R.string.save, Toast.LENGTH_SHORT).show();
          textValue.setText("");

      }

    private void createPDF(String name, String data) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(50, 50, 30, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(data, 80, 50, paint);
        //canvas.drawt
        // finish the page
        document.finishPage(page);

//        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/"+name+".pdf";
        File file = new File(name+".pdf");
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            document.writeTo(new FileOutputStream(file.getPath()));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }


}

