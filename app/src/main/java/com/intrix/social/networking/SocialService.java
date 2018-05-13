package com.intrix.social.networking;

import com.intrix.social.model.Category;
import com.intrix.social.model.CitiesResponse;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Event;
import com.intrix.social.model.Feedback;
import com.intrix.social.model.Item;
import com.intrix.social.model.OrderData;
import com.intrix.social.model.OrderItem;
import com.intrix.social.model.OrderedItem;
import com.intrix.social.model.OutletResponse;
import com.intrix.social.model.Split;
import com.intrix.social.model.Transaction;
import com.intrix.social.networking.model.AuthRequest;
import com.intrix.social.networking.model.AuthResponse;
import com.intrix.social.networking.model.Chat;
import com.intrix.social.networking.model.ChatRequest;
import com.intrix.social.networking.model.ConnectRequest;
import com.intrix.social.networking.model.ConnectResponse;
import com.intrix.social.networking.model.CustomerRequest;
import com.intrix.social.networking.model.FavoriteRequest;
import com.intrix.social.networking.model.FeedbackRequest;
import com.intrix.social.networking.model.OpenTableRequest;
import com.intrix.social.networking.model.OrderItemRequest;
import com.intrix.social.networking.model.OrderRequest;
import com.intrix.social.networking.model.OrderResponse;
import com.intrix.social.networking.model.SettleRequest;
import com.intrix.social.networking.model.SettlementRequest;
import com.intrix.social.networking.model.SettlementResponse;
import com.intrix.social.networking.model.SignInRequest;
import com.intrix.social.networking.model.SignInResponse;
import com.intrix.social.networking.model.SimpleRequest;
import com.intrix.social.networking.model.SplitPayment;
import com.intrix.social.networking.model.SplitPaymentRequest;
import com.intrix.social.networking.model.SplitRequest;
import com.intrix.social.networking.model.TableRequest;
import com.intrix.social.networking.model.TablesResponse;
import com.intrix.social.networking.model.Tag;
import com.intrix.social.networking.model.TagRequest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import static com.intrix.social.networking.NetworkConfigs.CATEGORIES;
import static com.intrix.social.networking.NetworkConfigs.CHAT;
import static com.intrix.social.networking.NetworkConfigs.CITIES;
import static com.intrix.social.networking.NetworkConfigs.CONNECT;
import static com.intrix.social.networking.NetworkConfigs.CUSTOMERS;
import static com.intrix.social.networking.NetworkConfigs.CUSTOMIZATIONS;
import static com.intrix.social.networking.NetworkConfigs.EVENTS;
import static com.intrix.social.networking.NetworkConfigs.FEEDBACKS;
import static com.intrix.social.networking.NetworkConfigs.ITEMS;
import static com.intrix.social.networking.NetworkConfigs.LEADERBOARD;
import static com.intrix.social.networking.NetworkConfigs.OPEN_TABLES;
import static com.intrix.social.networking.NetworkConfigs.ORDER;
import static com.intrix.social.networking.NetworkConfigs.ORDERITEMS;
import static com.intrix.social.networking.NetworkConfigs.OUTLETS;
import static com.intrix.social.networking.NetworkConfigs.SETTLEMENT;
import static com.intrix.social.networking.NetworkConfigs.SETTLE_ORDER;
import static com.intrix.social.networking.NetworkConfigs.SIGN_IN;
import static com.intrix.social.networking.NetworkConfigs.SIGN_UP;
import static com.intrix.social.networking.NetworkConfigs.SPLITS;
import static com.intrix.social.networking.NetworkConfigs.SPLIT_PAYMENTS;
import static com.intrix.social.networking.NetworkConfigs.TABLES;
import static com.intrix.social.networking.NetworkConfigs.TRANSACTIONS;
import static com.intrix.social.networking.NetworkConfigs.TRANSACTION_DETAILS;

/**
 * Created by yarolegovich on 8/9/15.
 */
public interface SocialService {

  //  @POST(SIGN_UP)
  //  AuthResponse signUp(@Body AuthRequest request, Callback<AuthResponse> call);

    @POST(SIGN_UP)
    void signUp(@Body AuthRequest request, Callback<AuthResponse> callback);

    @POST(SIGN_IN)
    void signIn(@Body SignInRequest request, Callback<SignInResponse> callback);


    @GET(CUSTOMIZATIONS)
    void getCustomizations(Callback<List<Customization>> callback);

    @GET(ITEMS)
    void getItems(Callback<List<Item>> callback);

    @GET(CATEGORIES)
    void getCategories(Callback<List<Category>> callback);

    @GET(EVENTS)
    void getEvents(Callback<List<Event>> callback);

    @GET(ORDER)
    void getOrders(Callback<List<OrderItem>> callback);

    @GET(FEEDBACKS)
    void getFeedback(Callback<List<Feedback>> callback);

    @GET(CONNECT)
    void getConnections(@Query("c_id") int id,Callback<List<Connection>> callback);

    @GET(CUSTOMERS)
    void getCustomers(Callback<List<Customer>> callback);

