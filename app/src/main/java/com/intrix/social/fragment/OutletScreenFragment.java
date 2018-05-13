package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.adapter.FoundTablesAdapter;
import com.intrix.social.chat.networking.NetworkConfigs;
import com.intrix.social.chat.networking.PhotoUploadHandler;
import com.intrix.social.common.AppMenu;
import com.intrix.social.lazyloading.ImageLoader;
import com.intrix.social.model.Cart;
import com.intrix.social.model.OutletResponse;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.OrderResponse;
import com.intrix.social.networking.model.TablesResponse;
import com.intrix.social.utils.Toaster;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OutletScreenFragment extends android.support.v4.app.Fragment implements View.OnClickListener, Callback<OrderResponse> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private final String TAG = "OutletScreenFragment";
    private TextView tv_checkedIn, tv_Reviews;
    private ImageView iv_call, iv_checkdin, iv_selfie, iv_people, iv_share, iv_back;
    private Button button;
    private TextView tv_hotelName;
    Button mReserveButton;
    String number, Rating, checkhedin, Reviews;
    private Dialog mDialog;
    ImageView iv_one, iv_two, iv_three, iv_four, iv_five;
    // TODO: Rename and change types of parameters
    boolean clcik;
    private TextView mReservingStatus;

    ImageLoader laoder;
    ImageView iv_bakimage;
    String hotel_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_outlet_screen, container, false);


        hotel_name = getArguments().getString("name");
        button = (Button) rootview.findViewById(R.id.button);
        iv_call = (ImageView) rootview.findViewById(R.id.iv_call);
        mReservingStatus = (TextView) rootview.findViewById(R.id.reserve_status);
        iv_checkdin = (ImageView) rootview.findViewById(R.id.iv_checkdin);
        iv_selfie = (ImageView) rootview.findViewById(R.id.iv_selfie);
        iv_people = (ImageView) rootview.findViewById(R.id.iv_people);
        tv_hotelName = (TextView) rootview.findViewById(R.id.tv_hotelName);
        mReserveButton = (Button) rootview.findViewById(R.id.reserve_table);
        tv_checkedIn = (TextView) rootview.findViewById(R.id.tv_checkedIn);
        tv_Reviews = (TextView) rootview.findViewById(R.id.tv_Reviews);
        iv_bakimage = (ImageView) rootview.findViewById(R.id.iv_bakimage);
        iv_share = (ImageView) rootview.findViewById(R.id.iv_share);
        iv_back = (ImageView) rootview.findViewById(R.id.iv_back);
        iv_one = (ImageView) rootview.findViewById(R.id.iv_one);
        iv_two = (ImageView) rootview.findViewById(R.id.iv_two);
        iv_three = (ImageView) rootview.findViewById(R.id.iv_three);
        iv_four = (ImageView) rootview.findViewById(R.id.iv_four);
        iv_five = (ImageView) rootview.findViewById(R.id.iv_five);

        laoder = new ImageLoader(getActivity());
