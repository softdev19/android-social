package com.intrix.social.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.ConfirmationAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.OrderResponse;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class ConfirmFragment extends Fragment implements
        View.OnClickListener, Callback<OrderResponse> {

    private static final String TAG = ConfirmFragment.class.getSimpleName();

    private Dialog mDialog;
    private Realm mRealm;
    ListView confirmationItemsList;
    ConfirmationAdapter confirmationAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);
        View v = inflater.inflate(R.layout.fragment_confirmation, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.confirmation);

        confirmationItemsList = (ListView) v.findViewById(android.R.id.list);
        confirmationAdapter = new ConfirmationAdapter(activity, getData());
        confirmationItemsList.setAdapter(confirmationAdapter);

        AppCompatActivity a = (AppCompatActivity) getActivity();
        a.setTitle("Confirmation");

        v.findViewById(R.id.fab).setOnClickListener(this);

        return v;
    }

    private List<Item> getData() {
        return Cart.instance().getItemsNotConfirmed(mRealm);
    }

    @Override
    public void onDestroyView() {
        mRealm.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!getData().isEmpty()) {
            //showPreferencesDialog();
            confirm();
        } else {
            Toaster.showToast("You should choose something first");
        }
    }

    private void showPreferencesDialog() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_preferences, null);
        final EditText userInput = (EditText) v.findViewById(R.id.userInput);
        String oldSpecial = MainApplication.data.loadData("temp.comment");
        userInput.setText(oldSpecial);
        new AlertDialog.Builder(getActivity())
                .setTitle("Additional comments")
                .setView(v)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    String specialInfo = userInput.getText().toString();
                    MainApplication.data.saveData("temp.special", specialInfo);
                    dialog.dismiss();
                    confirm();
                })
                .show();
    }

    private void confirm() {
        if (Cart.instance().getOrderId() == -1) {
            Networker.getInstance().order();
            showProgressDialog("Initiating order");
        } else {
            navigateToBill();
        }
    }

    @Override
    public void success(OrderResponse orderResponse, Response response) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (response.getStatus() == 201) {
            Toaster.showToast("Order Initated");
            Cart.instance().setOrderId(orderResponse.getId());
            Log.d(TAG, orderResponse.toString());
            navigateToBill();
        } else {
            Log.e(TAG, response.getStatus() + response.getReason());
        }
    }

    private void navigateToBill() {
        if (Cart.instance().itemsInCartNotConfirmed() != 0) {
            showProgressDialog("Processing order");
            Networker.getInstance().sendItems(getActivity());
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        Toaster.showToast("Operation failed");
        Log.e(TAG, String.valueOf(error.getKind()), error);
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("getOrderId")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "OrderId received");
                navigateToBill();
            } else {
                Log.i(TAG, "OrderId Failed");
            }
        }
        if (event.event.contains("sendItems")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "Items Sent");
                Toaster.toast("Orders Processed. Sit back and relax");
//                EventBus.getDefault().post(new ChangeFragmentEvent(UniversalActivity.class,
//                        new ConfirmedFragment()
//                ));
                Intent i = new Intent(getActivity(), UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmedFragment.class);
                startActivity(i);
            } else {
                Toaster.toast("Unable to process order. Please check with your waiter");
                Log.i(TAG, "Items Send Failed");
            }
        }

        if (event.event.contains("sentOneItem")) {
            if (event.status) {
                Log.i(TAG, "sentOneItem -- ");
//                mDialog.dismiss();
//                confirmationAdapter = new ConfirmationAdapter(getActivity(), getData());
//                confirmationItemsList.setAdapter(confirmationAdapter);
//                showProgressDialog("Processing order");
//                if(mDialog != null)
//                    mDialog.setTitle("Processing order - "+getData().size()+" left");
            } else {
                Log.i(TAG, "sentOneItem Failed");
            }
        }
    }

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }


}
