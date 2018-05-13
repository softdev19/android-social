package com.intrix.social;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.intrix.social.adapter.SocialiteAdapter;
import com.intrix.social.fragment.DiscoveringFragment;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.utils.FontCache;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toanpv on 12/1/15.
 */
public class ResultPeopleActivity extends AppCompatActivity implements View.OnClickListener {
    View menuHeader;
    private Drawer mDrawer;
    Data data;
    boolean filtered = false;
    SocialiteAdapter adapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //((TextView) toolbar.findViewById(android.R.id.title)).setText("Deepika Padukone");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);

        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        data = MainApplication.data;

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface tf = FontCache.get("fonts/montana-regular.otf", this);
        toolbarLayout.setCollapsedTitleTypeface(tf);
        toolbarLayout.setExpandedTitleTypeface(tf);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainApplication.data.searchResult == null || MainApplication.data.searchResult.size() == 0)
                    Snackbar.make(view, "Not enough users to filter :(", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else
                    filter(view);
            }
        });


        listView = (ListView) findViewById(R.id.people_list);
        ViewCompat.setNestedScrollingEnabled(listView, true);
        setItems(null);

    }

    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.dash_header, null);
        BezelImageView pic = (BezelImageView) header.findViewById(R.id.profile_pic);
        return header;
    }

    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_people, menu);
//        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//        searchView.setOnKeyListener();


        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("submit", s);
                if(s.length() > 0)
                    handleSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search
                Log.i("asdf", s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.view_as_list) {
            startActivity(new Intent(this, PeopleDetailActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickPeople(View v) {
        startActivity(new Intent(this, PeopleActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.action_search) {
            if(searchView.getQuery().length() > 0)
            {
                data.userSearchTerm = (String) searchView.getQuery();
                Intent i = new Intent(this, UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //displayText.setText("search for: '" + query + "'...");

            data.userSearchTerm = query;
            Intent i = new Intent(this, UniversalActivity.class);
            i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
            startActivity(i);
        }
    }

    public void handleSearch(String query)
    {
        data.userSearchTerm = query;
        Intent i = new Intent(this, UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
        startActivity(i);
        Log.i("handleSearch","asdfasdfasdfsadf ---- "+query);
        finish();
    }

    public void filter(View view)
    {
        if(filtered)
        {
            setItems(null);
            filtered = false;
            return;
        }

        List<CustomerMini> tempResults = MainApplication.data.searchResult;

        ArrayList<CustomerMini> newList = new ArrayList<>();

        String interest1 = data.user.getInterest1();
        String interest2 = data.user.getInterest2();
        String interest3 = data.user.getInterest3();

        if(interest1 == null)
            interest1 = "";
        if(interest2 == null)
            interest2 = "";
        if(interest3 == null)
            interest3 = "";

        if(interest1.length() == 0 && interest2.length() == 0 && interest3.length() == 0)
        {
            Snackbar.make(view, "Your interest's seem to be unfilled. Please enter them to filter by interests", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            filtered = false;
            return;
        }

        for(CustomerMini c : tempResults)
        {
            String newInterest = c.getInterest1();
            String newInterest2 = c.getInterest2();
            String newInterest3 = c.getInterest3();
            if(newInterest != null &&  (newInterest.equalsIgnoreCase(interest1) || newInterest.equalsIgnoreCase(interest2) || newInterest.equalsIgnoreCase(interest3)))
            {
                newList.add(c);
            }else if(newInterest2 != null &&  (newInterest2.equalsIgnoreCase(interest1) || newInterest2.equalsIgnoreCase(interest2) || newInterest2.equalsIgnoreCase(interest3)))
            {
                newList.add(c);
            }else if(newInterest3 != null &&  (newInterest3.equalsIgnoreCase(interest1) || newInterest3.equalsIgnoreCase(interest2) || newInterest3.equalsIgnoreCase(interest3)))
            {
                newList.add(c);
            }
        }

        if(newList.size() == 0)
        {
            Snackbar.make(view, "None of the users listed here match your interests :(", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            filtered = false;
        }else
        {
            setItems(newList);
            filtered = true;
        }
    }

    void setItems(ArrayList<CustomerMini> socialites)
    {

        if(MainApplication.data.searchResult == null || MainApplication.data.searchResult.size() == 0)
            return;

        adapter = new SocialiteAdapter(this);
        ArrayList<CustomerMini> arrayOfUsers;
        if(socialites == null)
            arrayOfUsers = (ArrayList<CustomerMini>) MainApplication.data.searchResult;
        else
            arrayOfUsers = socialites;

        List<CustomerMini> fullList = new ArrayList<>();

        CustomerMini header1 = new CustomerMini();
        adapter.addSectionHeaderItem(header1);
        fullList.add(new CustomerMini());
        int tempCount = 0;
        for (int i = 0; i < arrayOfUsers.size(); i++) {
            if(arrayOfUsers.get(i).isInsocial()) {
                adapter.addItem(arrayOfUsers.get(i));
                tempCount++;
                fullList.add(arrayOfUsers.get(i));
            }
        }
        header1.setName("Around you at Social: " + tempCount);

        CustomerMini header2 = new CustomerMini();
        adapter.addSectionHeaderItem(header2);
        fullList.add(new CustomerMini());
        tempCount = 0;
        for (int i = 0; i < arrayOfUsers.size(); i++) {
            if(!arrayOfUsers.get(i).isInsocial()){
                adapter.addItem(arrayOfUsers.get(i));
                fullList.add(arrayOfUsers.get(i));
                tempCount++;
            }
        }
        header2.setName("Other SOCIALITES: "+tempCount);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //data.selectedCustomer = arrayOfUsers.get(position);
                data.selectedCustomer = fullList.get(position);
                startActivity(new Intent(ResultPeopleActivity.this, PeopleActivity.class));
            }
        });
    }

}
