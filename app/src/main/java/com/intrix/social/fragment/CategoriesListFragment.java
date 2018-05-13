package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.Data;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.CategoriesListListAdapter;
import com.intrix.social.common.AppMenu;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Category;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.Tag;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 02.01.2016.
 */
public class CategoriesListFragment extends Fragment implements View.OnClickListener {

    private static final String CATEGORY_FILTER = "cattype";
    private static final String TAG = CategoriesListFragment.class.getSimpleName();


    public static CategoriesListFragment create(String category) {
        CategoriesListFragment fragment = new CategoriesListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_FILTER, category);
        fragment.setArguments(args);
        return fragment;
    }


    private String mCatType;
    private Realm mRealm;
    private Data data;
    private Dialog mDialog;
    ListView list;
    CategoriesListListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatType = getArguments().getString(CATEGORY_FILTER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);
        data = MainApplication.data;

        View v = inflater.inflate(R.layout.fragment_categories_list, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wood_image);
        int height = activity.getResources().getDimensionPixelSize(R.dimen.image_top_reduced);
        ImageUtils.setBitmapHeader(activity, imageView, getImage(), height);
        list = (ListView) v.findViewById(android.R.id.list);
//        adapter = new CategoriesListListAdapter(getActivity(),getData());
//        list.setAdapter(adapter);
        List<Category> cats = getData();
        if(cats.size() > 0) {
            adapter = new CategoriesListListAdapter(getActivity(), cats);
            list.setAdapter(adapter);
        }else
            refreshMenu();

        final View overlay = v.findViewById(R.id.overlay);
        final FloatingActionMenu fabMenu = (FloatingActionMenu) v.findViewById(R.id.fmenu);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                overlay.setVisibility(opened ? View.VISIBLE : View.GONE);
            }
        });

        fabMenu.findViewById(R.id.fab_call_bill).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_discover).setOnClickListener(this);
        //fabMenu.findViewById(R.id.fab_repeat).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_wall).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_tag).setOnClickListener(this);
        fabMenu.findViewById(R.id.fab_cart).setOnClickListener(this);


        setHasOptionsMenu(true);

        return v;
    }

    private List<Category> getData() {
        return mRealm.where(Category.class)
                .equalTo("catType", mCatType)
                .findAllSorted("id");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categoires_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_as_pager:
                EventBus.getDefault().post(new ChangeFragmentEvent(MainActivity.class,
                        CategoriesFragment.create(mCatType))
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_discover:
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.DISCOVER_PEOPLE));
                break;
//            case R.id.fab_repeat:
//                if (Cart.instance().restoreLastOrder()) {
//                    Intent i = new Intent(getActivity(), UniversalActivity.class);
//                    i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmFragment.class);
//                    startActivity(i);
//                } else {
//                    Toaster.showToast("You haven't ordered anything yet");
//                }
//                break;
            case R.id.fab_wall:
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.SOCIALWALL));
                break;
            case R.id.fab_call_bill:
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.CURRENT_ORDER));
                break;
            case R.id.fab_tag:
                showTagBox();
                break;
            case R.id.fab_cart:
                Intent i = new Intent(getActivity(), UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, ConfirmFragment.class);
                startActivity(i);
                break;
        }
    }


    private int getImage() {
        return mCatType.equals("Food") ? R.drawable.category_list_view :
                R.drawable.category_drink_listview;
    }

    public void tag()
    {
        if(Cart.instance().getOrderId() == -1)
            Toaster.toast("Place at-least one order to start tagging");
        else
        {
            showTagBox();
        }
    }


    private void showTagBox() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_tag, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.userInput);
        String oldComment = data.loadData("temp.tagtext");
        userInput.setText(oldComment);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String commentText = userInput.getText().toString();
                                if (commentText.length() == 0)
                                    return;
                                else {
                                    //data.saveData("temp.tagtext", commentText);
                                    //Toaster.toast(getActivity(), "Comment added" + commentText);
                                    Networker.getInstance().searchForTag(commentText);
                                    dialog.dismiss();
                                    showProgressDialog("Searching...");
                                    //postComment();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private void hideProgress() {
        if(mDialog != null)
            mDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showUserConfirmDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_user_tag_confirm, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        TextView userName = (TextView) promptsView
                .findViewById(R.id.userName);
        ImageView userpic = (ImageView) promptsView
                .findViewById(R.id.userImage);
        CustomerMini tagee = data.tagResult.get(0);
        userName.setText(tagee.getName());
        Glide.with(getActivity()).load(tagee.getPic()).error(R.drawable.no_photo).into(userpic);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Tag tag = new Tag();
                                tag.setCustomerId(tagee.getId());
                                tag.setTaggerId(data.user.getId());
                                tag.setOrderId(Cart.instance().getOrderId());
                                tag.setPosOrderId(Cart.instance().getPosOrderId());
                                Networker.getInstance().tag(tag);
                                showProgressDialog("Tagging " + tagee.getName());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onEvent(NetworkEvent event) {
        if  (event.event.contains("searchForTag")) {
            hideProgress();
            if (event.status) {
                Toaster.toast("searchForTag - success");
                if(data.tagResult.size() > 0)
                    showUserConfirmDialog();
                else
                    Toaster.toast("Unable to find any users with this info. Pls re-enter full email or mobile", true);
            } else {
                Log.i(TAG, "searchForTag - failure");
                Toaster.toast("Unable to find any users with this info", true);
            }
        } else if(event.event.contains("taggingUser"))
        {
            hideProgress();
            if (event.status) {
                //Cart.instance().addTag(MainApplication.data.tagResult.get(0));
                CustomerMini tagMan = MainApplication.data.tagResult.get(0);
                Toaster.toast(tagMan.getName() +" tagged", true);
            } else {
                Log.i(TAG, "searchForTag - failure");
                Toaster.toast("Unable to tag user", true);
            }
        }else if(event.event.contains("getCategories"))
        {
            hideProgress();
            if (event.status) {
                getItems();
            } else {
                Log.i(TAG, "getCategories - failure");
                Toaster.toast("Unable to get the menu. Please try again", true);
            }
        }else if(event.event.contains("getItems"))
        {
            hideProgress();
            if (event.status) {
                getCustomizations();
            } else {
                Log.i(TAG, "getItems - failure");
                Toaster.toast("Unable to get the menu. Please try again", true);
            }
        }else if(event.event.contains("getCustomizations"))
        {
            if (event.status) {
                adapter = new CategoriesListListAdapter(getActivity(),getData());
                list.setAdapter(adapter);
                Toaster.toast("Menu refershed", true);
                hideProgress();
            } else {
                hideProgress();
                Log.i(TAG, "getCustomizations - failure");
                Toaster.toast("Unable to get the menu. Please try again", true);
            }
        }
    }

    void refreshMenu()
    {
        showProgressDialog("Getting Categories..");
        Networker.getInstance().getCategoriesAuto();
    }

    void getItems()
    {
        showProgressDialog("Getting Menu Items..");
        Networker.getInstance().getItemsAuto();
    }

    void getCustomizations()
    {
        showProgressDialog("Getting Menu Customizations..");
        Networker.getInstance().getCustomizationsAuto();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }

}
