package com.intrix.social.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.DamagesAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.SplitPayment;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class DamagesFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "DamagesFragment";

    private Realm mRealm;
    private Dialog mDialog;
    ListView list;
    DamagesAdapter adapter;

    FloatingActionMenu fabMenu;
    TextView subtotalView;
    TextView totalView;
    TextView taxesView;
    TextView remainingView;
    TextView yourAmountView;
    View yourAmountHolder;
    boolean isMine = false;
    int currentAmount = 0;
    private BroadcastReceiver receiver;
    IntentFilter filter = new IntentFilter();
    boolean amountLocked = false;
    boolean fistTime = false;
    TextView tableNumber;

    View myAmount;
    View  myAmountView;
    View myAmountLiner;
    HorizontalScrollView tagHolder;
    View fragView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);
        View v = inflater.inflate(R.layout.fragment_damages, container, false);
        fragView = v;
        Utils.showToolbar(getActivity());
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.damages);

        list = (ListView) v.findViewById(android.R.id.list);
        adapter = new DamagesAdapter(activity, getData(false));
        list.setAdapter(adapter);
        View empty = v.findViewById(android.R.id.empty);
        list.setEmptyView(empty);

        final View overlay = v.findViewById(R.id.overlay);
        fabMenu = (FloatingActionMenu) v.findViewById(R.id.fmenu);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                overlay
                        .setVisibility(opened ? View.VISIBLE : View.GONE);
            }
        });

        subtotalView = (TextView) v.findViewById(R.id.sub_total);
        totalView = (TextView) v.findViewById(R.id.total);
        taxesView = (TextView) v.findViewById(R.id.taxes);
        remainingView = (TextView) v.findViewById(R.id.remaining);
        tableNumber = (TextView) v.findViewById(R.id.table_no);
        tableNumber.setText(""+Cart.instance().getTableNo());

        yourAmountView = (TextView) v.findViewById(R.id.your_amount);
        yourAmountHolder = v.findViewById(R.id.your_amount_section);
        tagHolder = (HorizontalScrollView) v.findViewById(R.id.tag_holder);

        if (!getData(false).isEmpty()) {
            //int subtotal = Cart.instance().calculateAmount(mRealm);
//            int subtotal = Cart.instance().calculateFullOrderAmount(mRealm);
//            int taxes = (int) ((double) subtotal * 0.26);
//            taxesView.setText(String.valueOf(taxes));
//            subtotalView.setText(String.valueOf(subtotal));
//            totalView.setText(String.valueOf(subtotal + taxes));
            //tableNumber.setText(Cart.instance().getTableNo());
            recalculateAmounts();
        } else {
//            v.findViewById(R.id.total_table).setVisibility(View.GONE);
//            tableNumber.setVisibility(View.GONE);
//            v.findViewById(R.id.table_no_label).setVisibility(View.GONE);
        }

        fabMenu.findViewById(R.id.fab_card).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_online).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_cash).setOnClickListener(this);
        //fabMenu.findViewById(R.id.fab_split).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_my_bill).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_full_bill).setOnClickListener(this);


        if(Cart.instance().getOrderId() > 0) {
            showProgressDialog("Getting order details..");
            Networker.getInstance().getOrderedItems(false);
        }

        filter.addAction("splitNotification");
        filter.addAction("settlementNotification");
        filter.addAction("itemOrderedNotification");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //String account_id = intent.getStringExtra("account_id");

                switch (intent.getAction()) {
                    case "itemOrderedNotification": {
                        refreshOrder();
                    }
                    break;

                    case "splitNotification": {
                        amountLocked = true;
                        recalculateAmounts();
                    }
                    break;
                    case "settlementNotification": {
                        //if(Cart.instance().getSettlementType().equalsIgnoreCase("split"))
                            recalculateAmounts();
                        //else

//                        if(MainApplication.data.loadBooleanData("checkedIn"))
//                            mReserveButton.setText("Tagged. Jump In");
//                        else
//                            mReserveButton.setText("Tagged. Check-In");
                    }
                    break;
                }
            }
        };
        switch (Cart.instance().getSettlementType())
        {
            case "split":
                amountLocked = true;
                break;
        }

        /*
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);

         */

        //setupScrollView();
        return v;
    }


    private List<Item> getData(boolean mine) {
        if(mine) {
            return Cart.instance().getOrderId() != -1 ?
                    Cart.instance().getMyOrderedItems(mRealm) :
                    Collections.<Item>emptyList();
        }else
        {
            return Cart.instance().getOrderId() != -1 ?
                    Cart.instance().getFullOrderedItems(mRealm) :
                    Collections.<Item>emptyList();
        }

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        mRealm.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_my_bill:
                fabMenu.close(true);
                showProgressDialog("Getting only your order details..");
                Networker.getInstance().getOrderedItems(true);
                return;
            case R.id.fab_full_bill:
                fabMenu.close(true);
                showProgressDialog("Getting order details..");
                Networker.getInstance().getOrderedItems(false);
                return;
        }
        if (getData(false).isEmpty()) {
            Toaster.showToast("No items in cart");
            return;
        }
        Intent i = new Intent(getActivity(), UniversalActivity.class);
        switch (v.getId()) {
            case R.id.fab_card:
//                i.putExtra(UniversalActivity.EXTRA_TOKEN, PaymentFragment.class);
//                i.putExtra(PaymentFragment.EXTRA_PAGE, 1);
                //Toaster.toast("Please select online payment and use card there or call waiter and provide card. Much Obliged.", true);
                Snackbar.make(v, "Please select online payment and use card there or call waiter and provide card. Much Obliged.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            case R.id.fab_online:
//                i.putExtra(UniversalActivity.EXTRA_TOKEN, PaymentFragment.class);
//                i.putExtra(PaymentFragment.EXTRA_PAGE, 2);
                //processOnlinePayment()
                showProgressDialog("Processing for Online payment. Please hold");
                Networker.getInstance().processOnlinePayment(currentAmount);

                return;
            case R.id.fab_cash:
//                if(Integer.parseInt(Cart.instance().getSettledAmount()) > 0) {
//                    showProgressDialog("Settling amount...");
//                    Networker.getInstance().settle(getActivity());
//                }
//                else {
//                    showProgressDialog("Settling amount...");
//                    Networker.getInstance().settlement("cash", "" + currentAmount);
//                }
                showProgressDialog("Settling amount...");
                if(Cart.instance().getSettlementType().equalsIgnoreCase("split"))
                    Networker.getInstance().settlement("cash", "" + Cart.instance().getSplitAmount());
                else
                    Networker.getInstance().settlement("cash", "" + currentAmount);
                return;
         /*   case R.id.fab_split:
                if(Cart.instance().isSplitSendingDone())
                {
                    Snackbar.make(v, "This option is not available now. Payment has already been split", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                Activity context = getActivity();
//                ContactSelector.selectContacts(context, selected -> {
//                    SplitDialog.show(context, selected, split -> {
//                        Networker.getInstance().split(split);
//                    });
//                });

                List<CustomerMini> tags = Cart.instance().getTags();

                if(MainApplication.data.loadIntData("taggerId") > 0)
                {
                    Snackbar.make(v, "Only the user who tagged you can Split the bill", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                else if(tags.size() == 0) {
                    Snackbar.make(v, "No users tagged yet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                List<Contact> selected = new ArrayList<>();
                // adding my self
                Customer tempMyUser = MainApplication.data.user;
                selected.add(new Contact(tempMyUser.getName(), tempMyUser.getMobileno()));

                for(CustomerMini tag : tags)
                {
                    selected.add(new Contact(tag.getName(), tag.getMobileno()));
                }

//                selected.add(new Contact("hello", "1234"));
//                selected.add(new Contact("opla", "44444"));

                SplitDialog splitDialog = new SplitDialog();
                splitDialog.show(context, selected, split -> {
                    recalculateAmounts();
                    showProgressDialog("Setting up split payment");
                    Networker.getInstance().split(split);
                });
                return;
            */
            case R.id.fab_my_bill:
                showProgressDialog("Getting only your order details..");
                Networker.getInstance().getOrderedItems(true);
                return;
            case R.id.fab_full_bill:
                showProgressDialog("Getting order details..");
                Networker.getInstance().getOrderedItems(false);
        }
        startActivity(i);
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

        if (event.event.contains("getMyOrderedItems")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                isMine = true;
                Log.i(TAG, "getMyOrderedItems success");
                adapter = new DamagesAdapter(getActivity(), getData(true));
                list.setAdapter(adapter);
                recalculateAmounts();
            } else {
                Log.i(TAG, "getMyOrderedItems failed");
                Toaster.toast("Unable to get your ordered items. Please try again", true);
            }
        }

        if (event.event.contains("getOrderedItems")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                isMine = false;
                Log.i(TAG, "getOrderedItems success");
                adapter = new DamagesAdapter(getActivity(), getData(false));
                list.setAdapter(adapter);
                recalculateAmounts();
            } else {
                Log.i(TAG, "getOrderedItems failed");
                Toaster.toast("Unable to get the full ordered items. Please try again", true);
            }
        }

        if (event.event.contains("settlement")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                isMine = false;
                Log.i(TAG, "settlement success");
                Toaster.toast("Settlement Success", true);
                //adapter = new DamagesAdapter(getActivity(), getData(false));
                //list.setAdapter(adapter);
                //recalculateAmounts();

                EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
                        new GotServedFragment()));

