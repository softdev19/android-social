package com.intrix.social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.intrix.social.fragment.SignInFragment;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Customer;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.model.event.SwitchFragEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class SignInActivity extends AppCompatActivity { //} BaseGameActivity {

    private static final String TAG = "SignInActivity";
    FragmentManager fm;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getGameHelper().setConnectOnStart(false);

        EventBus.getDefault().register(this);

//        if(!getSharedPreferences(Data.saveTag, Activity.MODE_PRIVATE).getBoolean("Icon_added", false)){
//            addShortcut();
//            getSharedPreferences(Data.saveTag, Activity.MODE_PRIVATE).edit().putBoolean("Icon_added", true);
//        }

        if(!MainApplication.data.loadBooleanData("Icon_added")) {
            addShortcut();
            MainApplication.data.saveData("Icon_added", true);
        }

        if (MainApplication.data.getLoginStatus()) {
            int customerId;
            try {
                customerId = Integer.parseInt(MainApplication.data.getCustomerId());
                MainApplication.data.refreshUser();
                Cart.instance().setCustomerId(customerId);
                goToDashBoard();
                return;
            } catch (NumberFormatException e) {
                String emailId = MainApplication.data.getEmail();

                if(emailId.length() > 0) {
                    //Toaster.toast("Getting user data", true);
                    Customer customer = new Customer();
                    customer.setEmail(emailId);
                }else
                {
                    MainApplication.data.saveData("login.status", false);
                }
            }
        }

        //FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_container_with_fb);

        fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = new SignInFragment();
            String backStackString = "SignIn";
            fm.beginTransaction().replace(R.id.container, fragment).commit();
        }

        initializeFb();
    }

    public void onEvent(SwitchFragEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        /* Changed by Damoc
        if (event.frag.contains("SignIn Manual")) {
            backStackString = "SignIn Manual";
            fragment = new SignInManualFragment();
        } else if (event.frag.contains("SignUp Manual")) {
            backStackString = "SignUp Manual";
            fragment = new SignUpFragment();
        } else if (event.frag.contains("SignIn Google")) {
            beginUserInitiatedSignIn();
        } else if (event.frag.contains("SignIn Facebook")) {
            loginButton.performClick();
        }*/

        // Changed by Damoc

        if (event.frag.contains("SignIn Facebook")) {
            backStackString = "SignIn Facebook";
            loginButton.performClick();
        }
        //
        if (fragment != null)
            fm.beginTransaction().replace(R.id.container, fragment).addToBackStack(backStackString).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Log.i(TAG, "BackStack count "+ fm.getBackStackEntryCount());
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment instanceof SignInFragment) {
            finish();
        } else {
            fm.popBackStack();
        }
    }

//    @Override
//    public void onSignInFailed() {
//
//    }
//
//    @Override
//    public void onSignInSucceeded() {
//        //mGoogleApiClient = getGameHelper().getApiClient();
//        setProfileInfo();
//        //goToDashBoard();
//    }

    //test
    private void setProfile() {
        MainApplication.data.saveData("user.name", "Damoc");
        MainApplication.data.saveData("signup.type", "facebook");

        MainApplication.data.saveData("user.email", "damocpetre1978@outlook.com");
        MainApplication.data.saveData("user.id", "damocpetre1978@outlook.com");
            MainApplication.data.saveData("user.pic", "http://chorui.com/catalog/update.php?auth_key=v9dtGoBDWq2fxWdqVwFPBdQ3Mo4AKbiM&get=file&filename=1010002.jpg");
        MainApplication.data.saveData("login.status", true);

        CustomerMini customer = new CustomerMini();
        customer.setName("Damoc");
        customer.setEmail("damocpetre1978@outlook.com");
            customer.setPic("http://chorui.com/catalog/update.php?auth_key=v9dtGoBDWq2fxWdqVwFPBdQ3Mo4AKbiM&get=file&filename=1010002.jpg");
        Networker.getInstance().customers(customer);
    }

//    MainApplication.data.saveData("user.uid", data.getUid());
//    MainApplication.data.saveData("user.email", data.getEmail());
//    MainApplication.data.saveData("user.id", data.getEmail());
//    MainApplication.data.saveData("login.status", true);
//    MainApplication.data.saveData("signup.type", "manual");

