package com.intrix.social.common;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intrix.social.R;
import com.intrix.social.utils.Toaster;

public class DrawerActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] items;

    private ActionBarDrawerToggle drawerToggle;

    protected void onCreateDrawer() {
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, R.drawable.ic_launcher, 0, 0) {
            public void onDrawerClosed(View view) {
                //              getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(R.string.hello_world);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        //  getActionBar().setDisplayHomeAsUpEnabled(true);
        //  getActionBar().setHomeButtonEnabled(true);

        items = getResources().getStringArray(R.array.drawer_items);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
                items)); //Items is an ArrayList or array with the items you want to put in the Navigation Drawer.

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // Do what you want when an item from the Navigation Drawer is clicked
                Toaster.toast(DrawerActivity.this, "position " + pos);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}


