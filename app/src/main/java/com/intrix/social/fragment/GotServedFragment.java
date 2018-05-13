package com.intrix.social.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intrix.social.Data;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.adapter.DamagesAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 24.11.2015.
 */
public class GotServedFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "GotServedFragment";
    private Realm mRealm;
    Data data;
    private Dialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = MainApplication.data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);
        View v = inflater.inflate(R.layout.fragment_cash_settlement, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.wall);
        activity.setTitle("Settlement");

        ListView list = (ListView) v.findViewById(android.R.id.list);
        DamagesAdapter adapter = new DamagesAdapter(activity, getData());
        adapter.setViewMoreView(v.findViewById(R.id.view_more));
        list.setAdapter(adapter);

        TextView subtotalView = (TextView) v.findViewById(R.id.sub_total);
        TextView totalView = (TextView) v.findViewById(R.id.total);
        TextView remainingView = (TextView) v.findViewById(R.id.remaining);

        int subtotal = Cart.instance().calculateFullOrderAmount(mRealm);
        int taxes = (int) ((double) subtotal * 0.26);
        subtotalView.setText(String.valueOf(subtotal));
        totalView.setText(String.valueOf(subtotal + taxes));
        remainingView.setText(Cart.instance().getSettledAmount());

        TextView orderIdView = (TextView) v.findViewById(R.id.order_id);
        orderIdView.setText("Order Id: " + Cart.instance().getOrderId());

        v.findViewById(R.id.fab).setOnClickListener(this);

        data.setSocialCurrency(data.getSocialCurrency() + 5);

        return v;
    }

    private List<Item> getData() {
        //return Cart.instance().getItemsConfirmed(mRealm);
        return Cart.instance().getFullOrderedItems(mRealm);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        mRealm.close();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if(Cart.instance().getSettlementPendingAmount() <= 0) {
            showProgressDialog("Settling amount...");
            Networker.getInstance().settle(getActivity());
        }else
        {
            Snackbar.make(v, "Full amount not settled yet. Please try again after settling all charges", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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

    public void onEvent(NetworkEvent event) {

        if (event.event.contains("settleOrder")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "settleOrder success");
                EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
                                new OrderFeedbackOrSkipFragment())
                );

            } else {
                Log.i(TAG, "settleOrder failed");
                Toaster.toast("Unable to complete order sttlement. Please try again or call your waiter", true);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }



}
