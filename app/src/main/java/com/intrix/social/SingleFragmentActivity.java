package com.intrix.social;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intrix.social.fragment.SignUpFragment;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class SingleFragmentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = new SignUpFragment();
            fm.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }
}
