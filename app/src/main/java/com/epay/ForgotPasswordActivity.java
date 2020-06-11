package com.epay;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Harsh on 2/23/2018.
 */
public class ForgotPasswordActivity extends Fragment implements View.OnClickListener{

    private static View view;
    private static EditText emailID;
    private static TextView submit,back;
    private static Animation shakeAnimation;
    private static LinearLayout forgotPasswordLayout;
    private static FirebaseAuth mAuth;
    private static ProgressDialog sendEmailProgress;

    public ForgotPasswordActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_forgotpasswd,container,false);
        initViews();
        setListners();
        return view;
    }

    private void initViews() {

        emailID = (EditText) view.findViewById(R.id.registered_emailid);
        submit = (TextView) view.findViewById(R.id.forgot_button);
        back = (TextView) view.findViewById(R.id.backToLoginBtn);
        forgotPasswordLayout = (LinearLayout) view.findViewById(R.id.forgotpassword_layout);
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
        mAuth = FirebaseAuth.getInstance();
        sendEmailProgress = new ProgressDialog(getActivity());
    }

    private void setListners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.forgot_button:
                boolean valid = submitCheck();
                if(valid) forgotPasswordSubmit();
                break;
            case R.id.backToLoginBtn:
                new Authenticate().replaceLoginFragment();
                break;
        }
    }

    private void forgotPasswordSubmit() {

        sendEmailProgress.setMessage("Sending Mail..");
        sendEmailProgress.setCancelable(false);
        sendEmailProgress.setCanceledOnTouchOutside(false);

        String EmailID = emailID.getText().toString().trim();

        mAuth.sendPasswordResetEmail(EmailID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendEmailProgress.dismiss();
                    Toast.makeText(getActivity(),R.string.passwordemailsent,Toast.LENGTH_SHORT).show();
                    new Authenticate().replaceLoginFragment();
                }else{
                    sendEmailProgress.dismiss();
                    Toast.makeText(getActivity(),R.string.emailnotsend,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean submitCheck() {

        String emailId = emailID.getText().toString().trim();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(emailId);

        if(emailId.equals("") || emailId.length()==0){
            forgotPasswordLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(),view, String.valueOf(R.string.enteremail));
            return false;
        }
        else if(!m.find()){
            forgotPasswordLayout.startAnimation(shakeAnimation);
            new CustomToast().ShowToast(getActivity(),view, String.valueOf(R.string.invalidemail));
            return false;
        }
        else
        {
            return true;
        }
    }
}
