package com.intrix.social.networking;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.model.AuthData;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Category;
import com.intrix.social.model.CitiesResponse;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Feedback;
import com.intrix.social.model.Item;
import com.intrix.social.model.Order;
import com.intrix.social.model.OrderData;
import com.intrix.social.model.OrderItem;
import com.intrix.social.model.OrderedItem;
import com.intrix.social.model.OutletResponse;
import com.intrix.social.model.SignInData;
import com.intrix.social.model.Split;
import com.intrix.social.model.Transaction;
import com.intrix.social.model.event.MessageEvent;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.model.App;
import com.intrix.social.networking.model.AppsRequest;
import com.intrix.social.networking.model.AppsResponse;
import com.intrix.social.networking.model.AuthRequest;
import com.intrix.social.networking.model.AuthResponse;
import com.intrix.social.networking.model.Chat;
import com.intrix.social.networking.model.ChatRQ;
import com.intrix.social.networking.model.ChatRequest;
import com.intrix.social.networking.model.ConnectRQ;
import com.intrix.social.networking.model.ConnectRequest;
import com.intrix.social.networking.model.ConnectResponse;
import com.intrix.social.networking.model.CustomerRequest;
import com.intrix.social.networking.model.FavoriteRequest;
import com.intrix.social.networking.model.FeedbackRequest;
import com.intrix.social.networking.model.InstaMojoRequest;
import com.intrix.social.networking.model.OpenTableRequest;
import com.intrix.social.networking.model.OrderItemRequest;
import com.intrix.social.networking.model.OrderRequest;
import com.intrix.social.networking.model.OrderResponse;
import com.intrix.social.networking.model.PaymentResponse;
import com.intrix.social.networking.model.SettleRequest;
import com.intrix.social.networking.model.Settlement;
import com.intrix.social.networking.model.SettlementRequest;
import com.intrix.social.networking.model.SettlementResponse;
import com.intrix.social.networking.model.SignInRequest;
import com.intrix.social.networking.model.SignInResponse;
import com.intrix.social.networking.model.SimpleRequest;
import com.intrix.social.networking.model.SplitPayment;
import com.intrix.social.networking.model.SplitPaymentRequest;
import com.intrix.social.networking.model.SplitRequest;
import com.intrix.social.networking.model.Table;
import com.intrix.social.networking.model.TableRequest;
import com.intrix.social.networking.model.TablesResponse;
import com.intrix.social.networking.model.Tag;
import com.intrix.social.networking.model.TagRequest;
import com.intrix.social.utils.Toaster;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;

import static com.intrix.social.networking.NetworkConfigs.APPS_API_URL;
import static com.intrix.social.networking.NetworkConfigs.PAYMENTS_API_URL;
import static com.intrix.social.networking.NetworkConfigs.SIGN_IN;
import static com.intrix.social.networking.NetworkConfigs.SIGN_UP;
import static com.intrix.social.networking.NetworkConfigs.SOCIAL_API_URL;

/**
 * Created by yarolegovich on 8/4/15.
 */
public class Networker {

    private static final String TAG = Networker.class.getSimpleName();

    private SocialService mSocialService;

    private AppsService mAppsService;

    private PaymentService mPaymentService;

    private Realm mRealm;
    private Dispatcher mDispatcher;
    private ExecutorService mExecutorService;

    private Handler mMainHandler;

    private static Networker sInstance;

    public static Networker getInstance() {
        return sInstance != null ? sInstance : (sInstance = new Networker());
    }

    public static boolean realmUpdating = false;

