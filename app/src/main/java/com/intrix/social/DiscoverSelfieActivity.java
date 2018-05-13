package com.intrix.social;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.fragment.DiscoveringFragment;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.utils.Toaster;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by toanpv on 11/30/15.
 */
public class DiscoverSelfieActivity extends AppCompatActivity {


    private static final String TAG = "DiscoverSelfieActivity";
    private static final int SELECT_PHOTO = 1233;
    ImageView selfie_btn;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EventBus.getDefault().register(this);

        selfie_btn = (ImageView) findViewById(R.id.selfie_btn);

        String imgLink = MainApplication.data.getProfilePicUrl();
        if (imgLink.length() > 0 && Patterns.WEB_URL.matcher(imgLink).matches()) {
            Glide.with(this).load(imgLink).error(R.drawable.no_photo).placeholder(R.drawable.selfie).fallback(R.drawable.selfie).into(selfie_btn);
            //selfie_btn.setba(Color.TRANSPARENT);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DiscoverSelfieActivity.this, ResultPeopleActivity.class));
//            }
//        });
    }

    public void onClickGetStarted(View view) {

        //if(MainApplication.data.getProfilePicUrl().length() > 0) {
        //startActivity(new Intent(this, DiscoverTalentsActivity.class));

        Intent i = new Intent(this, UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
        startActivity(i);
        finish();

        //}else
        //  Toaster.toast("No photo. No entry", true);
    }

    public void onClickPhoto2(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Set picture")
                .setMessage("Do you want to choose picture for profile?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PHOTO);
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SELECT_PHOTO) {
//            if (resultCode == Activity.RESULT_OK) {
//                Uri selectedImage = data.getData();
//                Glide.with(this).load(selectedImage).into(selfie_btn);
//                MainApplication.data.savePhoto(selectedImage);
//                //data.savePhoto(selectedImage);
//            }
//        }
//    }

    public void onClickPhoto(View view) {

        if (view != null) {
            requestPermissionAndUsePic();
            return;
        }
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DiscoverSelfieActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (items[item].equals("Choose from Library")) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_FILE);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                Glide.with(this).load(selectedImage).into(selfie_btn);
                MainApplication.data.savePhoto(selectedImage, false, false);
                //   onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.WEBP, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            MainApplication.data.newProfileImage = destination;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri selectedImage = Uri.fromFile(destination);
        Log.i(TAG, selectedImage.toString());
        Glide.with(this).load(selectedImage).into(selfie_btn);
        MainApplication.data.savePhoto(selectedImage, true, false);
        showProgressDialog("Uploading your pic");
//        ivImage.setImageBitmap(thumbnail);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void showProgressDialog(String message) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }


    public void onEvent(NetworkEvent event) {
        if (event.event.contains("photoUpload")) {

            if (event.status) {
                if (mDialog != null)
                    mDialog.dismiss();
                Toaster.toast("Pic uploaded", true);
            } else
                Toaster.toast("Pic upload failed", true);
        }
    }

    private void requestPermissionAndUsePic() {
        Dexter.checkPermissions(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                if (report.areAllPermissionsGranted()) {
                    onClickPhoto(null);
                } else {
                    Snackbar.make(selfie_btn, "Bummer. No photo update without permissions :(", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            /* ... */
                Snackbar.make(selfie_btn, "Need access to camera and storage to proceed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                token.continuePermissionRequest();
            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
