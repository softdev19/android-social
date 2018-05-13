package com.intrix.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.DamagesAdapter;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Category;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.ItemAddedToCartEvent;
import com.intrix.social.utils.ImageUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class TransactionSingleFragment extends Fragment implements View.OnClickListener {

    private Realm mRealm;
    private Category mCategory;

    private TextView mItemsAdded;
    DamagesAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        int id = getActivity().getIntent().getIntExtra(DishPagerFragment.EXTRA_CATEGORY_ID, -1);
        mCategory = mRealm.where(Category.class).equalTo("id", id).findFirst();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exp_dish_list, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.img_transaction);

        ListView list = (ListView) v.findViewById(android.R.id.list);
        mAdapter = new DamagesAdapter(getActivity(), getData());
        list.setAdapter(mAdapter);

        //fabMenu = (FloatingActionMenu) v.findViewById(R.id.fmenu);


        return v;
    }

    private List<Item> getData() {
        return mRealm.where(Item.class)
                .equalTo("categoryId", mCategory.getId())
                .findAllSorted("id", Sort.ASCENDING);
    }

    public void onEvent(ItemAddedToCartEvent event) {
        mItemsAdded.setText(getString(R.string.items_added, Cart.instance().itemsInCartNotConfirmed()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_as_pager:
                EventBus.getDefault().post(new ChangeFragmentEvent(UniversalActivity.class,
                                new DishPagerFragment())
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmFragment.class);
        startActivity(i);
    }
}