    private Networker() {

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();


        RestAdapter rest = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(SOCIAL_API_URL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mSocialService = rest.create(SocialService.class);

        RestAdapter rest2 = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(APPS_API_URL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mAppsService = rest2.create(AppsService.class);

        RestAdapter rest3 = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(PAYMENTS_API_URL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mPaymentService = rest3.create(PaymentService.class);


        mDispatcher = new Dispatcher(this);
        //       mRealm = Realm.getDefaultInstance();
        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadExecutor();
    }

//    public void getItems(final Callback<List<Item>> callback) {
//        mSocialService.getItems(new GetCallbackWrapper<>(callback));
//    }
//
//    public void getCustomizations(Callback<List<Customization>> callback) {
//        mSocialService.getCustomizations(new GetCallbackWrapper<>(callback));
//    }

//    public void getCategories(Callback<List<Category>> callback) {
//        mSocialService.getCategories(new GetCallbackWrapper<>(callback));
//    }

    public void getItems(final Callback<List<Item>> callback) {
        mSocialService.getItems(callback);
    }

    public void getCustomizations(Callback<List<Customization>> callback) {
        mSocialService.getCustomizations(callback);
    }

    public void getCategories(Callback<List<Category>> callback) {
        mSocialService.getCategories(callback);
    }

    /*
     * For thread safety dispatching commands using custom runnable class and executor service
     */
    private void execute(final String command, final String... params) {
        mDispatcher.setCommand(command);
        mDispatcher.setParams(params);
        mExecutorService.submit(mDispatcher);
    }

    public void order(Callback<OrderResponse> callback) {
//        request.setCustomerId(MainApplication.data.getCustomerId());
        Cart cart = Cart.instance();
        OrderData data = new OrderData(cart.getPosOrderId(), cart.getTableNo(), MainApplication.data.user.getId());
        mSocialService.order(new OrderRequest(data), callback);
    }

    public void order() {
//        request.setCustomerId(MainApplication.data.getCustomerId());
        Cart cart = Cart.instance();
        OrderData data = new OrderData(cart.getPosOrderId(), cart.getTableNo(), MainApplication.data.user.getId());

        mSocialService.order(new OrderRequest(data), new Callback<OrderResponse>() {
            @Override
            public void success(OrderResponse orderResponse, Response response) {

                if(response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Log.i(TAG, "Order created successfully");
                    Cart.instance().setOrderId(orderResponse.getId());
                    Log.d(TAG, orderResponse.toString());
                    EventBus.getDefault().post(new NetworkEvent("getOrderId", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getOrderId", false));

                Log.d(TAG, "/customers: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Toaster.showToast("Internet problems");
                EventBus.getDefault().post(new NetworkEvent("getOrderId", false));
            }
        });
    }

    public void settle(Context context) {
        mSocialService.settleOrder(new SettleRequest(Cart.instance().getOrderId(), "customer"), new Callback<Void>() {

            @Override
            public void success(Void settlementResponse, Response response) {
                Log.i(TAG, "settleOrder  success " + response.getStatus());
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    EventBus.getDefault().post(new NetworkEvent("settleOrder", true));
                    Cart.instance().releaseTable();
                } else
                    EventBus.getDefault().post(new NetworkEvent("settleOrder", false));
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i(TAG, "settleOrder  failed " + error.toString());
                error.printStackTrace();
//                Log.e(TAG, (String) error.getBody(), error);
//                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("settleOrder", false));
            }
        });


//        mSocialService.settleOrder(new SettleRequest(Cart.instance().getOrderId()),
//                new LoggingCallback<>(aNull -> Cart.instance().releaseTable(context))
//        );
//        EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
    }

    public void openTable(int tableNo, int noOfPeople, Callback<Integer> callback) {
        int customerId = MainApplication.data.user.getId();
        mSocialService.openTable(new OpenTableRequest(tableNo, noOfPeople, customerId), callback);
    }

    public void allotTableSelf(int tableId) {
        int customerId = MainApplication.data.user.getId();
        mSocialService.allotTableSelf(new TableRequest(new Table(tableId, customerId, "requested")), new Callback<TablesResponse>() {
            @Override
            public void success(TablesResponse tablesResponse, Response response) {

                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    MainApplication.data.saveIntData("tableCode", tablesResponse.getTableId());
                    EventBus.getDefault().post(new NetworkEvent("allotTableSelf", true));
                }else
                    EventBus.getDefault().post(new NetworkEvent("allotTableSelf", false));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new NetworkEvent("allotTableSelf", false));
                error.printStackTrace();
            }
        });
    }


    public void getTables(Callback<List<TablesResponse>> callback) {
        mSocialService.getFreeTables(callback);
    }

    public void getMyTable(Callback<List<TablesResponse>> callback) {
        int customerId = MainApplication.data.user.getId();
        mSocialService.getMyTable(customerId, callback);
    }

    public void getCities(Callback<List<CitiesResponse>> callback) {
        mSocialService.getFilterCities(callback);
    }


    public void getOutletData(Callback<List<OutletResponse>> callback) {
        mSocialService.getOutletData(callback);
    }

    public void sendItems(Context context) {
        new Thread(() -> {
            Cart cart = Cart.instance();
            Realm realm = Realm.getInstance(context);
            List<Order> items = new ArrayList(cart.getOrders());
            Log.i(TAG, "sendItems - order size - " + items.size());
            Cart.instance().updateSpecials();
            for (int i = 0; i < items.size(); i++) {
                Order order = items.get(i);
                Item dish = realm.where(Item.class).equalTo("id", order.itemId).findFirst();
                try {

                    if (!order.isOrderConfirmed()) {
                        OrderItem item = new OrderItem(
                                MainApplication.data.getCustomerId(),
                                cart.getOrderId(), order.itemId,
                                order.getAmount()
                        );
                        item.setUom(String.valueOf(dish.getUom()));
                        item.setItemCode(dish.getItemCode());
                        //item.setSpecial(MainApplication.data.loadData("temp.special"));
                        item.setSpecial(order.getSpecial());
                        Response response = mSocialService.orderItem(new OrderItemRequest(item));
                        if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                            if (order.getCustomizations().size() == 0) {
                                Cart.instance().orderConfirmedFor(order.itemId);
                                EventBus.getDefault().post(new NetworkEvent("sentOneItem", true));
                            } else {
                                Cart.instance().orderConfirmedWithoutCustomizationFor(order.itemId);
                                EventBus.getDefault().post(new NetworkEvent("sentOneItemWithoutCustomization", true));
                            }
                        } else {
                            EventBus.getDefault().post(new NetworkEvent("sendItems", false));
                            break;
                        }
                        Log.d(TAG, "Got response after sending " + dish.getName() + ": " + response.getStatus());
                    }

                    List<Integer> customizations = order.getUnconfirmedCustomizations();

                    for (int j = 0; j < customizations.size(); j++) {
                        Integer currentCustomization = customizations.get(j);
                        Customization custm = realm.where(Customization.class).equalTo("id", currentCustomization).findFirst();
                        OrderItem item2 = new OrderItem(
                                MainApplication.data.getCustomerId(),
                                cart.getOrderId(), currentCustomization,
                                order.getCustomizationsUnconfirmedCount(currentCustomization)
                        );
                        item2.setUom(String.valueOf(custm.getUom()));
                        item2.setItemCode(custm.getItemCode());
                        item2.setSpecial(MainApplication.data.loadData("temp.special"));
                        Response response2 = mSocialService.orderItem(new OrderItemRequest(item2));
                        if (response2.getStatus() >= 200 && response2.getStatus() <= 205) {
                            Cart.instance().orderConfirmedCustomizationFor(order.itemId, currentCustomization);
                            EventBus.getDefault().post(new NetworkEvent("sentOneCustomizationItem", true));
                        } else {
                            EventBus.getDefault().post(new NetworkEvent("sendItems", false));
                            break;
                        }
                        Log.d(TAG, "Got response after sending " + custm.getName() + ": " + response2.getStatus());
                    }
                    Cart.instance().orderConfirmedFor(order.itemId);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    Log.d(TAG, "Error when sending: " + dish.getName());
                    EventBus.getDefault().post(new NetworkEvent("sendItems", false));
                }
            }
            realm.close();
            EventBus.getDefault().post(new NetworkEvent("sendItems", true));
        }).start();
    }

    public void getOrders(Callback<List<OrderItem>> orderItemCallback) {
        mSocialService.getOrders(orderItemCallback);
    }

    public void getFeedback(Callback<List<Feedback>> orderItemCallback) {
        mSocialService.getFeedback(orderItemCallback);
    }

    public void getConnections(Callback<List<Connection>> connectionsCallback) {
        int id = MainApplication.data.user.getId();
        mSocialService.getConnections(id, connectionsCallback);
    }

    public void getCustomers(Callback<List<Customer>> customersCallback) {
        mSocialService.getCustomers(customersCallback);
    }

    public void getChatsSync( Callback<List<Chat>> chatsCallback) {
        int id = MainApplication.data.user.getId();
        mSocialService.getChats(id, chatsCallback);
    }

    public void feedback(Feedback feedback) {
        mSocialService.feedback(new FeedbackRequest(feedback), new Callback<Feedback>() {
            @Override
            public void success(Feedback feedbackResponse, Response response) {

                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Log.i(TAG, "Feedback sent");
                    EventBus.getDefault().post(new NetworkEvent("feedback", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("feedback", false));

                Log.d(TAG, "/feedback: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Toaster.showToast("Internet problems");
                EventBus.getDefault().post(new NetworkEvent("feedback", false));
            }
        });
    }


    public void customers(final CustomerMini customer) {
        mSocialService.customers(new CustomerRequest(customer), new Callback<Customer>() {
            @Override
            public void success(Customer customerResponse, Response response) {

                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    saveUserData(customerResponse);
                    Log.i(TAG, "User data received");
                    EventBus.getDefault().post(new NetworkEvent("customers", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("customers", false));

                Log.d(TAG, "/customers: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                Toaster.showToast("Internet problems");
                EventBus.getDefault().post(new NetworkEvent("customers", false));
            }
        });
    }

    public void saveUserData(Customer customerResponse) {
        // id ll be zero on update calls commenting this out
        if(customerResponse.getId() > 0) {
            int id = customerResponse.getId();
            Cart.instance().setCustomerId(id);
            MainApplication.data.saveData("user.customerId", String.valueOf(id));
        }

        if (customerResponse.getInterest1() != null && customerResponse.getInterest1().length() > 0)
            MainApplication.data.saveData("user.love1", customerResponse.getInterest1());
        if (customerResponse.getInterest2() != null && customerResponse.getInterest2().length() > 0)
            MainApplication.data.saveData("user.love2", customerResponse.getInterest2());
        if (customerResponse.getInterest3() != null && customerResponse.getInterest3().length() > 0)
            MainApplication.data.saveData("user.love3", customerResponse.getInterest3());

        if (customerResponse.getFblink() != null && customerResponse.getFblink().length() > 0)
            MainApplication.data.saveData("user.fblink", customerResponse.getFblink());

        if (customerResponse.getTwtlink() != null && customerResponse.getTwtlink().length() > 0)
            MainApplication.data.saveData("user.twtlink", customerResponse.getTwtlink());

        if (customerResponse.getBelink() != null && customerResponse.getBelink().length() > 0)
            MainApplication.data.saveData("user.belink", customerResponse.getBelink());

        if (customerResponse.getDrlink() != null && customerResponse.getDrlink().length() > 0)
            MainApplication.data.saveData("user.drlink", customerResponse.getDrlink());

        if (customerResponse.getSclink() != null && customerResponse.getSclink().length() > 0)
            MainApplication.data.saveData("user.sclink", customerResponse.getSclink());

        if (customerResponse.getLocation() != null && customerResponse.getLocation().length() > 0)
            MainApplication.data.saveData("user.location", customerResponse.getLocation());

        if (customerResponse.getPic() != null && customerResponse.getPic().length() > 0)
            MainApplication.data.saveData("user.pic", customerResponse.getPic());

        if (customerResponse.getName() != null && customerResponse.getName().length() > 0)
            MainApplication.data.saveData("user.name", customerResponse.getName());

        if (customerResponse.getOtherinterest() != null && customerResponse.getOtherinterest().length() > 0)
            MainApplication.data.saveData("user.otherinterest", customerResponse.getOtherinterest());

        if (customerResponse.getOtherlink() != null && customerResponse.getOtherlink().length() > 0)
            MainApplication.data.saveData("user.otherlinks", customerResponse.getOtherlink());

        if (customerResponse.getDescription() != null && customerResponse.getDescription().length() > 0)
            MainApplication.data.saveData("user.description", customerResponse.getDescription());

        if (customerResponse.getDescription2() != null && customerResponse.getDescription2().length() > 0)
            MainApplication.data.saveData("user.description2", customerResponse.getDescription2());

        if (customerResponse.getDescription3() != null && customerResponse.getDescription3().length() > 0)
            MainApplication.data.saveData("user.description3", customerResponse.getDescription3());

        if (customerResponse.getUpvotes() != null && customerResponse.getUpvotes().length() > 0)
            MainApplication.data.saveData("user.upvotes", customerResponse.getUpvotes());

        if (customerResponse.getDownvotes() != null && customerResponse.getDownvotes().length() > 0)
            MainApplication.data.saveData("user.downvotes", customerResponse.getDownvotes());

        if (customerResponse.getMobileno() != null && customerResponse.getMobileno().length() > 0)
            MainApplication.data.saveData("user.mobile", customerResponse.getMobileno());

        MainApplication.data.user = customerResponse;
        Log.i(TAG, " Obtained user data and set for " + MainApplication.data.user.getName());
    }

    //too much to do skipping the properway
    public void saveUserDataMini(CustomerMini customerResponse) {
        // id ll be zero on update calls commenting this out
        if(customerResponse.getId() != null && customerResponse.getId() > 0) {
            int id = customerResponse.getId();
            Cart.instance().setCustomerId(id);
            MainApplication.data.saveData("user.customerId", String.valueOf(id));
        }

        if (customerResponse.getInterest1() != null && customerResponse.getInterest1().length() > 0)
            MainApplication.data.saveData("user.love1", customerResponse.getInterest1());
        if (customerResponse.getInterest2() != null && customerResponse.getInterest2().length() > 0)
            MainApplication.data.saveData("user.love2", customerResponse.getInterest2());
        if (customerResponse.getInterest3() != null && customerResponse.getInterest3().length() > 0)
            MainApplication.data.saveData("user.love3", customerResponse.getInterest3());

        if (customerResponse.getFblink() != null && customerResponse.getFblink().length() > 0)
            MainApplication.data.saveData("user.fblink", customerResponse.getFblink());

        if (customerResponse.getTwtlink() != null && customerResponse.getTwtlink().length() > 0)
            MainApplication.data.saveData("user.twtlink", customerResponse.getTwtlink());

        if (customerResponse.getBelink() != null && customerResponse.getBelink().length() > 0)
            MainApplication.data.saveData("user.belink", customerResponse.getBelink());

        if (customerResponse.getDrlink() != null && customerResponse.getDrlink().length() > 0)
            MainApplication.data.saveData("user.drlink", customerResponse.getDrlink());

        if (customerResponse.getSclink() != null && customerResponse.getSclink().length() > 0)
            MainApplication.data.saveData("user.sclink", customerResponse.getSclink());

        if (customerResponse.getLocation() != null && customerResponse.getLocation().length() > 0)
            MainApplication.data.saveData("user.location", customerResponse.getLocation());

        if (customerResponse.getPic() != null && customerResponse.getPic().length() > 0)
            MainApplication.data.saveData("user.pic", customerResponse.getPic());

        if (customerResponse.getName() != null && customerResponse.getName().length() > 0)
            MainApplication.data.saveData("user.name", customerResponse.getName());

        if (customerResponse.getOtherinterest() != null && customerResponse.getOtherinterest().length() > 0)
            MainApplication.data.saveData("user.otherinterest", customerResponse.getOtherinterest());

        if (customerResponse.getOtherlink() != null && customerResponse.getOtherlink().length() > 0)
            MainApplication.data.saveData("user.otherlinks", customerResponse.getOtherlink());

        if (customerResponse.getDescription() != null && customerResponse.getDescription().length() > 0)
            MainApplication.data.saveData("user.description", customerResponse.getDescription());

        if (customerResponse.getDescription2() != null && customerResponse.getDescription2().length() > 0)
            MainApplication.data.saveData("user.description2", customerResponse.getDescription2());

        if (customerResponse.getDescription3() != null && customerResponse.getDescription3().length() > 0)
            MainApplication.data.saveData("user.description3", customerResponse.getDescription3());

        if (customerResponse.getUpvotes() != null && customerResponse.getUpvotes().length() > 0)
            MainApplication.data.saveData("user.upvotes", customerResponse.getUpvotes());

        if (customerResponse.getDownvotes() != null && customerResponse.getDownvotes().length() > 0)
            MainApplication.data.saveData("user.downvotes", customerResponse.getDownvotes());

        if (customerResponse.getMobileno() != null && customerResponse.getMobileno().length() > 0)
            MainApplication.data.saveData("user.mobile", customerResponse.getMobileno());

        //MainApplication.data.user = customerResponse;
        Log.i(TAG, " Obtained user data and set for " + MainApplication.data.user.getName());
    }

    public void orderItem(OrderItem item) {

    }

    public void signUp(String email, String password, String confirmPassword) {
        if (isOnUi()) {
            execute(SIGN_UP, email, password, confirmPassword);
        } else {

            mSocialService.signUp(new AuthRequest(email, password, confirmPassword), new Callback<AuthResponse>() {
                @Override
                public void success(AuthResponse authResponse, Response response) {
                    // here you do stuff with returned tasks
                    if (authResponse.isSuccess()) {
                        AuthData data = authResponse.getData();
                        String email = data.getEmail();
                        MainApplication.data.saveData("user.uid", email);
                        MainApplication.data.saveData("user.email", email);
                        String username = email.substring(0, email.indexOf("@"));
                        MainApplication.data.saveData("user.name", username);
                        MainApplication.data.saveData("user.id", email);
                        MainApplication.data.saveData("login.status", true);
                        MainApplication.data.saveData("signup.type", "manual");

                        CustomerMini customer = new CustomerMini();
                        customer.setName(username);
                        customer.setEmail(data.getEmail());
                        customers(customer);

                        Log.d(TAG, "success " + data);
                        EventBus.getDefault().post(new MessageEvent("Hello everyone! Signup success"));

                    } else {
                        Log.e(TAG, "failed " + TextUtils.join(", ", authResponse.getErrors()));
                    }
                }

                @Override
                public void failure(RetrofitError e) {
                    // you should handle errors, too
                    Log.e(TAG, e.getMessage() + " asdfasdf   ----- " + e.getResponse() + " --ghg--" + e.getBody());
                    String json = new String(((TypedByteArray) e.getResponse().getBody()).getBytes());
                    String fullMsgs = "";
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONObject data = obj.getJSONObject("errors");
                        JSONArray fullMsgArray = data.getJSONArray("full_messages");
                        for (int i = 0; i < fullMsgArray.length(); i++) {
                            fullMsgs = fullMsgs + fullMsgArray.getString(i) + " -/- \n ";
                        }
                        EventBus.getDefault().post(new MessageEvent(fullMsgs));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Log.e(TAG, json);
                }

            });
        }
    }

    public void favorite(Item item) {
        mSocialService.changeFavorite(item.getId(),
                new FavoriteRequest(item.getFav()),
                new LoggingCallback<>()
        );
    }

    public void split(Split split) {
        mSocialService.split(new SplitRequest(split), new Callback<Split>() {

            @Override
            public void success(Split splitResponse, Response response) {

                Log.i(TAG, "splitResponse  success " + response.getStatus());
                if (response.getStatus() >= 201 && response.getStatus() <= 205) {
                    int splitId = splitResponse.getId();
                    Log.i(TAG, "Split id - " + splitId);
                    Cart.instance().setSplitId(splitId);
                    EventBus.getDefault().post(new NetworkEvent("splitCall", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("splitCall", false));
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i(TAG, "splitCall  failed " + error.toString());
                error.printStackTrace();
//                Log.e(TAG, (String) error.getBody(), error);
//                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("splitCall", false));
            }
        });
    }

    public void splitPayments(SplitPayment splitPayment)
    {
        mSocialService.splitPayment(new SplitPaymentRequest(splitPayment), new Callback<SplitPayment>() {
            @Override
            public void success(SplitPayment splitPaymentResponse, Response response) {
                Log.i(TAG, "splitPayment success " + response.getStatus());
                if (response.getStatus() >= 201 && response.getStatus() <= 205) {
                    int splitId = splitPaymentResponse.getId();
                    Log.i(TAG, "Split id - " + splitId);
                    //Cart.instance().setSplitId(splitId);
                    if (splitPaymentResponse.getCustomerId() == MainApplication.data.user.getId())
                        Cart.instance().saveSentMySplit(true);
                    else
                        Cart.instance().addSplitPayment(splitPaymentResponse);
                    EventBus.getDefault().post(new NetworkEvent("splitPaymentCall", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("splitPaymentCall", false));
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i(TAG, "splitCall  failed " + error.toString());
                error.printStackTrace();
//                Log.e(TAG, (String) error.getBody(), error);
//                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("splitPaymentCall", false));
            }
        });
    }

    public void signIn(String email, String password) {
        Log.i(TAG, "signIn calling 12");
        if (isOnUi()) {
            Log.i(TAG, "signIn calling ui");
            execute(SIGN_IN, email, password);
        } else {
            // SignInResponse response = mSocialService.signIn(new SignInRequest(email, password));
            Log.i(TAG, "signIn calling ");

            mSocialService.signIn(new SignInRequest(email, password), new Callback<SignInResponse>() {
                @Override
                public void success(SignInResponse signInResponse, Response response) {
                    // here you do stuff with returned tasks
                    SignInData data = signInResponse.getData();
                    MainApplication.data.saveData("user.uid", data.getUid());
                    MainApplication.data.saveData("user.email", data.getEmail());
                    MainApplication.data.saveData("user.id", data.getEmail());
                    String username = email.substring(0, email.indexOf("@"));
                    MainApplication.data.saveData("user.name", username);
                    MainApplication.data.saveData("login.status", true);
                    MainApplication.data.saveData("signup.type", "manual");

                    CustomerMini customer = new CustomerMini();
                    customer.setEmail(data.getEmail());

                    customers(customer);

                    Log.d(TAG, "success " + data);
                    EventBus.getDefault().post(new MessageEvent("Hello everyone! SignIn success"));

                    {
                        //   Log.e(TAG, "failed " + TextUtils.join(", ", signInResponse.getErrors()));
                    }
                }

                @Override
                public void failure(RetrofitError e) {
                    // you should handle errors, too
//                    Log.e(TAG, e.getMessage() + " asdfasdf   ----- " + e.getResponse() + " --ghg--" + e.getBody());
                    String json = new String(((TypedByteArray) e.getResponse().getBody()).getBytes());
                    String fullMsgs = "";
                    try {
                        JSONObject obj = new JSONObject(json);
                        //JSONObject data = obj.getJSONObject("errors");
                        JSONArray fullMsgArray = obj.getJSONArray("errors");
                        for (int i = 0; i < fullMsgArray.length(); i++) {
                            fullMsgs = fullMsgs + fullMsgArray.getString(i) + " -/- \n ";
                        }
                        EventBus.getDefault().post(new MessageEvent(fullMsgs));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Log.e(TAG, json);
                }

            });
        }
    }

    public void updateUser(final CustomerRequest customerRequest, int id) {
        mSocialService.updateUser(customerRequest, id, new Callback<Customer>() {

            @Override
            public void success(Customer customerResponse, Response response) {
//              int id = customerResponse.getId();

                Log.d(TAG, "/updateUser: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 201 && response.getStatus() <= 205) {
                    saveUserDataMini(customerRequest.getCustomer());
                    MainApplication.data.refreshUser();
                    //Toaster.showToast("User data updated");
                    EventBus.getDefault().post(new NetworkEvent("updateUser", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("updateUser", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //Log.e(TAG, (String) error.getBody(), error);
                //Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("updateUser", false));
            }
        });
    }

    public void searchCustomers(final String searchWord) {
        mSocialService.searchCustomers(searchWord, new Callback<List<CustomerMini>>() {
            @Override
            public void success(List<CustomerMini> searchResult, Response response) {
                Log.d(TAG, "searchCustomers " + response.getStatus());
                Log.d(TAG, "searchCustomers " + searchResult);

                MainApplication.data.searchResult = searchResult;

                int customerId = Integer.parseInt(MainApplication.data.getCustomerId());
                for (int i = 0; i < searchResult.size(); i++) {
                    if (searchResult.get(i).getId() == customerId)
                        MainApplication.data.searchResult.remove(i);
                }

                //for (int i = 0; i < searchResult.size(); i++)
                //  Log.i(TAG, "people " + i + "  - " + searchResult.get(i).getName());
                EventBus.getDefault().post(new NetworkEvent("searchCustomers", true));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new NetworkEvent("searchCustomers", false));
                Log.e(TAG, "searchCustomers - error " + error.getResponse());

                Toaster.showToast("Internet problems");
            }
        });
    }

    public void searchCustomersByInterest(final String searchWord) {
        mSocialService.searchCustomersByInterest(searchWord, new Callback<List<CustomerMini>>() {
            @Override
            public void success(List<CustomerMini> searchResult, Response response) {
                Log.d(TAG, "searchCustomers " + response.getStatus());
                Log.d(TAG, "searchCustomers " + searchResult);

                MainApplication.data.searchResult = searchResult;

                int customerId = Integer.parseInt(MainApplication.data.getCustomerId());
                for (int i = 0; i < searchResult.size(); i++) {
                    if (searchResult.get(i).getId() == customerId)
                        MainApplication.data.searchResult.remove(i);
                }

                //for (int i = 0; i < searchResult.size(); i++)
                //  Log.i(TAG, "people " + i + "  - " + searchResult.get(i).getName());
                EventBus.getDefault().post(new NetworkEvent("searchCustomers", true));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new NetworkEvent("searchCustomers", false));
                Log.e(TAG, "searchCustomers - error " + error.getResponse());

                Toaster.showToast("Internet problems");
            }
        });
    }

    public void connect(final ConnectRQ connectRQ) {
        mSocialService.connect(new ConnectRequest(connectRQ), new Callback<ConnectResponse>() {
            @Override
            public void success(ConnectResponse connectResponse, Response response) {
                int id = connectResponse.getId();

                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Connection connection = new Connection();

                    connection.setPoi_id(connectResponse.getPoi_id());
                    connection.setLocation(connectResponse.getLocation());
                    connection.setCustomer_id(connectResponse.getCustomer_id());
                    connection.setChat_id(connectResponse.getChat_id());
                    connection.setId(connectResponse.getId());
                    connection.setStatus(connectResponse.getStatus());
                    connection.setCreated_at(connectResponse.getCreated_at());
                    connection.setUpdated_at(connectResponse.getUpdated_at());

                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(connection);
                    mRealm.commitTransaction();
                    mRealm.close();
                    Log.d(TAG, "/customers: " + response.getStatus());

                    //Toaster.showToast("User data updated");
                    EventBus.getDefault().post(new NetworkEvent("connect", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("connect", false));

            }

            @Override
            public void failure(RetrofitError e) {
                Toaster.showToast("Internet problems");
                e.printStackTrace();
                Log.e(TAG, e.getMessage() + " asdfasdf   ----- " + e.getResponse().getReason() + " --  " + e.getBody() + " --ghg--" + e.getBody());

                EventBus.getDefault().post(new NetworkEvent("connect", false));
            }
        });
    }

    public void connectConfirm(final ConnectRQ connectRQ, final int id) {
        mSocialService.connectConfirm(new ConnectRequest(connectRQ), id, new Callback<ConnectResponse>() {

            @Override
            public void success(ConnectResponse connectResponse, Response response) {

                Log.d(TAG, "/connectConfirm: " + response.getStatus());
                Connection connection = new Connection();
//                connection.setId(connectResponse.getId());
//                connection.setPoi_id(connectResponse.getPoi_id());
//                connection.setLocation(connectResponse.getLocation());
//                connection.setCustomer_id(connectResponse.getCustomer_id());
//                connection.setChat_id(connectResponse.getChat_id());
//                connection.setStatus(connectResponse.getStatus());
//                connection.setCreated_at(connectResponse.getCreated_at());
//                connection.setUpdated_at(connectResponse.getUpdated_at());

                mRealm = Realm.getDefaultInstance();
                connection = mRealm.where(Connection.class).equalTo("id", id).findFirst();

                mRealm.beginTransaction();
                //mRealm.copyToRealmOrUpdate(connection);
                connection.setStatus("connected");
                mRealm.commitTransaction();
                mRealm.close();

                Log.d(TAG, "/connectConfirm: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.showToast("Connect Confirmed");
                    EventBus.getDefault().post(new NetworkEvent("connectConfirm", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("connectConfirm", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("connectConfirm", false));
            }
        });
    }


    public void chatSetup(final ChatRQ chatRQ) {
        mSocialService.chatSetup(new ChatRequest(chatRQ), new Callback<Chat>() {

            @Override
            public void success(Chat chat, Response response) {

                Log.d(TAG, "/chatSetup: " + response.getStatus());

                //chat
                mRealm = Realm.getDefaultInstance();
                //   connection = mRealm.where(Connection.class).equalTo("id", id).findFirst();

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(chat);
                mRealm.commitTransaction();
                mRealm.close();

                MainApplication.data.newChat = chat;

                Log.d(TAG, "/chatSetup: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.showToast("Connect Confirmed");
                    EventBus.getDefault().post(new NetworkEvent("chatSetup", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("chatSetup", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("connectConfirm", false));
            }
        });
    }


    public void getOtherCustomers(String location, Callback<List<Customer>> customersCallback) {
        mSocialService.getOtherCustomers(location, new Callback<List<Customer>>() {

            @Override
            public void success(List<Customer> others, Response response) {

                Log.d(TAG, "/getOtherCustomers: " + response.getStatus());

                mRealm = Realm.getDefaultInstance();
                //   connection = mRealm.where(Connection.class).equalTo("id", id).findFirst();

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(others);
                mRealm.commitTransaction();
                mRealm.close();

                MainApplication.data.otherCustomers = others;

                Log.d(TAG, "/getOtherCustomers: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.showToast("got Others");
                    EventBus.getDefault().post(new NetworkEvent("getOtherCustomers", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getOtherCustomers", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("getOtherCustomers", false));
            }
        });
    }

    public void getChats(int customerId) {

        int wtf = customerId;

        mSocialService.getChats(customerId, new Callback<List<Chat>>() {

            @Override
            public void success(List<Chat> chats, Response response) {
                Log.d(TAG, "/getChats: " + response.getStatus());

                /* 2016-01-16 Addded for debugging
                Log.i(TAG, "Start Of mSocialService.success(...) in Networker.getChats --- customerId = " + wtf);
                StringBuilder tempStrBldr = new StringBuilder();
                tempStrBldr.append("[");
                for ( Chat chat : chats )
                    tempStrBldr.append("{P1:" + chat.getPerson_1_id() + ",P2:" + chat.getPerson_2_id() + "}," );
                tempStrBldr.append("]");
                Log.i(TAG, "Allll Chats in Networker.getChats ----" + tempStrBldr);
                */

                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(chats);
                mRealm.commitTransaction();
                mRealm.close();

//                MainApplication.data.otherCustomers = others;

                Log.d(TAG, "/getChats: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.showToast("got chats");
                    EventBus.getDefault().post(new NetworkEvent("getChats", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getChats", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("getChats", false));
            }
        });
    }


    public void getApps(List<String> packages) {
        mAppsService.getApps(new AppsRequest(packages), new Callback<AppsResponse>() {
            @Override
            public void success(AppsResponse appsResponse, Response response) {
                Log.d(TAG, "/getChats: " + response.getStatus());


//                mRealm = Realm.getDefaultInstance();
//                mRealm.beginTransaction();
//                mRealm.copyToRealmOrUpdate(chats);
//                mRealm.commitTransaction();
//                mRealm.close();

                List<App> socialApps = new ArrayList<App>();
                for (App app : appsResponse.getApps()) {

                    if (app.getCategory().equalsIgnoreCase("Social") && app.getTitle() != null) {
                        app.setIcon_url("http:" + app.getIcon_url());
                        socialApps.add(app);
                        Log.i(TAG, " social app - " + app.getTitle() + " - " + app.getPack());
                    }
                }

                MainApplication.data.socialApps = socialApps;

                Log.d(TAG, "/getApps: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.showToast("got Apps");
                    EventBus.getDefault().post(new NetworkEvent("getApps", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getApps", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("getApps", false));
            }
        });
    }

    private boolean isOnUi() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public void getCustomersAuto() {
        getCustomers(new RealmSyncCallback<Customer>("getCustomers"));
    }

    public void getConnectionsAuto() {
        getConnections(new RealmSyncCallback<Connection>("getConnections"));
    }

    public void getChatsAuto() {
        getChatsSync(new RealmSyncCallback<Chat>("getChats"));
    }

    public void getCategoriesAuto() {
        getCategories(new RealmSyncCallback<Category>("getCategories"));
    }

    public void getItemsAuto() {
        getItems(new RealmSyncCallback<Item>("getItems"));
    }

    public void getCustomizationsAuto() {
        getCustomizations(new RealmSyncCallback<Customization>("getCustomizations"));
    }

    static class RealmSyncCallback<T extends RealmObject> implements Callback<List<T>> {

        private static final String LOG_TAG = RealmSyncCallback.class.getSimpleName();

        private Realm mRealm;

        private String callName = "";

        RealmSyncCallback() {
            mRealm = Realm.getDefaultInstance();
        }

        RealmSyncCallback(String networkCall) {
            mRealm = Realm.getDefaultInstance();
            callName = networkCall;
        }


        @Override
        public void success(List<T> realmObjects, Response response) {
            Log.d(LOG_TAG, "Got response from server: " + response.getStatus());
            Log.i(LOG_TAG, realmObjects.toString());

//            if(realmObjects.get(0).getClass() == Customer.class)
//            {
//                for(Customer customer : (List<Customer>)realmObjects)
//                {
//                    Log.i(LOG_TAG, " ---+++---  " + customer.getId() + "  " + customer.getEmail());
//                }
//            }

            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmObjects);
            mRealm.commitTransaction();
            mRealm.close();

            if (callName.length() > 0)
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    EventBus.getDefault().post(new NetworkEvent(callName, true));
                } else
                    EventBus.getDefault().post(new NetworkEvent(callName, false));


        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(LOG_TAG, "Error when syncing with server: " + error.getMessage(), error);
            if (callName.length() > 0)
                EventBus.getDefault().post(new NetworkEvent(callName, false));
        }
    }

    public void getOpenOrders(int customerId) {
        mSocialService.getOpenOrders(customerId, new Callback<List<OrderData>>() {
            @Override
            public void success(List<OrderData> orderItems, Response response) {


                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    if (orderItems.size() > 0)
                        MainApplication.data.openOrders = orderItems;

                    EventBus.getDefault().post(new NetworkEvent("getOpenOrders", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getOpenOrders", false));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new NetworkEvent("getOpenOrders", false));
            }
        });
    }

    public void setGcmId(String token) {
        if (MainApplication.data.user == null)
            Log.e(TAG, "User data not initialized - setGcmId");
        CustomerMini customer = new CustomerMini();
        customer.setGcmToken(token);

        int myId = MainApplication.data.user.getId();
        mSocialService.setGcmId(new CustomerRequest(customer), myId, new Callback<Customer>() {

            @Override
            public void success(Customer customerResponse, Response response) {

                Log.d(TAG, "setGcmId " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    //Toaster.showToast("GCM registered");
                    Log.i(TAG, "GCM registered");
                    EventBus.getDefault().post(new NetworkEvent("setGcmId", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("setGcmId", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e(TAG, "setGcmId failed");
                EventBus.getDefault().post(new NetworkEvent("setGcmId", false));
            }
        });
    }

    public void tag(Tag tag) {
        mSocialService.tag(new TagRequest(tag), new Callback<Tag>() {

            @Override
            public void success(Tag tagResponse, Response response) {

                Log.d(TAG, "/tag: " + response.getStatus());

                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    //Toaster.showToast("Tag Confirmed");
                    //MainApplication.data.saveIntData("");
                    Cart.instance().addTag(MainApplication.data.tagResult.get(0));
                    EventBus.getDefault().post(new NetworkEvent("taggingUser", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("taggingUser", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("taggingUser", false));
            }
        });
    }


    public void searchForTag(final String searchWord) {
        mSocialService.searchForTag(searchWord, new Callback<List<CustomerMini>>() {
            @Override
            public void success(List<CustomerMini> searchResult, Response response) {
                Log.d(TAG, "searchForTag " + response.getStatus());
                Log.d(TAG, "searchForTag " + searchResult);

                MainApplication.data.tagResult = searchResult;

                int customerId = Integer.parseInt(MainApplication.data.getCustomerId());
                for (int i = 0; i < searchResult.size(); i++) {
                    if (searchResult.get(i).getId() == customerId)
                        MainApplication.data.tagResult.remove(i);
                }


                //for (int i = 0; i < searchResult.size(); i++)
                //  Log.i(TAG, "people " + i + "  - " + searchResult.get(i).getName());
                EventBus.getDefault().post(new NetworkEvent("searchForTag", true));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().post(new NetworkEvent("searchForTag", false));
                Log.e(TAG, "searchForTag - error " + error.getResponse());

                Toaster.showToast("Internet problems");
            }
        });
    }

    public void settlement(String mode, String amount) {

        Settlement settlement = new Settlement();
        settlement.setOrderId(Cart.instance().getOrderId());
        settlement.setCustomerId(MainApplication.data.user.getId());
        settlement.setTableId(Cart.instance().getTableNo());
        settlement.setAmountSettled(amount); // dummy now need to set
        settlement.setPaymentMode(mode);
        settlement.setSettledBy("customer");

        mSocialService.settlement(new SettlementRequest(settlement), new Callback<SettlementResponse>() {

            @Override
            public void success(SettlementResponse settlementResponse, Response response) {

                Log.i(TAG, "settlement  success " + response.getStatus());
                if (response.getStatus() >= 201 && response.getStatus() <= 205) {
                    Cart.instance().setSettlementPendingAmount(Cart.instance().getSettlementPendingAmount() - Integer.parseInt(amount));
                    Cart.instance().saveSettledAmount(settlementResponse.getAmountSettled());
                    EventBus.getDefault().post(new NetworkEvent("settlement", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("settlement", false));
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.i(TAG, "settlement  failed " + error.toString());
                error.printStackTrace();
//                Log.e(TAG, (String) error.getBody(), error);
//                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("settlement", false));
            }
        });
    }

    public void getOrderedItems(boolean justMine) {
        int orderId = Cart.instance().getOrderId();
        mSocialService.getOrderedItems(orderId, new Callback<List<OrderedItem>>() {
            @Override
            public void success(List<OrderedItem> orderedItems, Response response) {

                if (response.getStatus() >= 200 && response.getStatus() <= 205) {

                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.clear(OrderedItem.class);
                    mRealm.copyToRealmOrUpdate(orderedItems);
                    mRealm.commitTransaction();
                    mRealm.close();


                    if (justMine)
                        EventBus.getDefault().post(new NetworkEvent("getMyOrderedItems", true));
                    else
                        EventBus.getDefault().post(new NetworkEvent("getOrderedItems", true));
                } else if (justMine)
                    EventBus.getDefault().post(new NetworkEvent("getMyOrderedItems", false));
                else
                    EventBus.getDefault().post(new NetworkEvent("getOrderedItems", false));
            }

            @Override
            public void failure(RetrofitError error) {
                if (justMine)
                    EventBus.getDefault().post(new NetworkEvent("getMyOrderedItems", false));
                else
                    EventBus.getDefault().post(new NetworkEvent("getOrderedItems", false));
            }
        });
    }

    public void processOnlinePayment(int amount) {
        InstaMojoRequest request = new InstaMojoRequest();
        request.setAmount(amount);
        request.setPurpose("Church Street Social F&B Bill");

        Data data = MainApplication.data;
        request.setBuyerName(data.getUserName());
        request.setEmail(data.getEmail());
        request.setSendEmail(true);
        if(data.getMobile().length() > 0) {
            request.setPhone(data.getMobile());
            request.setSendSms(true);
        }
        request.setRepeatedPayments(false);
        request.setRedirectUrl("http://www.socialoffline.in/");
        request.setWebhook("http://socialoffline.herokuapp.com/payment_requests");
        //https://socialoffline.herokuapp.com/backups

        mPaymentService.processOnlinePayment(request, new Callback<PaymentResponse>() {
            @Override
            public void success(PaymentResponse instaResponse, Response response) {
                Log.d(TAG, "/processOnlinePayment: " + response.getStatus());

                Log.i(TAG, "processOnlinePayment instaResponse " + instaResponse.toString());


                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    Toaster.toast("processOnlinePayment");
                    //Cart.instance().setPaymentUrl();

                    boolean success = instaResponse.isSuccess();
                    if (success) {
                        InstaMojoRequest instaRequest = instaResponse.getPaymentRequest();
                        Cart.instance().setPaymentUrl(instaRequest.getLongurl());
                        EventBus.getDefault().post(new NetworkEvent("processOnlinePayment", true));
                    } else {
                        EventBus.getDefault().post(new NetworkEvent("processOnlinePayment", false));
                    }
                } else
                    EventBus.getDefault().post(new NetworkEvent("processOnlinePayment", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //            Log.e(TAG, (String) error.getBody(), error);
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("processOnlinePayment", false));
            }
        });
    }

    public void setMobileNo(String mobileNo) {
        if (MainApplication.data.user == null)
            Log.e(TAG, "User data not initialized - setMobileNo");

        CustomerMini customer = new CustomerMini();
        customer.setMobileno(mobileNo);

        int myId = MainApplication.data.user.getId();
        mSocialService.setMobileNo(new CustomerRequest(customer), myId, new Callback<Customer>() {

            @Override
            public void success(Customer customerResponse, Response response) {
                Log.d(TAG, "setMobileNo " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    //saveUserData(customerResponse);
                    //MainApplication.data.refreshUser();
                    MainApplication.data.saveData("user.mobile", mobileNo);
                    MainApplication.data.refreshUser();
                    Log.i(TAG, "setMobileNo set");
                    EventBus.getDefault().post(new NetworkEvent("setMobileNo", true));
                } else {
                    Log.i(TAG, "setMobileNo failed");
                    EventBus.getDefault().post(new NetworkEvent("setMobileNo", false));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e(TAG, "setMobileNo failed");
                EventBus.getDefault().post(new NetworkEvent("setMobileNo", false));
            }
        });
    }


    public void discover() {
        int id = MainApplication.data.user.getId();
        mSocialService.discover(new SimpleRequest(id), new Callback<List<CustomerMini>>() {
            @Override
            public void success(List<CustomerMini> discoverResponse, Response response) {
                Log.i(TAG, "discover  success " + response.getStatus());
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    MainApplication.data.searchResult = discoverResponse;

                    int customerId = MainApplication.data.user.getId();
                    for (int i = 0; i < discoverResponse.size(); i++) {
                        if (discoverResponse.get(i).getId() == customerId)
                            MainApplication.data.searchResult.remove(i);
                    }

                    EventBus.getDefault().post(new NetworkEvent("searchCustomers", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("searchCustomers", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                EventBus.getDefault().post(new NetworkEvent("searchCustomers", false));
            }
        });

//        mSocialService.settleOrder(new SettleRequest(Cart.instance().getOrderId()),
//                new LoggingCallback<>(aNull -> Cart.instance().releaseTable(context))
//        );
//        EventBus.getDefault().post(new ChangePageRequest(AppMenu.HOME));
    }

    public void updateUserLocation(final CustomerRequest customerRequest, int id) {
        mSocialService.updateUser(customerRequest, id, new Callback<Customer>() {

            @Override
            public void success(Customer customerResponse, Response response) {
//              int id = customerResponse.getId();

                Log.d(TAG, "/updateUser: " + response.getStatus());
                Log.d(TAG, String.valueOf(response.getStatus()));
                if (response.getStatus() >= 201 && response.getStatus() <= 205) {
                    saveUserDataMini(customerRequest.getCustomer());
                    MainApplication.data.refreshUser();
                    //Toaster.showToast("User data updated");
                    EventBus.getDefault().post(new NetworkEvent("updateUserLocation", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("updateUserLocation", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                //Log.e(TAG, (String) error.getBody(), error);
                //Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("updateUserLocation", false));
            }
        });
    }

    public void setUserLocation(double latitude, double longitude)
    {
        CustomerMini customer = new CustomerMini();
        customer.setLatitude(latitude);
        customer.setLongitude(longitude);
        CustomerRequest customerRequest = new CustomerRequest(customer);
        updateUserLocation(customerRequest, MainApplication.data.user.getId());
        MainApplication.data.saveLongData("user.gps_time", System.currentTimeMillis());
    }

    public void getLeaderboard() {
        int id = MainApplication.data.user.getId();
        mSocialService.getLeaderboard(new SimpleRequest(id), new Callback<List<CustomerMini>>() {
            @Override
            public void success(List<CustomerMini> callResponse, Response response) {
                Log.i(TAG, "getLeaderboard  success " + response.getStatus());
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    MainApplication.data.rankResult = callResponse;
                    EventBus.getDefault().post(new NetworkEvent("getLeaderboard", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getLeaderboard", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                EventBus.getDefault().post(new NetworkEvent("getLeaderboard", false));
            }
        });

    }


    //http://socialoffline.herokuapp.com/transcations/index?cid=1


    public void getTransactions() {
        int customerId = MainApplication.data.user.getId();
        mSocialService.getTransactions(customerId, new Callback<List<Transaction>>() {
            @Override
            public void success(List<Transaction> transactions, Response response) {
                Log.d(TAG, "/getTransactions: " + response.getStatus());

                MainApplication.data.transactions = transactions;

                Log.d(TAG, "/getTransactions: " + response.getStatus());
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(transactions);
                    mRealm.commitTransaction();
                    mRealm.close();
                    EventBus.getDefault().post(new NetworkEvent("getTransactions", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getTransactions", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("getTransactions", false));
            }
        });
    }

//    http://socialoffline.herokuapp.com/transcations/items?oid=3

    public void getTransactionsDetails() {
        int customerId = MainApplication.data.user.getId();

        mSocialService.getTransactions(customerId, new Callback<List<Transaction>>() {
            @Override
            public void success(List<Transaction> transactions, Response response) {
                Log.d(TAG, "/getTransactions: " + response.getStatus());

                MainApplication.data.transactions = transactions;

                Log.d(TAG, "/getTransactions: " + response.getStatus());
                if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(transactions);
                    mRealm.commitTransaction();
                    mRealm.close();
                    EventBus.getDefault().post(new NetworkEvent("getTransactions", true));
                } else
                    EventBus.getDefault().post(new NetworkEvent("getTransactions", false));
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.e(TAG, error.getMessage(), error);
                EventBus.getDefault().post(new NetworkEvent("getTransactions", false));
            }
        });
    }
}

