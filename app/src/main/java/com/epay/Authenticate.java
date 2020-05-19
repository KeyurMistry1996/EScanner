package com.epay;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by Harsh on 2/23/2018.
 */

public class Authenticate extends AppCompatActivity {

    private static FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.frameContainer,new LoginActivity(),Utils.Login_Fragment).commit();
        }

    }

    protected void replaceLoginFragment(){
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.left_enter,R.anim.right_out)
                .replace(R.id.frameContainer,new LoginActivity(),Utils.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

        Fragment SignUp_Fragment = fragmentManager.findFragmentByTag(Utils.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager.findFragmentByTag(Utils.ForgotPassword_Fragment);

        if(SignUp_Fragment != null){
            replaceLoginFragment();
        }
        else if(ForgotPassword_Fragment != null){
            replaceLoginFragment();
        }
        else {
            super.onBackPressed();
        }
    }
}
