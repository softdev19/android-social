package com.intrix.social.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

import com.intrix.social.lazyloading.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class BaseFragment extends Fragment implements ConstantValue {


	protected UsefullData objUsefullData;
	protected Validation objValidation;
	protected ImageLoader objImageLoader;
	protected SaveData objSaveData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		objUsefullData = new UsefullData(getActivity());
		objValidation = new Validation(getActivity());
		objImageLoader = new ImageLoader(getActivity());
		objSaveData = new SaveData(getActivity());

		setHasOptionsMenu(false);
		setMenuVisibility(false);
		getActivity().invalidateOptionsMenu();
	}
	
	


	// =================== INTERNET ===================//
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			objUsefullData
					.showMsgOnUI("Please check your internet is not working.");
			return false;
		} else
			return true;

	}

	// =================== Write file ===================//
	public void writeFile(String str) {

		File f = new File(Environment.getExternalStorageDirectory()
				+ "/gurudwara_error.txt");
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(str.getBytes(), 0, str.getBytes().length);
			fout.flush();
			fout.close();
		} catch (IOException e) {
			UsefullData.Log("" + e);
			e.printStackTrace();
		}
	}

	// =================================//
	public void sendEmail(String toEmailId, String subject, String msgBody) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { toEmailId });
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, msgBody);
		startActivity(Intent.createChooser(intent, ""));
	}

	// =================================//

 
}