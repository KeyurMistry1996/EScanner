package com.epay;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Fragment implements View.OnClickListener {

    private static View view;
    private static EditText fullName, emailId, mobileNumber, location, password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private static Animation shakeAnimation;
    private static LinearLayout signUpLayout;

    private static DatabaseReference mDatabase;
    private static DatabaseReference userReference;
    private static DatabaseReference profileReference;
    private static FirebaseAuth mAuth;
    private static ProgressDialog registerProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_signup, container, false);
        initViews();
        setListners();
        return view;
    }

    private void initViews() {

        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
        location = (EditText) view.findViewById(R.id.location);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmpassword);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.alreay_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        signUpLayout = (LinearLayout) view.findViewById(R.id.signup_layout);
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        registerProgress = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void setListners() {

        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signUpBtn:
                boolean valid = checkValidation();
                if (valid) doSignUp();
                break;
            case R.id.alreay_user:
                new Authenticate().replaceLoginFragment();
                break;
        }
    }

    private void doSignUp() {

        registerProgress.setMessage(String.valueOf(R.string.registering));
        registerProgress.show();
        registerProgress.setCancelable(false);
        registerProgress.setCanceledOnTouchOutside(false);

        final String getEmailID = emailId.getText().toString().trim();
        String getPassword = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(getEmailID, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.reigistererror, Toast.LENGTH_SHORT).show();
                    registerProgress.dismiss();
                } else {
                    final String user_id = mAuth.getCurrentUser().getUid();

                    userReference = mDatabase.child(user_id);
                    profileReference = userReference.child("Profile");
                    profileReference.child("Name").setValue(fullName.getText().toString().trim());
                    profileReference.child("Email").setValue(getEmailID);
                    profileReference.child("Phone").setValue(mobileNumber.getText().toString().trim());
                    profileReference.child("Location").setValue(location.getText().toString().trim());

                    registerProgress.dismiss();
                    new Authenticate().replaceLoginFragment();
                }
            }
        });
    }

    private boolean checkValidation() {

        String getFullName = fullName.getText().toString().trim();
        String getEmailId = emailId.getText().toString().trim();
        String getMobileNumber = mobileNumber.getText().toString().trim();
        String getLocation = location.getText().toString().trim();
        String getPassword = password.getText().toString().trim();
        String getConfirmPassword = confirmPassword.getText().toString().trim();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        Pattern p1 = Pattern.compile(Utils.passregEx);
        Matcher m1 = p1.matcher(getPassword);

        if (getFullName.equals("") || getFullName.length() == 0 ||
                getEmailId.equals("") || getEmailId.length() == 0 ||
                getMobileNumber.equals("") || getMobileNumber.length() == 0 ||
                getLocation.equals("") || getLocation.length() == 0 ||
                getPassword.equals("") || getPassword.length() == 0 ||
                getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.allrequired));
            return false;
        } else if (!m.find()) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.invalidemail));
            return false;
        } else if (getMobileNumber.length() != 10) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.validphone));
            return false;
        } else if (!m1.find()) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.passwordhint));
            return false;
        } else if (!getConfirmPassword.equals(getPassword)) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.notmatch));
            return false;
        } else if (!terms_conditions.isChecked()) {
            signUpLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(), view, String.valueOf(R.string.selectcondition));
            return false;
        } else {
            return true;
        }
    }
}
