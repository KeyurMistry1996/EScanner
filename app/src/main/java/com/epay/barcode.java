package com.epay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class barcode extends AppCompatActivity  implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private DatabaseReference billReference;
    private FirebaseAuth mAuth;
    EditText ename;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView =new ZXingScannerView(this);
        setContentView(scannerView);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(this, R.string.GP, Toast.LENGTH_SHORT).show();
            }
            else {
                requestPermissions();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermissions();
            }
        }
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);

    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(barcode.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(barcode.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    public void onRequestPermissionResult(int requestCode,String Permission[] , int grantResults[])
    {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), R.string.permissionGranted, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), R.string.permissionDenied, Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel(getString(R.string.both),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }


    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d(String.valueOf(R.string.QR), result.getText());
        Log.d(getString(R.string.QR), result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.alert_dialog_input, null);
        ename = dialogView.findViewById(R.id.username);
        builder.setTitle(R.string.scanResult);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth = FirebaseAuth.getInstance();

                billReference = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(mAuth.getCurrentUser().getUid().toString()).child("Bills");

                DatabaseReference bill = billReference.push();
                bill.child("Name").setValue(ename.getText().toString().trim());
                bill.child("Text").setValue(myResult);
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                bill.child("Created").setValue(date.format(new Date()));
                startActivity(new Intent(barcode.this,MainActivity.class));
            }
        });
        builder.setNeutralButton(R.string.Visit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
                startActivity(browserIntent);
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}