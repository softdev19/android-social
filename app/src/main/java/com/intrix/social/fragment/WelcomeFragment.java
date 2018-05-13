package com.intrix.social.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.adapter.FoundTablesAdapter;
import com.intrix.social.chat.networking.NetworkConfigs;
import com.intrix.social.common.AppMenu;
import com.intrix.social.fragment.table.TablesFragment;
import com.intrix.social.model.Cart;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.TablesResponse;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yarolegovich on 28.01.2016.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener,
        Callback<List<TablesResponse>>, FacebookCallback<Sharer.Result> {

    private final String TAG = "WelcomeFragment";
    private TextView mReservingStatus;
    private Button mReserveButton; // i am here first then check  table status
    private Button mRequestButton;
    private Dialog mDialog;
    private BroadcastReceiver receiver;
    IntentFilter filter = new IntentFilter();

    View thisView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        mReserveButton = (Button) v.findViewById(R.id.reserve_table);
        mRequestButton = (Button) v.findViewById(R.id.allot_table);
        mRequestButton.setOnClickListener(this);
        mReserveButton.setOnClickListener(this);
        mReservingStatus = (TextView) v.findViewById(R.id.reserve_status);
//        if (!tableNotBooked()) {
//            mReserveButton.setVisibility(View.GONE);
//            mReservingStatus.setText(R.string.status_reserved);
//            mReservingStatus.setVisibility(View.VISIBLE);
//        }
        v.findViewById(R.id.home).setOnClickListener(this);
        v.findViewById(R.id.menu).setOnClickListener(this);

        filter.addAction("tableNotification");
        filter.addAction("tagNotification");
        filter.addAction("tableAcceptedNotification");
        filter.addAction("tableDeniedNotification");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //String account_id = intent.getStringExtra("account_id");

                switch (intent.getAction()) {
                    case "tableNotification": {
                        mReserveButton.setText("Proceed"); //Table alloted.
                    }
                    break;
                    case "tagNotification": {
                        if(MainApplication.data.loadBooleanData("checkedIn"))
                            mReserveButton.setText("Tagged. Jump In");
                        else
                            mReserveButton.setText("Tagged. Check-In");
                    }
                    break;
                    case "tableAcceptedNotification": {
                        //mRequestButton.setText("Table alloted. Proceed");
                        //mRequestButton.setVisibility(View.INVISIBLE);
                        mRequestButton.setText("Table Approved. Change Table?");
                    }
                    break;
                    case "tableDeniedNotification": {
                        mRequestButton.setVisibility(View.VISIBLE);
                        mRequestButton.setText("Table Denied. Request Again?");
                    }
                    break;
                }
            }
        };

        if(MainApplication.data.loadIntData("tableCode") == 0)
            Cart.instance().removeAllFromCart();

        if(MainApplication.data.loadIntData("taggerId") > 0 && MainApplication.data.loadBooleanData("checkedIn"))
            mReserveButton.setText("Tagged. Jump In");
        else if(MainApplication.data.loadIntData("taggerId") > 0 && !MainApplication.data.loadBooleanData("checkedIn"))
            mReserveButton.setText("Tagged. Check In");
        else if(MainApplication.data.loadIntData("tableCode") == 0 && MainApplication.data.loadBooleanData("checkedIn"))
            mReserveButton.setText("Checked-in. Get Status");
        else if (!(MainApplication.data.loadIntData("tableCode") == 0) && MainApplication.data.loadBooleanData("checkedIn"))
            mReserveButton.setText("Proceed");//Table alloted.

        if(MainApplication.data.getMobile().length() == 0) {
            Log.i(TAG, "showMobileNumberBox in oncreateview");
            showMobileNumberBox();
        }

        thisView = v;
        return v;
    }

    public static Bitmap getFacebookProfilePicture(){
        Bitmap bitmap;
        try {
            URL imageURL = new URL(MainApplication.data.getProfilePicUrl());
            HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "facebook.com");
            System.out.println("Request URL ... " + imageURL);
            boolean redirect = false;
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            System.out.println("Response Code ... " + status);

            if (redirect) {
                String newUrl = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "facebook.com");
                System.out.println("Redirect to URL : " + newUrl);

            }

            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            bitmap = null;

        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allot_table:
                showTableNumberBox();
                break;

            case R.id.menu:
