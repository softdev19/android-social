package com.intrix.social.networking;

/**
 * Created by yarolegovich on 8/4/15.
 */
public class NetworkConfigs {

    public static final String SOCIAL_API_URL   = "http://socialoffline.herokuapp.com";
    public static final String APPS_API_URL   = "http://api.wheredatapp.com/";
    public static final String PAYMENTS_API_URL   = "https://www.instamojo.com/api/1.1";

    public static final String ACTION_CHANNEL_CREATED = "CHANNEL_CREATED";
    public static final String ACTION_PURPOSE_SET     = "CHANNEL_PURPOSE_SET";
    public static final String ACTION_WALLET_CREATED  = "WALLET_CREATED";

    public static final String SIGN_UP        = "/auth";
    public static final String SIGN_IN        = "/auth/sign_in";

    public static final String CATEGORIES     = "/categories";
    public static final String ITEMS          = "/items";
    public static final String CUSTOMIZATIONS = "/cutomizations";
    public static final String EVENTS         = "/events";

    public static final String ORDER          = "/orders";
    public static final String ORDERITEMS     = "/ordereditems";
    public static final String BILL_GENERATOR = "/billgenerator";
    public static final String SETTLE_ORDER   = ORDER + "/settle_order";

    public static final String TABLES         = "/tables";
    public static final String OPEN_TABLES    = TABLES + "/open_table";

    public static final String SPLITS         = "/splits";
    public static final String SPLIT_PAYMENTS         = "/split_payments";
    public static final String FEEDBACKS      = "/feedbacks";

    public static final String CUSTOMERS      = "/customers";
    public static final String CONNECT        = "/connect_requests";

    public static final String CHAT           = "/chats";

    public static final String OUTLETS      = "/outlets";

    public static final String CITIES      = "/cities";

    public static final String SETTLEMENT   = "/settlements";

    public static final String LEADERBOARD  = "/customers/leaderboard";

    public static final String TRANSACTIONS  = "/transcations/index";

    public static final String TRANSACTION_DETAILS  = "/transcations/items";

    public static final String APPS      = "/data";

    public static final String PAYMENTS      =  "/payment-requests/";

    //http://socialoffline.herokuapp.com/outlets


    //public static final String SEARCH_CUSTOMERS = "/customers?utf8=✓&query=";
    //public static final String SEARCH_CUSTOMERS = "/customers?query=";
}