//                showProgressDialog("Settling Order");
//                Networker.getInstance().settle(getActivity());
            } else {
                Log.i(TAG, "settlement failed");
                Toaster.toast("Unable to proceed. Please try again or contact waiter.", true);
            }
        }

        if (event.event.contains("settleOrder")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                isMine = false;
                Log.i(TAG, "settleOrder success");
                Toaster.toast("settleOrder Success");

                //adapter = new DamagesAdapter(getActivity(), getData(false));
                //list.setAdapter(adapter);
                //recalculateAmounts();
            } else {
                Log.i(TAG, "settleOrder failed");
                Toaster.toast("Unable to Settle Order. Please try again or contact waiter.", true);
            }
        }

        if (event.event.contains("processOnlinePayment")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "processOnlinePayment success");
                Toaster.toast("processOnlinePayment Success");
                if(Cart.instance().getSettlementType().equalsIgnoreCase("split"))
                    Networker.getInstance().settlement("online", "" + Cart.instance().getSplitAmount());
                else
                    Networker.getInstance().settlement("online", "" + currentAmount);
                String url = Cart.instance().getPaymentUrl();
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(i);
//                EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
//                        new GotServedFragment()));

                //adapter = new DamagesAdapter(getActivity(), getData(false));
                //list.setAdapter(adapter);
                //recalculateAmounts();
            } else {
                Log.i(TAG, "settleOrder failed");
                Toaster.toast("Unable to Settle Order. Please try again or contact waiter.", true);
            }
        }

        if (event.event.contains("splitCall")) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (event.status) {
                Log.i(TAG, "splitCall success");
                showProgressDialog("Sending split data to users");
                processSplitPayments();
            } else {
                Log.i(TAG, "splitCall failed");
                Toaster.toast("Unable to process split. Please try again", true);
            }
        }

        if (event.event.contains("splitPaymentCall")) {
//            if (mDialog != null) {
//                mDialog.dismiss();
//            }
            if (event.status) {
                Log.i(TAG, "splitPaymentCall success");
                processSplitPayments();
            } else {
                Log.i(TAG, "splitPaymentCall failed");
                Toaster.toast("Unable to process split. Please try again", true);
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        }
    }

    private void recalculateAmounts()
    {
        int subtotal = 0;
        if(isMine)
            subtotal = Cart.instance().calculateMyOrderAmount(mRealm);
        else
            subtotal = Cart.instance().calculateFullOrderAmount(mRealm);
        int taxes = (int) ((double) subtotal * 0.26);
        currentAmount = subtotal + taxes;

        Log.i(TAG, "taxes - "+taxes);
        taxesView.setText(String.valueOf(taxes));
        subtotalView.setText(String.valueOf(subtotal));
        totalView.setText(String.valueOf(currentAmount));
        int cartPendingAmount = Cart.instance().getSettlementPendingAmount();

        if(cartPendingAmount > 0) {
            remainingView.setText(String.valueOf(cartPendingAmount));
            currentAmount = cartPendingAmount;
        }
        else {
            //if(!fistTime)
            Cart.instance().setSettlementPendingAmount(currentAmount);
            remainingView.setText(String.valueOf(currentAmount));
        }

        if(Cart.instance().getSettlementType().equalsIgnoreCase("split"))
        {
            yourAmountHolder.setVisibility(View.VISIBLE);
            //yourAmountView.setVisibility(View.VISIBLE);
            yourAmountView.setText("" + Cart.instance().getSplitAmount());
        }else
        {
            yourAmountHolder.setVisibility(View.GONE);
            //yourAmountView.setVisibility(View.GONE);
        }

//        int splitAmount = Cart.instance().getSplitAmount();
//        if(splitAmount > 0) {
//            yourAmountView.setVisibility(View.VISIBLE);
//            yourAmountView.setText("" + splitAmount);
//        }
//        else
//            yourAmountView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(receiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }

    public void processSplitPayments()
    {
        int splitId = Cart.instance().getSplitId();
        List<SplitPayment> currentSplitPayments = Cart.instance().getSplitPayments();

        List<CustomerMini> tags = Cart.instance().getTags();
        CustomerMini newTag = null;
        boolean fullyCleared = false;
        for(CustomerMini tag: tags)
        {
            boolean cleared = false;
            newTag = null;
            for(SplitPayment sp: currentSplitPayments)
            {
                Log.i(TAG,"sp id " + sp.getId() + " sp cus-id - "+sp.getCustomerId() +"  - tag - id -"+tag.getId());
                if((int)sp.getCustomerId() == (int)tag.getId()) {
                    cleared = true;
                    Log.i(TAG,"cleared true");
                }
            }
            if(!cleared)
            {
                Log.i(TAG,"!cleared !!!!!");
                newTag = tag;
                break;
            }
        }

        if(newTag != null) {
            SplitPayment splitPayment = new SplitPayment();
            splitPayment.setSplitId(splitId);
            splitPayment.setCustomerId(newTag.getId());
            splitPayment.setAmount(newTag.getAmount());
            splitPayment.setMode("cash");
            Networker.getInstance().splitPayments(splitPayment);
        }else if(!Cart.instance().isMySPlitSent())
        {
            SplitPayment splitPayment = new SplitPayment();
            splitPayment.setSplitId(splitId);
            splitPayment.setCustomerId(MainApplication.data.user.getId());
            splitPayment.setAmount(Cart.instance().getSplitAmount());
            splitPayment.setMode("cash");
            Networker.getInstance().splitPayments(splitPayment);
        }else
        {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Toaster.showToast("All splits sent");
        }
    }


    private void setupScrollView()
    {
        WindowManager windowManager = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

//        int width = Utils.dpToPx(dpWidth);
//        int height = Utils.dpToPx(dpHeight);
        int width = Utils.dpToPx(50);
        int height = Utils.dpToPx(50);

        HorizontalScrollView _scrollView = (HorizontalScrollView) fragView.findViewById(R.id.tag_holder);
        //_scrollView.setBackgroundColor(Color.CYAN);
        _scrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        Log.i("DEBUG", "Screen dp width = " + dpWidth + " screen dp height = " + dpHeight);

        TextView view = new TextView(getActivity());
        view.setBackgroundColor(Color.RED);
        view.setText("TEST");

        view.setX(0);

        view.setWidth(width);
        view.setHeight(height - 50);
/*
        TextView view2 = new TextView(getActivity());
        view2.setBackgroundColor(Color.GREEN);
        view2.setText("TEST2");

        view2.setX(0);

        view2.setWidth(width);
        view2.setHeight(height - 50);
*/
        LinearLayout layout = new LinearLayout(getActivity());
        //layout.setBackgroundColor(Color.MAGENTA);

//        layout.addView(view);
//
//        layout.addView(view2);

    layout.addView(view);
        List<CustomerMini> tags = Cart.instance().getTags();

        if(tags != null)
        for(CustomerMini tag: tags)
        {
            //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            ImageView pic = new ImageView(getActivity());
            pic.setLayoutParams(new ViewGroup.LayoutParams(
                    width,
                    height));
            pic.setX(0);
            Glide.with(getActivity()).load(tag.getPic()).error(R.drawable.people_full).into(pic);
            layout.addView(pic);
            //_scrollView.addView(pic);
        }

        _scrollView.addView(layout);
    }

    private void refreshOrder()
    {
        fabMenu.close(true);
        showProgressDialog("Getting order details..");
        Networker.getInstance().getOrderedItems(false);
    }


}
