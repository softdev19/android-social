package com.intrix.social.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.intrix.social.R;
import com.intrix.social.adapter.TransactionsAdapter;
import com.intrix.social.model.Transaction;
import com.intrix.social.model.Transactions;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Worldtrack 16.12.15.
 */
public class TransactionFragment extends Fragment {

    private static final String LOG_TAG = "TransactionFragment";

    private TransactionsAdapter mAdapter;
    private Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View v = inflater.inflate(R.layout.fragment_transaction, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.img_transaction);

        ListView list = (ListView) v.findViewById(android.R.id.list);
        mAdapter = new TransactionsAdapter(getActivity(), getData());
        list.setAdapter(mAdapter);

        return v;
    }

    private List<Transactions> getData() {
        final List<Transactions> adapterData = new ArrayList<>();
        showProgressDialog("Getting your transactions..");
        Networker.getInstance().getTransactions();

        /*
        Networker.getInstance().getOrders(new Callback<List<OrderItem>>() {
            @Override
            public void success(List<OrderItem> orderItems, Response response) {
                Log.d(LOG_TAG, "got response: " + response.getStatus() + "; " + response.getReason());
                try {
                    int customerId = Integer.parseInt(MainApplication.data.getCustomerId());
                    List<OrderItem> ourOrders = new ArrayList<>();
                    for (OrderItem item : orderItems) {
                        if (customerId == item.getCustomerId()) {
                            ourOrders.add(item);
                        }
                    }

                    removeProgressDialog();
                    new OrderFeedbackMerger(adapterData, ourOrders);

                } catch (NumberFormatException e) {
                    removeProgressDialog();
                    Toaster.toast("We are unable get your transactions at the moment :( .  Please try again later or # our support team. ");
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, error.getMessage(), error);

                removeProgressDialog();
                Toaster.toast("We are unable get your transactions at the moment :( .  Please try again later or # our support team. ");
            }
        });
        */
        return adapterData;
    }

    public void onDataReady(List<Transactions> data) {
        mAdapter.addAll(data);
    }

    /*
    private class OrderFeedbackMerger implements Callback<List<Feedback>> {

        private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        private List<Transactions> resulting;
        private List<OrderItem> orders;

        private OrderFeedbackMerger(List<Transactions> resulting, List<OrderItem> orders) {
            this.resulting = resulting;
            this.orders = orders;
            showProgressDialog("Transactions list obtained. Getting individual Transaction data. Thank you for patience");
            Networker.getInstance().getFeedback(this);
        }

        @Override
        public void success(List<Feedback> feedbacks, Response response) {
            Map<String, List<Transaction>> trans = new HashMap<>();
            for (OrderItem item : orders) {
                Log.d(LOG_TAG, item.toString());
                Transaction transaction = new Transaction();
                transaction.setAmount(String.valueOf(item.getAmount()));
                Feedback feedback = null;
                for (Feedback f : feedbacks) {
                    Log.d(LOG_TAG, f.toString());
                    if (f.getOrderId() == item.getOrderId()) {
                        feedback = f;
                        break;
                    }
                }
                if (feedback != null) {
                    transaction.setComment(feedback.getComments());
                    transaction.setRating(feedback.getRating());
                }
                transaction.setPaymentType(item.getPaymentType());
                String key = item.getCreatedAt().substring(0, item.getCreatedAt().indexOf("T"));
                if (trans.containsKey(key)) {
                    trans.get(key).add(transaction);
                } else {
                    List<Transaction> forDate = new ArrayList<>();
                    forDate.add(transaction);
                    trans.put(key, forDate);
                }
            }
            try {
                for (Map.Entry<String, List<Transaction>> entry : trans.entrySet()) {
                    Transactions transactions = new Transactions();
                    transactions.setTransactions(entry.getValue());
                    transactions.setDate(FORMAT.parse(entry.getKey()));
                    resulting.add(transactions);
                }
                onDataReady(resulting);
                removeProgressDialog();
                Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                onDataReady(Collections.emptyList());
                removeProgressDialog();
                Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(LOG_TAG, error.getMessage(), error);
            removeProgressDialog();
            Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
        }
    }
    */

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private void removeProgressDialog()
    {
        if(mDialog != null)
            mDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void onEvent(NetworkEvent event) {
        if (event.event.contains("getTransactions")) {
            if (mDialog != null)
                mDialog.dismiss();
            if (event.status) {
                processTransactions();
            } else {
                Log.i(LOG_TAG, "getTransactions - failure");
                //Toaster.toast("getTransactions - failure", true);
                Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
            }
        }
    }

    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    void processTransactions()
    {
        List<Transactions> resulting = new ArrayList<>();;
        Realm realm = Realm.getInstance(getActivity());
        List<Transaction> transacs = realm.where(Transaction.class).findAllSorted("createdAt");

        Map<String, List<Transaction>> trans = new HashMap<>();

        for (Transaction transaction : transacs) {
            //Log.d(LOG_TAG, item.toString());
            String key = transaction.getCreatedAt().substring(0, transaction.getCreatedAt().indexOf("T"));
            if (trans.containsKey(key)) {
                trans.get(key).add(transaction);
            } else {
                List<Transaction> forDate = new ArrayList<>();
                forDate.add(transaction);
                trans.put(key, forDate);
            }
        }

        try {
            for (Map.Entry<String, List<Transaction>> entry : trans.entrySet()) {
                Transactions transactions = new Transactions();
                transactions.setTransactions(entry.getValue());
                transactions.setDate(FORMAT.parse(entry.getKey()));
                resulting.add(transactions);
            }
            onDataReady(resulting);
            removeProgressDialog();
            //Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            onDataReady(Collections.emptyList());
            removeProgressDialog();
            //Toaster.toast("We are unable get your transactions data at the moment :( .  Please try again later or # our support team.");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }


}