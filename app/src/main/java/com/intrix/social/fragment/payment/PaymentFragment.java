package com.intrix.social.fragment.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.adapter.DamagesAdapter;
import com.intrix.social.adapter.PagerAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Item;
import com.intrix.social.utils.ImageUtils;
import com.mikepenz.iconics.utils.Utils;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class PaymentFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener{

    public static final String EXTRA_PAGE = "page";

    private Realm mRealm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_payment, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.header_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.wall,
                Utils.convertDpToPx(getActivity(), 200));
        activity.setTitle("");

        Intent i = getActivity().getIntent();
        int initialPage = i.getIntExtra(EXTRA_PAGE, 1);

        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(),
                Arrays.asList("Wallet", "Card", "Net Banking"),
                Arrays.asList(WalletFragment.class, PaymentCardFragment.class, PaymentNetFragment.class)
        );
        pager.setAdapter(adapter);
        pager.setCurrentItem(initialPage);

        TabLayout layout = (TabLayout) v.findViewById(R.id.tabs);

        v.findViewById(R.id.view_bill).setOnClickListener(this);

        layout.setupWithViewPager(pager);

        return v;
    }

    @Override
    public void onClick(View v) {
        View billView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bill, null);

        Cart cart = Cart.instance();
        ListView list = (ListView) billView.findViewById(android.R.id.list);
        List<Item> data = cart.getItemsConfirmed(mRealm);
        DamagesAdapter adapter = new DamagesAdapter(getActivity(), data);
        adapter.setLayoutRes(R.layout.item_damage_reduced);
        list.setAdapter(adapter);

        int total = (int) (cart.calculateAmount(mRealm) * 1.26f);
        TextView totalView = (TextView) billView.findViewById(R.id.total);
        totalView.setText(String.valueOf(total));

        new AlertDialog.Builder(getActivity())
                .setView(billView)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