//                Networker.getInstance().processOnlinePayment();
//                if(true)
//                    return;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = null;
                fragment = new TablesFragment();
                if (fragment != null) {
                    fm.beginTransaction()
                            .replace(R.id.fl_home, fragment)
                            .commit();
                }
                /* modified by Damoc
                if (tableNotBooked()) {


                    Dialog dialog=new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);
                    Button btn_offer = (Button) dialog.findViewById(R.id.btn_offer);
                    Button btn_share = (Button) dialog
                            .findViewById(R.id.btn_share);


                    btn_share.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub


                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Fragment fragment = null;
                            fragment = new AddShareFragment();
                            if (fragment != null) {
                                fm.beginTransaction()
                                        .replace(R.id.fl_home, fragment)
                                        .commit();
                            }


                            dialog.dismiss();
                        }
                    });

                    btn_offer.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Fragment fragment = null;
                            fragment = new AddOfferFragment();
                            if (fragment != null) {
                                fm.beginTransaction()
                                        .replace(R.id.fl_home, fragment)
                                        .commit();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

//                    askToReserve();

                } else {
                    EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                }*/

                break;
            case R.id.reserve_table:

               /* if(AccessToken.getCurrentAccessToken() != null) {
                    Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
                    for(String perm : permissions)
                    {
                        if(perm.equalsIgnoreCase("publish_actions"))
                            sharePhotoToFacebook();
                        else
                            asktopublish();
                    }
                }else
                    asktopublish();
                if(true)
                    break;
                */
                Log.i(TAG, "cart table no  - " + Cart.instance().getTableNo(getActivity()) + "  --  orderid - " + Cart.instance().getOrderId());
                if(Cart.instance().getTableNo(getActivity()) == -1 && Cart.instance().getOrderId() == -1){

                if (MainApplication.data.loadBooleanData("checkedIn")) {
                    if (MainApplication.data.loadIntData("tableCode") != 0)
                        mReserveButton.setText("Proceed");//Table alloted.
                    Networker.getInstance().getMyTable(this);
                    showProgressDialog("Fetching your table");
                    break;
                }
                    processCheckIn();
                }else if(Cart.instance().getOrderId() == -1)
                {
                    showProgressDialog("Reserving..");
                    Log.i(TAG, "Order generation 1 ");
                    if(Cart.instance().getPosOrderId() <= 0 && MainApplication.data.loadIntData("taggerId") <= 0)
                        Networker.getInstance().getMyTable(this);
                    else {
                        Networker.getInstance().order();
                    }

                }else if(!MainApplication.data.loadBooleanData("checkedIn"))
                {
                    processCheckIn();
                }else {
                    Cart.instance().saveSettledAmount("0");
                    EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                }
                break;

            case R.id.home:
//                if (tableNotBooked()) {
//                    askToReserve();
//                } else {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).openDrawer();
                    }
                //}
                break;
        }
    }

    private void askToReserve() {
        Toaster.showToast("First reserve a table please");
    }

    @Override
    public void success(List<TablesResponse> tablesResponse, Response response) {
        if(mDialog != null)
            mDialog.dismiss();
        if(tablesResponse.size() == 0) {
            Toaster.toast("Table not yet alloted. Please talk to your hostess", true);
            return;
        }
//        else
//        {
//
//            boolean approved = false;
//            for(TablesResponse table : tablesResponse)
//            {
//                if(table.getStatus().equalsIgnoreCase("approved")) {
//                    approved = true;
//                }
//
//            }
//        }

        List<TablesResponse> tablesResponseSingle = new ArrayList<>(tablesResponse.subList(tablesResponse.size() - 1, tablesResponse.size())); // getting only one table
        if(tablesResponseSingle.get(0).getStatus() == null) {

        }if(tablesResponseSingle.get(0).getStatus().equalsIgnoreCase("approved"))
        {

        }else if(tablesResponseSingle.get(0).getStatus().equalsIgnoreCase("denied"))
        {
            Toaster.toast("Table has been denied. Please talk to your hostess or Request another table", true);
            return;
        }else
        {
            Toaster.toast("Table not yet approved. Please talk to your hostess", true);
            return;
        }

        ArrayAdapter<TablesResponse> adapter = new FoundTablesAdapter(getActivity(), tablesResponseSingle);
        int tableId = tablesResponseSingle.get(0).getTableId();
        MainApplication.data.saveIntData("tableCode", tableId);
        new AlertDialog.Builder(getActivity())
                .setTitle("Tap below to proceed")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TablesResponse selected = adapter.getItem(which);
                        showProgressDialog("Reserving");
                        Networker.getInstance().openTable(selected.getTableCode(), 4,
                                new Callback<Integer>() {
                                    @Override
                                    public void success(Integer integer, Response response) {
                                        mDialog.dismiss();

//                                        mReservingStatus.setText(R.string.status_reserved);
//                                        mReserveButton.setVisibility(View.GONE);
//                                        mReservingStatus.setVisibility(View.VISIBLE);

//                                        Cart.instance().

                                        Cart.instance().setPosOrderId(integer);
                                        Cart.instance().setTableNo(selected.getTableId());
                                        Log.i(TAG, " got pos id getting order id");
                                        //EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                                        showProgressDialog("Reserving..");
                                        Networker.getInstance().order();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        if(false) // set it to false on dummy
                                        {
                                            Toaster.showToast("Testing Mode dummy Pos used");
                                            mDialog.dismiss();
                                            Random rnd = new Random(System.currentTimeMillis());
                                            int dummyPos = 1000000 + rnd.nextInt();
                                            Cart.instance().setPosOrderId(dummyPos);
                                            Cart.instance().setTableNo(selected.getTableId());
                                            Log.i(TAG, " got pos id getting order id");
                                            //EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                                            showProgressDialog("Reserving..");
                                            Networker.getInstance().order();
                                        }else
                                            WelcomeFragment.this.failure(error);
                                    }
                                });
                    }
                })
                .setCancelable(true)
                .show();
    }

    @Override
    public void failure(RetrofitError error) {
        Toaster.showToast("Unable to open table. Please try again or contact waiter.");
        mReservingStatus.setVisibility(View.GONE);
        mReserveButton.setVisibility(View.VISIBLE);
        Log.e(getClass().getSimpleName(), error.getMessage(), error);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        Utils.hideToolbar(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToolbar(getActivity());
        EventBus.getDefault().unregister(this);
    }

    private boolean tableNotBooked() {
        return Cart.instance().getTableNo(getActivity()) == -1;
    }

    private void showProgressDialog(String message) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private void processAfterCheckIn()
    {
        MainApplication.data.saveData("checkedIn", true);
        if(MainApplication.data.loadIntData("taggerId") > 0)
            mReserveButton.setText("Tagged. Jump In");
        else if(MainApplication.data.loadIntData("tableCode") == 0)
            mReserveButton.setText("Checked-in. Get Status");
        else
            mReserveButton.setText("Proceed"); //Table alloted.
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("tableNotification")) {
            if (event.status) {
                Log.i(TAG,"Table notification received");
                showProgressDialog("Getting your table");
                Networker.getInstance().getMyTable(this);
                showProgressDialog("Fetching your table");
            } else {
                Log.i(TAG, "Table GCM failed");
            }
        }

        if (event.event.contains("getOrderId")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "OrderId received");
                Cart.instance().saveSettledAmount("0");
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
            } else {
                Log.i(TAG, "OrderId Failed");
                Toaster.toast("Error booking table. Please try again", true);
            }
        }

        if (event.event.contains("setMobileNo")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "setMobileNo received");
                Toaster.toast("Thank you", true);
            } else {
                Toaster.toast("Unable to set mobile number. Please try again", true);
                showMobileNumberBox();
                Log.i(TAG, "setMobileNo Failed");
            }
        }

        if (event.event.contains("allotTableSelf")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "allotTableSelf received");
                Toaster.toast("Table number " + MainApplication.data.loadIntData("tableCode") + " requested", true);
            } else {
                Toaster.toast("Unable to allot table. Please try again", true);
                Log.i(TAG, "setMobileNo Failed");
            }
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter);

    }

    private void processCheckIn()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Checking in to Church St social");
        dialog.setContentView(R.layout.dialog_popup);
        CheckBox ch_share = (CheckBox) dialog.findViewById(R.id.checkbox_share);
        ch_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    NetworkConfigs.USER_ID = MainApplication.data.getUserId();