//        if (!tableNotBooked()) {
//            mReserveButton.setVisibility(View.GONE);
//            mReservingStatus.setText(R.string.status_reserved);
//            mReservingStatus.setVisibility(View.VISIBLE);
//        }

        if (!tableNotBooked()) {
            mReserveButton.setVisibility(View.VISIBLE);
            mReserveButton.setText("Table Reserved. Start ordering");
            //mReservingStatus.setText(R.string.status_reserved);
        }


        iv_back.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });
        iv_share.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_layout);
                Button btn_facebbok_share = (Button) dialog.findViewById(R.id.btn_offer);
                Button btn_twitter_share = (Button) dialog
                        .findViewById(R.id.btn_share);
                btn_facebbok_share.setText("Facebook");
                btn_twitter_share.setText("Twiiter");

                btn_facebbok_share.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        String urlToShare = "YOUR_URL";

                        try {
                            Intent intent1 = new Intent();
                            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
                            intent1.setAction("android.intent.action.SEND");
                            intent1.setType("text/plain");
                            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                            startActivity(intent1);
                        } catch (Exception e) {
                            // If we failed (not native FB app installed), try share through SEND
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                            startActivity(intent);
                        }


                    }
                });

                btn_twitter_share.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String url = "http://www.twitter.com/intent/tweet?url=YOURURL&text=YOURTEXT";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                dialog.show();


            }
        });
        iv_call.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);

            }
        });

        iv_checkdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!clcik) {

                    checkhedin = checkhedin.replace(",", "");

                    Float check = (Float.parseFloat(checkhedin));

                    tv_checkedIn.setText("" + check++);

                }
                clcik = true;

            }
        });

        iv_selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PhotoUploadHandler.deviceSupportsCamera(getActivity()))
                    PhotoUploadHandler.takePhoto(OutletScreenFragment.this);
                else
                    Toaster.toast("No camera support", true);

                /*
                FragmentManager fm = getActivity().getSupportFragmentManager();
                android.support.v4.app.Fragment fragment = null;
                fragment = new WallFragment();
                if (fragment != null) {
                    fm.beginTransaction()
                            .replace(R.id.rl_outlet, fragment)
                            .commit();
                }

                */

            }
        });

        iv_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                android.support.v4.app.Fragment fragment = null;
                fragment = new DiscoverFragment();
                if (fragment != null) {
                    fm.beginTransaction()
                            .replace(R.id.rl_outlet, fragment)
                            .commit();
                }

            }
        });
        mReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (tableNotBooked()) {
                if(Cart.instance().getTableNo(getActivity()) == -1 && Cart.instance().getOrderId() == -1){

                    mReserveButton.setVisibility(View.GONE);
                    mReservingStatus.setVisibility(View.VISIBLE);
                    showProgressDialog("Obtaining free tables");
                    Log.e("hereee table is booking", "yesss");
                    Networker.getInstance().getTables(new Callback<List<TablesResponse>>() {
                        @Override
                        public void success(List<TablesResponse> tablesResponses, Response response) {
                            {
                                mDialog.dismiss();
                                ArrayAdapter<TablesResponse> adapter = new FoundTablesAdapter(getActivity(), tablesResponses);
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Found free tables")
                                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                TablesResponse selected = adapter.getItem(which);
                                                showProgressDialog("Reserving");
                                                Networker.getInstance().openTable(selected.getTableCode(), 4,
                                                        new Callback<Integer>() {
                                                            @Override
                                                            public void success(Integer integer, Response response) {
//                                                                mDialog.dismiss();
//
//                                                                mReservingStatus.setText(R.string.status_reserved);
//                                                                mReserveButton.setVisibility(View.GONE);
//                                                                mReservingStatus.setVisibility(View.VISIBLE);

                                                                Cart.instance().setPosOrderId(integer);
                                                                Cart.instance().setTableNo(selected.getTableId());
                                                                Networker.getInstance().order(OutletScreenFragment.this);
//                                                                EventBus.getDefault().post(new ChangePageRequest(2));
                                                            }

                                                            @Override
                                                            public void failure(RetrofitError error) {
                                                                OutletScreenFragment.this.failure(error);
                                                            }
                                                        });
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "Unable to get tables");
                            Toaster.toast("Unable to get tables", true);
                        }
                    });
                }else if(Cart.instance().getOrderId() == -1)
                {
                    showProgressDialog("Reserving.");
                    Networker.getInstance().order(OutletScreenFragment.this);
                }
                else
                {
                    EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                }

//                {
//                    Log.i(TAG, "Table has already been booked");
//                    Toaster.toast("Table has already been booked", true);
//                }

                /*
                Random random = new Random();
                int waitNumber = random.nextInt(10);
                Toaster.toast("In waiting list - "+waitNumber,true);
                EventBus.getDefault().post(new ChangePageRequest(2));
                */
            }
        });
        Networker.getInstance().getOutletData(new Callback<List<OutletResponse>>() {
            @Override
            public void success(List<OutletResponse> outletResponses, Response response) {
                int index = 0;

                Log.e("hotel_name",""+hotel_name);

                for (int i = 0; i < outletResponses.size(); i++) {
                    OutletResponse outletdata = outletResponses.get(i);

                    if (hotel_name.contains(outletResponses.get(i).getName())) {
                        index = i;
                        number = outletResponses.get(index).getPhoneNumber();
                        float Rating = Float.parseFloat(outletResponses.get(index).getRatings());
                        checkhedin = outletResponses.get(index).getCheckedIn();
                        Reviews = outletResponses.get(index).getReviews();
                        tv_hotelName.setText(outletResponses.get(index).getName());
                        laoder.DisplayImage(outletResponses.get(index).getImage(), iv_bakimage);
                        tv_Reviews.setText(Reviews);
                        tv_checkedIn.setText(checkhedin);
                        if (Rating <= 1) {
                            iv_one.setBackgroundResource(R.drawable.star);
                            iv_two.setBackgroundResource(R.drawable.star_gray);
                            iv_three.setBackgroundResource(R.drawable.star_gray);
                            iv_four.setBackgroundResource(R.drawable.star_gray);
                            iv_five.setBackgroundResource(R.drawable.star_gray);
                        } else if (Rating <= 2) {
                            iv_one.setBackgroundResource(R.drawable.star);
                            iv_two.setBackgroundResource(R.drawable.star);
                            iv_three.setBackgroundResource(R.drawable.star_gray);
                            iv_four.setBackgroundResource(R.drawable.star_gray);
                            iv_five.setBackgroundResource(R.drawable.star_gray);
                        } else if (Rating <= 3) {
                            iv_one.setBackgroundResource(R.drawable.star);
                            iv_two.setBackgroundResource(R.drawable.star);
                            iv_three.setBackgroundResource(R.drawable.star);
                            iv_four.setBackgroundResource(R.drawable.star_gray);
                            iv_five.setBackgroundResource(R.drawable.star_gray);
                        } else if (Rating <= 4) {
                            iv_one.setBackgroundResource(R.drawable.star);
                            iv_two.setBackgroundResource(R.drawable.star);
                            iv_three.setBackgroundResource(R.drawable.star);
                            iv_four.setBackgroundResource(R.drawable.star);
                            iv_five.setBackgroundResource(R.drawable.star_gray);
                        } else {
                            iv_one.setBackgroundResource(R.drawable.star);
                            iv_two.setBackgroundResource(R.drawable.star);
                            iv_three.setBackgroundResource(R.drawable.star);
                            iv_four.setBackgroundResource(R.drawable.star);
                            iv_five.setBackgroundResource(R.drawable.star);
                        }
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {


            }
        });

        return rootview;
    }

    @Override
    public void failure(RetrofitError error) {
        Toaster.showToast("Unable to reserve");
        mReservingStatus.setVisibility(View.GONE);
        mReserveButton.setVisibility(View.VISIBLE);
        Log.e(getClass().getSimpleName(), error.getMessage(), error);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    private boolean tableNotBooked() {
        if(Cart.instance().getOrderId() != -1)
            return false;
        else
            return (Cart.instance().getTableNo(getActivity()) == -1);// || Cart.instance().getOrderId() == -1);
    }

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NetworkConfigs.CHANNEL_ID = NetworkConfigs.SOCIAL_WALL;
        PhotoUploadHandler.handleFileUploadResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void success(OrderResponse orderResponse, Response response) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (response.getStatus() == 201) {
            //Toaster.showToast("Order created successfully");
            Cart.instance().setOrderId(orderResponse.getId());
            Log.d(TAG, orderResponse.toString());
            processComplete();
        } else {
            Log.e(TAG, response.getStatus() + response.getReason());
        }
    }

    private void processComplete()
    {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        mReservingStatus.setText(R.string.status_reserved);
        mReserveButton.setVisibility(View.GONE);
        mReservingStatus.setVisibility(View.VISIBLE);
        EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
    }

}
