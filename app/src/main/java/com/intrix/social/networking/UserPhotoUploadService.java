package com.intrix.social.networking;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.intrix.social.MainApplication;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.model.CustomerRequest;
import com.intrix.social.utils.Utils;

import java.io.File;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 27.12.2015.
 */
public class UserPhotoUploadService extends IntentService {

    public static final String LOG_TAG = UserPhotoUploadService.class.getSimpleName();

    public static final String PHOTO_URI = "uri";
    public static final String IS_CAMERA_PHOTO = "cameraPhoto";

    public UserPhotoUploadService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        try {
            Uri uri = (Uri) extras.getParcelable(PHOTO_URI);
            boolean isCamPhoto = extras.getBoolean(IS_CAMERA_PHOTO, false);
            boolean isOriginalPhoto = extras.getBoolean("original", false);
            File file;
            if(isCamPhoto)
                file = MainApplication.data.newProfileImage;
            else if (isOriginalPhoto)
                file = MainApplication.data.ProfileImage;
            else
                file = Utils.uriToFile(uri);

            Cloudinary cloudinary = new Cloudinary(Utils.getClourdinaryConfig());
            Map rawResponse = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String url = (String) rawResponse.get("url");
            Log.d("photo", url);
            MainApplication.data.saveData("user.pic", url);

            CustomerMini customer = new CustomerMini();
            customer.setPic(url);
            CustomerRequest customerRequest = new CustomerRequest(customer);
            Networker.getInstance().updateUser(customerRequest, MainApplication.data.user.getId());
            //MainApplication.data.saveLongData("user.gps_time", System.currentTimeMillis());
            EventBus.getDefault().post(new NetworkEvent("photoUpload", true));

//            User user = new User();
//            UserData data = UserData.get(getApplicationContext());
//            user.setUsername(data.getName());
//            user.setImageLink(url);
//            user.setEmail(data.getEmail());
//            user.setBillingAddress(data.getBillingAddress());
//            user.setShippingAddress(data.getShippingAddress());
//            user.setDob(data.getUserBirth());
            //new Networker().customers(user, r -> Log.d(LOG_TAG, r));
        } catch (Exception e) {
            EventBus.getDefault().post(new NetworkEvent("photoUpload", false));
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