//                    NetworkConfigs.USER_NAME = MainApplication.data.getUserName();
//                    String messageContent = "Hi I am checking into Church Street Social";
//                    com.intrix.social.chat.networking.Networker.getInstance().sendToWall(messageContent);

                    Log.i(TAG, "Share on facebook checked "+isChecked);
                }
            }
        });

        Button btn_stealth = (Button) dialog.findViewById(R.id.btn_stealth);
        btn_stealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "You are stealth checked in", Toast.LENGTH_SHORT).show();
                //EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                //EventBus.getDefault().post(new ChangePageRequest(AppMenu.DISCOVER_PEOPLE));
                //sharePhotoToFacebook();
                processAfterCheckIn();
            }
        });
        Button btn_announce = (Button) dialog.findViewById(R.id.btn_announce);
        btn_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                        .build();

                CheckBox ch_share = (CheckBox) dialog.findViewById(R.id.checkbox_share);

                if (ch_share.isChecked()) {
                    //asktopublish();
                    shareOnFbProcess();
                }
                NetworkConfigs.USER_ID = MainApplication.data.getUserId();
                NetworkConfigs.USER_NAME = MainApplication.data.getUserName();
                String messageContent = "Hi I am checking into Social";
                com.intrix.social.chat.networking.Networker.getInstance().sendToWall(messageContent);

                new AsyncTask<Void, Void, Void>() {
                    String filePath = "profile.jpg";
                    File output;

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            output = new File(getActivity().getFilesDir() + "/" + filePath);
                            Bitmap bitmap = getFacebookProfilePicture();
                            FileOutputStream fOut = new FileOutputStream(output);
                            if (bitmap != null)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                            fOut.flush();
                            fOut.close();

                            bitmap.recycle();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        NetworkConfigs.USER_ID = MainApplication.data.getUserId();
                        NetworkConfigs.USER_NAME = MainApplication.data.getUserName();
                        NetworkConfigs.CHANNEL_ID = NetworkConfigs.SOCIAL_WALL;
                        com.intrix.social.chat.networking.Networker.getInstance().uploadFile(output.getAbsolutePath());
                        //EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                        processAfterCheckIn();
                        super.onPostExecute(aVoid);
                    }

                }.execute();
            }

        });
        dialog.show();
    }

    private void sharePhotoToFacebook(){

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                //.setCaption("Hi I am checking into Social")
                .setCaption("Hi I've checked-in at Social")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, this);
    }

    @Override
    public void onSuccess(Sharer.Result result) {
        Log.i(TAG, "Facebook share success");
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        Log.i(TAG, "Facebook share error");
        error.printStackTrace();
    }

    private void asktopublish() {
        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG);
                        Toaster.toast("Facebook Done", true);
                        sharePhotoToFacebook();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "Facebook cancel 1");

                    }

                    @Override
                    public void onError(FacebookException error) {
                        error.printStackTrace();
                    }
                });
    }

    public void shareOnFbProcess()
    {
        if(AccessToken.getCurrentAccessToken() != null) {
            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
            for(String perm : permissions)
            {
                if(perm.equalsIgnoreCase("publish_actions"))
                    sharePhotoToFacebook();
                else
                    asktopublish();
            }
        }else
            asktopublish();
    }

    private void showMobileNumberBox() {

        /*
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_mobile, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.userInput);
        String oldComment = "";//data.loadData("temp.tagtext");

        userInput.setText(oldComment);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String mobileNo = userInput.getText().toString();
                                if (mobileNo.length() != 10) {
//                                    Snackbar.make(thisView, "Please enter valid 10 digit Mobile Number.", Snackbar.LENGTH_LONG)
//                                            .setAction("Action", null).show();
                                    Toaster.toast("Please enter valid 10 digit Mobile Number.", true);
                                    //return;
                                } else {
                                    //data.saveData("temp.tagtext", commentText);
                                    //Toaster.toast(getActivity(), "Comment added" + commentText);
                                    dialog.dismiss();
                                    Networker.getInstance().setMobileNo(mobileNo);
                                    showProgressDialog("Setting mobile number");
                                    //postComment();
                                }
                            }
                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        })
        ;

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
*/

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptView = li.inflate(R.layout.dialog_mobile, null);

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(promptView)
                .setCancelable(false)
                //.setTitle(R.string.my_title)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                //.setNegativeButton(android.R.string.cancel, null)
                .create();
        final EditText userInput = (EditText) promptView
                .findViewById(R.id.userInput);
        String oldComment = "";//data.loadData("temp.tagtext");

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        String mobileNo = userInput.getText().toString();
                        if (mobileNo.length() != 10) {
//                                    Snackbar.make(thisView, "Please enter valid 10 digit Mobile Number.", Snackbar.LENGTH_LONG)
//                                            .setAction("Action", null).show();
                            Toaster.toast("Please enter valid 10 digit Mobile Number.", true);
                            //return;
                        } else {
                            d.dismiss();
                            Networker.getInstance().setMobileNo(mobileNo);
                            showProgressDialog("Setting mobile number");
                        }
                        //Dismiss once everything is OK.

                    }
                });
            }
        });

        d.show();

    }



    private void showTableNumberBox() {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptView = li.inflate(R.layout.dialog_table_allot, null);

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(promptView)
                .setCancelable(false)
                        //.setTitle(R.string.my_title)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        final EditText userInput = (EditText) promptView
                .findViewById(R.id.userInput);
        String oldComment = "";//data.loadData("temp.tagtext");

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        String tableNoStr = userInput.getText().toString();
                        if (tableNoStr.length() == 0) {
                            Snackbar.make(thisView, "Please enter valid table Number.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                        } else {
                            d.dismiss();
                            int tableNo = Integer.parseInt(tableNoStr);
                            Networker.getInstance().allotTableSelf(tableNo);
                            showProgressDialog("Requesting Table...");
                        }
                        //Dismiss once everything is OK.

                    }
                });
            }
        });

        d.show();


    }

}