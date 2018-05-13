package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.adapter.FeedbackAdapter;
import com.intrix.social.common.AppMenu;
import com.intrix.social.common.ScaleViewOnClick;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Feedback;
import com.intrix.social.model.FeedbackItem;
import com.intrix.social.model.Item;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.ImageUtils;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class OrderFeedbackFragment extends Fragment implements
        View.OnClickListener, DialogInterface.OnClickListener {

    private Realm mRealm;

    private FeedbackAdapter mAdapter;

    private boolean mFeedbackProvided;
    private Button mProceed;

    private int mSelectedRating;
    Data data;
    private Dialog mDialog;
    List<FeedbackItem> feedbackItems;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getInstance(getActivity());
        data = MainApplication.data;
        EventBus.getDefault().register(this);

        View v = inflater.inflate(R.layout.fragment_order_feedback, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ImageView imageView = (ImageView) v.findViewById(R.id.wall_image);
        ImageUtils.setBitmapHeader(activity, imageView, R.drawable.wall);
        activity.setTitle("Feedback");
        TextView tableNo = (TextView) v.findViewById(R.id.table_no);
        tableNo.setText("TableNo: "+ Cart.instance().getTableNo());
        TextView orderId = (TextView) v.findViewById(R.id.order_id);
        orderId.setText("Order Id: "+ Cart.instance().getOrderId());

        Button mainCommentButton = (Button) v.findViewById(R.id.button_comment);
        mainCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentBox(false);
            }
        });

        ScaleViewOnClick animator = new ScaleViewOnClick(this,
                v.findViewById(R.id.feedback_good),
                v.findViewById(R.id.feedback_ok),
                v.findViewById(R.id.feedback_bad)
        );



        List<Item> items = Cart.instance().getFullOrderedItems(mRealm);
        if(data.feedbackItems != null && data.feedbackItems.size() > 0 )
            feedbackItems = data.feedbackItems;
        else {
            feedbackItems = new ArrayList<>();
            for (Item itm : items) {
                FeedbackItem feedbackItem = new FeedbackItem();
                feedbackItem.setItemId(itm.getId());
                feedbackItems.add(feedbackItem);
            }
            data.feedbackItems = feedbackItems;
        }

        ListView list = (ListView) v.findViewById(android.R.id.list);
        mAdapter = new FeedbackAdapter(getActivity(), items);//Cart.instance().getFullOrderedItems(mRealm));
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //data.selectedCustomer = items.get(position);
                data.currentSelectedItemFeedback = items.get(position).getId();
                showCommentBox(true);
                //startActivity(new Intent(getActivity(), PeopleActivity.class));
            }
        });


        mProceed = (Button) v.findViewById(R.id.button_proceed);
        mProceed.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.button_proceed) {
            if (!mFeedbackProvided) {
                mFeedbackProvided = true;
                mProceed.setText("Submit");
            }
            switch (v.getId()) {
                case R.id.feedback_good:
                    mSelectedRating = 4;
                    break;
                case R.id.feedback_ok:
                    mSelectedRating = 2;
                    break;
                case R.id.feedback_bad:
                    mSelectedRating = 1;
                    break;
            }
//        }else if (v.getId() == R.id.button_comment) {
//            showCommentBox(false);
        } else {
            if (mProceed.getText().equals(getString(R.string.skip))) {
                Cart.instance().removeAllFromCart();
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
                data.saveData("temp.comment", "");
            } else {
                if (mAdapter.isRated()) {
                    Cart.instance().removeAllFromCart();
                    rate();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure?")
                            .setMessage("Maybe you want to rate individual items?")
                            .setPositiveButton("Rate", null)
                            .setNegativeButton("Submit", this)
                            .show();
                }
            }
        }
    }

    private void rate() {
        Feedback feedback = new Feedback();
        feedback.setOrderId(Cart.instance().getOrderId());
        feedback.setRating(mSelectedRating);
        showProgressDialog("Sending your precious feedbacks...");
        Networker.getInstance().feedback(feedback);
//        EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
//        data.saveData("temp.comment", "");
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        rate();
    }

    private void showCommentBox(boolean forItem) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_comment, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.userCommentInput);
        String oldComment = getCommentForCurrentItem(data.currentSelectedItemFeedback);
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
//                                    data.saveData("temp.comment", commentText);
//                                    Toaster.toast(getActivity(), "Comment added" + commentText);
//                                    //postComment();
//                                    EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
                                    if (forItem) {
                                        setCommentForCurrentItem(commentText);
                                    }else
                                        data.saveData("temp.comment", commentText);
                                    //Toaster.toast(getActivity(), "Comment added" + commentText);
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if(!forItem)
                                EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("feedback")) {
            if (mDialog != null)
                mDialog.dismiss();

            if (event.status) {
                EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
                data.saveData("temp.comment", "");
            } else {
                Toaster.toast("Unable to send feedback", true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        mRealm.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void showProgressDialog(String message) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private void setCommentForCurrentItem(String comment)
    {
        for(FeedbackItem fBItem : feedbackItems) {
            if (fBItem.getItemId() == data.currentSelectedItemFeedback) {
                fBItem.setComment(comment);
                return;
            }
        }
    }

    private String getCommentForCurrentItem(int itemId)
    {
        for(FeedbackItem fBItem : feedbackItems) {
            if (fBItem.getItemId() == itemId) {
                return fBItem.getComment();

            }
        }

        return "";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.showToolbar(getActivity());
    }

}
