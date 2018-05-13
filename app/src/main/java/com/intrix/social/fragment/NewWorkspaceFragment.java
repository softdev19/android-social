package com.intrix.social.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.intrix.social.Data;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.common.AppMenu;
import com.intrix.social.common.BaseFragment;
import com.intrix.social.common.DateValidateWatcher;
import com.intrix.social.common.Validation;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 01.01.2016.
 */
public class NewWorkspaceFragment extends BaseFragment implements View.OnClickListener {

    private Spinner mSexSpinner,
            mCitySpinner,
            mExpAreaSpinner;
    private CheckBox chk_Opportunity, chk_ConductMeeting, chk_Socialize, chk_NeedWorkSpace, chk_Workspace_Event, chk_Showcase, chk_Social, chk_Other_Space;
    private CheckBox chk_friend, chk_Social_Media, chk_Newspaper, chk_Advertisement, chk_online_media, chk_Other;
    private EditText mPhone,
            mMail, mFullName,
            mDateOfBirth, profession, what_you_do, work_links, additional, et_Other_Space, et_Other;
    private Validation mValidation;
    private int i = 0;

    private int j = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_workspace, container, false);
        mValidation = new Validation(getActivity());
        mSexSpinner = (Spinner) v.findViewById(R.id.sex_spinner);
        String[] sexVars = getResources().getStringArray(R.array.sex);
        mSexSpinner.setAdapter(spinnerAdapter(sexVars));

        mCitySpinner = (Spinner) v.findViewById(R.id.city_spinner);
        String[] cityVars = getResources().getStringArray(R.array.cities);
        mCitySpinner.setAdapter(spinnerAdapter(cityVars));

        mExpAreaSpinner = (Spinner) v.findViewById(R.id.area_of_expertise_spinner);
        String[] areaVars = getResources().getStringArray(R.array.area_of_exp);
        mExpAreaSpinner.setAdapter(spinnerAdapter(areaVars));

        chk_Other_Space = (CheckBox) v.findViewById(R.id.chk_Other_Space);
        chk_Opportunity = (CheckBox) v.findViewById(R.id.chk_Opportunity);
        chk_ConductMeeting = (CheckBox) v.findViewById(R.id.chk_ConductMeeting);
        chk_Socialize = (CheckBox) v.findViewById(R.id.chk_Socialize);
        chk_NeedWorkSpace = (CheckBox) v.findViewById(R.id.chk_NeedWorkSpace);
        chk_Workspace_Event = (CheckBox) v.findViewById(R.id.chk_Workspace_Event);
        chk_Showcase = (CheckBox) v.findViewById(R.id.chk_Showcase);
        chk_Social = (CheckBox) v.findViewById(R.id.chk_Social);
        chk_friend = (CheckBox) v.findViewById(R.id.chk_friend);
        chk_Social_Media = (CheckBox) v.findViewById(R.id.chk_Social_Media);
        chk_Newspaper = (CheckBox) v.findViewById(R.id.chk_Newspaper);
        chk_Advertisement = (CheckBox) v.findViewById(R.id.chk_Advertisement);
        chk_online_media = (CheckBox) v.findViewById(R.id.chk_online_media);
        chk_Other = (CheckBox) v.findViewById(R.id.chk_Other);


        mFullName = (EditText) v.findViewById(R.id.full_name);

        mFullName.setNextFocusDownId(R.id.phone);

        mPhone = (EditText) v.findViewById(R.id.phone);
        mMail = (EditText) v.findViewById(R.id.email);
        mDateOfBirth = (EditText) v.findViewById(R.id.date_of_birth);
        profession = (EditText) v.findViewById(R.id.profession);
        what_you_do = (EditText) v.findViewById(R.id.what_you_do);

        work_links = (EditText) v.findViewById(R.id.work_links);
        additional = (EditText) v.findViewById(R.id.additional);
        et_Other_Space = (EditText) v.findViewById(R.id.et_Other_Space);
        et_Other = (EditText) v.findViewById(R.id.et_Other);


        DateValidateWatcher.bindTo(mDateOfBirth);

        populateFields();

        v.findViewById(R.id.home).setOnClickListener(this);
        v.findViewById(R.id.next).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (checkValidation()) {

                    Log.e("====true====", i + "====" + j);


                    if (checkSpce()) {

                        if (checkSocial()) {
                            calNextButton();
                        } else {
                            objUsefullData.showMsgOnUI("Select  How did you find out about the workspace at Social");
                        }

                    } else {

                        objUsefullData.showMsgOnUI("Select  What will you do with the space");

                    }


                }


                break;
            case R.id.home:
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).openDrawer();
                }
                break;
        }
    }

    private boolean checkSocial() {
        if (chk_friend.isChecked()) {
            j = 1;
            return true;
        }
        if (chk_Social_Media.isChecked()) {
            j = 1;
            return true;
        }
        if (chk_Newspaper.isChecked()) {
            j = 1;
            return true;
        }
        if (chk_Advertisement.isChecked()) {
            j = 1;
            return true;
        }
        if (chk_online_media.isChecked()) {
            j = 1;
            return true;
        }
        if (chk_Other.isChecked()) {
            j = 1;
            return true;
        } else {
            j = 0;
            return false;
        }


    }

    private boolean checkSpce() {

        if (chk_Opportunity.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_ConductMeeting.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_Socialize.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_NeedWorkSpace.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_Workspace_Event.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_Showcase.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_Social.isChecked()) {
            i = 1;
            return true;
        }
        if (chk_Other_Space.isChecked()) {
            i = 1;
            return true;
        } else {
            i = 0;
            return false;
        }
    }

    private void calNextButton() {
        Toaster.showToast("We will get back to you shortly");
        EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
    }

    private void populateFields() {
        Data data = MainApplication.data;
        String email = data.getEmail();
        if (!email.equals("")) {
            mMail.setText(email);
        }
        String name = data.getFirstName() + " " + data.getLastName();
        if (!name.replaceAll(" ", "").equals("")) {
            mFullName.setText(name);
        }
    }

    private ArrayAdapter<String> spinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_workspace_spinner,
                android.R.id.text1,
                data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToolbar(getActivity());
    }


    protected boolean checkValidation() {
        // TODO Auto-generated method stub

        if (mValidation.checkEmpty(mFullName, "Full Name")) {
            return false;
        }

        if (mValidation.checkEmpty(mPhone, "Phone")) {
            return false;
        }
        if (mValidation.checkEmpty(mMail, "Email")) {
            return false;
        }
        if (mValidation.checkEmpty(mDateOfBirth, "DateOfBirth")) {
            return false;
        }
        if (mValidation.checkEmpty(profession, "Profession")) {
            return false;
        }
        if (!mValidation.checkForPhone(mPhone, "Phone")) {
            return false;

        }
        if (mValidation.checkEmpty(what_you_do, "More about You")) {
            return false;
        }
        if (mValidation.checkEmpty(work_links, "Work links")) {
            return false;
        }
        if (mValidation.checkEmpty(additional, "Additional")) {
            return false;
        }
        /*if (mValidation.checkEmpty(et_Other_Space, "Other")) {
            return false;

        }
        if (mValidation.checkEmpty(et_Other, "Other")) {
            return false;

        }*/


        return true;
    }


}
