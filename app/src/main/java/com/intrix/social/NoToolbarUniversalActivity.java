package com.intrix.social;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.intrix.social.model.event.ChangeFragmentEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 24.12.2015.
 */
public class NoToolbarUniversalActivity extends AppCompatActivity {

    public static final String EXTRA_TOKEN = "token";

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        EventBus.getDefault().register(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            Class<? extends Fragment> token = (Class<? extends Fragment>)
                    getIntent().getSerializableExtra(EXTRA_TOKEN);
            try {
                fragment = token.newInstance();
            } catch (Exception e) {
                Log.e("CastExc", e.getMessage(), e);
            }

            fm.beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    public void onEvent(ChangeFragmentEvent event) {
        if (event.receiver == getClass()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, event.fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