/*
    public void setProfileInfo() {
        //not sure if mGoogleapiClient.isConnect is appropriate...
        Log.i(TAG, " is connected - " + mGoogleApiClient.isConnected());

        Customer customer = new Customer();

        if (!mGoogleApiClient.isConnected()) {
            customer = null;
        } else if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) == null) {
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            MainApplication.data.saveData("user.email", email);
            MainApplication.data.saveData("user.id", email);
            String username = email.substring(0, email.indexOf("@"));
            MainApplication.data.saveData("user.name", username);
            MainApplication.data.saveData("login.status", true);
            customer.setName(username);
            customer.setEmail(email);

        } else {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            Log.i(TAG, currentPerson.getImage().getUrl() + "?sz=100");
            Log.i(TAG, Plus.AccountApi.getAccountName(mGoogleApiClient));
            String picUrl = currentPerson.getImage().getUrl();
            picUrl = picUrl.replace("?sz=50", "?sz=100");

            String emailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
            MainApplication.data.saveData("user.email", emailId);
            MainApplication.data.saveData("user.id", emailId);
            MainApplication.data.saveData("user.pic", picUrl);
            MainApplication.data.saveData("login.status", true);

            String username = emailId.substring(0, emailId.indexOf("@"));
            MainApplication.data.saveData("user.name", username);

            MainApplication.data.saveData("signup.type", "google");

            customer.setName(currentPerson.getDisplayName());
            customer.setEmail(emailId);

        }
        Networker.getInstance().customers(customer);

    }

    */

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("customers")) {
            if (event.status) {
                Log.i(TAG, "customers - success");
                //Toaster.toast("customers - success", true);
                goToDashBoard();
            } else {
                Log.e(TAG, "customers - failure");
                //Toaster.toast("customers - failure", true);
            }

        }
    }


    private void goToDashBoard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                SignInActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Social");
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
    CallbackManager callbackManager;
    LoginButton loginButton;

    public void initializeFb()
    {

        callbackManager = CallbackManager.Factory.create();

        //fb = (Button) findViewById(R.id.fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);

//       loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

//        List< String > permissionNeeds = Arrays.asList("user_photos", "email",
//                "user_birthday", "public_profile", "AccessToken");
        List< String > permissionNeeds = Arrays.asList("email",
                "user_birthday", "public_profile");
        //List< String > permissionNeeds2 = Arrays.asList("publish_actions");

        loginButton.setReadPermissions(permissionNeeds);
        //loginButton.setPublishPermissions(permissionNeeds2);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        MainApplication.data.saveData("accesToken", accessToken);
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        Log.i("LoginActivity",
                                                response.toString());
                                        try {
                                           String id = object.getString("id");
                                            String pic = "";
                                            try {
                                                URL profile_pic = new URL(
                                                        "https://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.i("profile_pic",
                                                        profile_pic + "");
                                                pic = profile_pic.toString();

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            String username = object.getString("name");
                                            String emailId = object.getString("email");

                                            if(object.has("gender")) {
                                                String gender = object.getString("gender");
                                            }if(object.has("birthday")) {
                                                String birthday = object.getString("birthday");
                                            }


                                            MainApplication.data.saveData("user.name", username);
                                            MainApplication.data.saveData("signup.type", "facebook");

                                            MainApplication.data.saveData("user.email", emailId);
                                            MainApplication.data.saveData("user.id", emailId);
                                            if(pic.length() > 0)
                                            MainApplication.data.saveData("user.pic", pic);
                                            MainApplication.data.saveData("login.status", true);

                                            CustomerMini customer = new CustomerMini();
                                            customer.setName(username);
                                            customer.setEmail(emailId);
                                            if(pic.length() > 0)
                                                customer.setPic(pic);
                                            Networker.getInstance().customers(customer);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toaster.showToast("Unable to connect to Facebook. Please try again or contact Social tech Support team");
                        exception.printStackTrace();
                        System.out.println("onError");
                        try {
                            Log.v("LoginActivity", exception.getMessage());
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
