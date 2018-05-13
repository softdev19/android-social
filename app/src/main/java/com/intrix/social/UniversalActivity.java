package com.intrix.social;


import android.content.pm.ActivityInfo;
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
 * Created by yarolegovich on 16.12.2015.
 */
public class UniversalActivity extends AppCompatActivity {

    public static final String EXTRA_TOKEN = "token";

    private TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        mTitle = (TextView) toolbar.findViewById(android.R.id.title);

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


    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }

    public void onEvent(ChangeFragmentEvent event) {
        if (event.receiver == getClass()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, event.fragment)
            //        .commit();
            .commitAllowingStateLoss(); // bug fix
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