    @GET(CUSTOMERS)
    void getOtherCustomers(String location, Callback<List<Customer>> callback);

    //https://socialoffline.herokuapp.com/chats?c_id=73
    @GET(CHAT)
    void getChats(Callback<List<Chat>> callback);

    @GET(CHAT)
    void getChats(@Query("c_id") int id,Callback<List<Chat>> callback);

    @GET(ORDER)
    void getOpenOrders(@Query("c_id") int id,Callback<List<OrderData>> callback);

    @POST(ORDER)
    @Headers("Content-Type : application/json")
    void order(@Body OrderRequest request, Callback<OrderResponse> callback);

    @POST(ORDERITEMS)
    @Headers("Content-Type : application/json")
    retrofit.client.Response orderItem(@Body OrderItemRequest request);

    @POST(CUSTOMERS)
    @Headers("Content-Type : application/json")
    void customers(@Body CustomerRequest customerRequest, Callback<Customer> callback);

    @POST(FEEDBACKS)
    @Headers("Content-Type : application/json")
    void feedback(@Body FeedbackRequest feedbackRequest, Callback<Feedback> callback);

    @PATCH("/customers/{id}")
    @Headers("Content-Type : application/json")
    void updateUser(@Body CustomerRequest customerRequest, @Path("id") int id, Callback<Customer> callback);

    ///customers?utf8=✓&query=
    @GET(CUSTOMERS)
    void searchCustomers( @Query("query") String searchWord,Callback<List<CustomerMini>> callback);

    @GET(CUSTOMERS)
    void searchCustomersByInterest( @Query("query") String searchWord,Callback<List<CustomerMini>> callback);

    @POST(CONNECT)
    @Headers("Content-Type : application/json")
    void connect(@Body ConnectRequest connectRequest, Callback<ConnectResponse> callback);

    @PATCH(ITEMS + "/{id}")
    @Headers("Content-Type : application/json")
    void changeFavorite(@Path("id") int itemId, @Body FavoriteRequest request, Callback<Void> callback);

	@PATCH("/connect_requests/{id}")
    @Headers("Content-Type : application/json")
    void connectConfirm(@Body ConnectRequest connectRequest, @Path("id") int id, Callback<ConnectResponse> callback);

    @POST(CHAT)
    @Headers("Content-Type : application/json")
    void chatSetup(@Body ChatRequest chatRequest, Callback<Chat> callback);

    @POST(SPLITS)
    @Headers("Content-Type : application/json")
    void split(@Body SplitRequest splitRequest, Callback<Split> callback);

    @POST(SPLIT_PAYMENTS)
    @Headers("Content-Type : application/json")
    void splitPayment(@Body SplitPaymentRequest splitPaymentRequest, Callback<SplitPayment> callback);

    @GET(TABLES)
    void getFreeTables(Callback<List<TablesResponse>> callback);

    @GET(CITIES)
    void getFilterCities(Callback<List<CitiesResponse>> callback);

    @GET(OUTLETS)
    void getOutletData(Callback<List<OutletResponse>> callback);

    @POST(OPEN_TABLES)
    void openTable(@Body OpenTableRequest request, Callback<Integer> onObtainPosNo);

    @POST(TABLES)
    void allotTableSelf(@Body TableRequest request, Callback<TablesResponse> onObtainPosNo);


    @POST(SETTLE_ORDER)
    void settleOrder(@Body SettleRequest settleRequest, Callback<Void> callback);

    @PATCH("/customers/{id}")
    @Headers("Content-Type : application/json")
    void setGcmId(@Body CustomerRequest customerRequest, @Path("id") int id, Callback<Customer> callback);

    @PATCH("/customers/{id}")
    @Headers("Content-Type : application/json")
    void setMobileNo(@Body CustomerRequest customerRequest, @Path("id") int id, Callback<Customer> callback);

    @POST("/tags")
    @Headers("Content-Type : application/json")
    void tag(@Body TagRequest request, Callback<Tag> callback);

    @GET(CUSTOMERS)
    void searchForTag( @Query("tag_id") String searchWord,Callback<List<CustomerMini>> callback);

    @GET(TABLES)
    void getMyTable( @Query("c_id") int customerId, Callback<List<TablesResponse>> callback);

    @POST(SETTLEMENT)
    void settlement(@Body SettlementRequest settlementRequest, Callback<SettlementResponse> callback);

    @GET(ORDERITEMS)
    void getOrderedItems(@Query("o_id") int id,Callback<List<OrderedItem>> callback);

    @POST("/customers/people_in_social")
    @Headers("Content-Type : application/json")
    void discover(@Body SimpleRequest request, Callback<List<CustomerMini>> callback);


    @POST(LEADERBOARD)
    @Headers("Content-Type : application/json")
    void getLeaderboard(@Body SimpleRequest request, Callback<List<CustomerMini>> callback);

    @GET(TRANSACTIONS)
    void getTransactions(@Query("cid") int id,Callback<List<Transaction>> callback);

    @GET(TRANSACTION_DETAILS)
    void getTransactionDetails(@Query("oid") int id,Callback<List<Transaction>> callback);

}
