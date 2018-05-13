package com.intrix.social.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intrix.social.MainApplication;
import com.intrix.social.networking.model.SplitPayment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

/**
 * Created by yarolegovich on 20.12.2015.
 */
public class Cart {

    private static final String LOG_TAG = Cart.class.getSimpleName();

    private static final String LAST_ORDER = "last_order";
    private static final String CURRENT_ORDER = "current_order";
    private static final String CURRENT_ORDER_CONFIRMED = "current_order_confirmed";
    private static final String TABLE_NO = "cart.table";
    private static final String TABLE_NO_VISIBLE = "cart.table_visible";
    private static final String ORDER_ID = "cart.orderid";
    private static final String POS_ID = "cart.posid";
    private static final String USER_TAGS = "cart.tags";
    private static final String SETTLEMENT_TYPE = "cart.settlement_type";
    private static final String SETTLEMENT_PENDING_AMOUNT = "cart.settlement_pending_amount";
    private static final String SPLIT_ID = "cart.split_id";
    private static final String SPLIT_PAYMENTS = "cart.split_payments";
    private String PAYMENT_URL = "";
    private static final String SPLIT_AMOUNT = "cart.split_amount";
    private static final String SPLIT_SENT_MINE = "cart.split_mine_sent";

    private static Cart sInstance = new Cart();

    public static Cart instance() {
        if(sInstance == null)
        {
            sInstance = new Cart();
            sInstance.restoreCurrentOrder();
            sInstance.restoreCurrentOrderConfirmed();
            sInstance.restoreTags();
            sInstance.restoreCartData();
        }
        return sInstance;
    }

    private int customerId;
    private int orderId = -1;

    private int posOrderId = -1;
    private int tableNo = -1;

    private boolean isSplit = false;
    private int splitAmount = 0;
    private int mSplitAmount = 0;
    private String settlementType = "";
    private int settlementPendingAmount = 0;

    private List<Order> mOrders = new ArrayList<>();
    private List<Order> mConfirmed = new ArrayList<>();
    private List<Order> mPreviousOrder = new ArrayList<>();
    private List<Order> mFullOrders = new ArrayList<>();
    private List<Order> mMyOrders = new ArrayList<>();
    private List<CustomerMini> mTags = new ArrayList<>();
    private String mSettledAmount = "";
    private int splitId = 0;
    private List<SplitPayment> mSplitPayments = new ArrayList<>();
    private boolean sentMySplit = false;

    public void putItem(int itemId) {
        Order order = new Order(itemId);
        if (!mOrders.contains(order)) {
            mOrders.add(new Order(itemId));
        } else {
            mOrders.get(mOrders.indexOf(order)).incrementAmount();
        }
        saveOrders();
        Log.i(LOG_TAG, "Order size after put - "+mOrders.size());
    }

    public void setSpecial(int itemId, String special) {
        for(Order ord : mOrders) {
         if(ord.getItemId()== itemId) {
             ord.setSpecial(special);
         }
        }
        saveOrders();
        Log.i(LOG_TAG, "Order size after put - "+mOrders.size());
    }

    public void clearSpecial(int itemId) {
        for(Order ord : mOrders) {
            if(ord.getItemId()== itemId) {
                ord.setSpecial("");
            }
        }
        saveOrders();
        Log.i(LOG_TAG, "Order size after put - "+mOrders.size());
    }


    public String getSpecial(int itemId) {
        for(Order ord : mOrders) {
            if(ord.getItemId()== itemId && (ord.getSpecial() != null  &&  ord.getSpecial().length() > 0)) {
                return ord.getSpecial();
            }
        }
        return "";
    }

    public void updateSpecials() {
        for(Order ord : mOrders) {
            if(ord.getSpecial() != null && ord.getSpecial().length() > 0)
            for(Order ord2 : mOrders) {
                if(ord2.getItemId() == ord.getItemId())
                    ord2.setSpecial(ord.getSpecial());
            }
        }
        saveOrders();
        Log.i(LOG_TAG, "Order size after put - "+mOrders.size());
    }

    public void removeItem(int itemId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            Order presentOrder = mOrders.get(indexOf);
            presentOrder.decrementAmount();
            if (presentOrder.getAmount() == 0) {
                mOrders.remove(presentOrder);
            }
            saveOrders();
        }
    }

    public void saveOrders()
    {
        MainApplication.data.saveData(CURRENT_ORDER,  new Gson().toJson(mOrders));
    }

    public void saveTags()
    {
        MainApplication.data.saveData(USER_TAGS, new Gson().toJson(mTags));
    }

    public void saveOrdersConfirmed()
    {
        MainApplication.data.saveData(CURRENT_ORDER_CONFIRMED, new Gson().toJson(mConfirmed));
    }

    public int getAmountForNotConfirmed(int itemId) {
        return getAmountFor(itemId, mOrders);
    }

    public int getAmountForConfirmed(int itemId) {
        return getAmountFor(itemId, mConfirmed);
    }

    public int getAmountForFullOrders(int itemId) {
        return getAmountFor(itemId, mFullOrders);
    }

    public int getAmountForMyOrders(int itemId) {
        return getAmountFor(itemId, mMyOrders);
    }

    public List<Item> getItemsNotConfirmed(Realm realm) {
        return getItems(realm, mOrders);
    }

    public List<Item> getItemsConfirmed(Realm realm) {
        return getItems(realm, mConfirmed);
    }

    public int itemsInCartNotConfirmed() {
        if(mOrders == null) {
            restoreCurrentOrder();
            if(mOrders == null)
                mOrders = new ArrayList<>();
        }
        return itemsInCart(mOrders);
    }

    public int itemsInCartConfirmed() {
        return itemsInCart(mConfirmed);
    }

    public int calculateAmount(Realm realm) {
        boolean splitNotSet = !isSplit;
        if (splitNotSet) {
            return calculatePureAmount(realm);
        } else {
            return splitAmount;
        }
    }

    public int calculatePureAmount(Realm realm) {
        int total = 0;
        Cart cart = Cart.instance();
        for (Item item : cart.getItems(realm, mConfirmed)) {
            int amountFor = cart.getAmountFor(item.getId(), mConfirmed);
            total += Integer.parseInt(item.getPrice()) * amountFor;
            for (Customization customization : cart.getCustomizationsForItem(realm, item.getId())) {
                total += Integer.parseInt(customization.getPrice()) * amountFor;
            }
        }
        return total;
    }

    public int calculateFullOrderAmount(Realm realm) {
        int total = 0;
        Cart cart = Cart.instance();
        for (Item item : cart.getItems(realm, mFullOrders)) {
            int amountFor = cart.getAmountFor(item.getId(), mFullOrders);
            total += Integer.parseInt(item.getPrice()) * amountFor;
            for (Customization customization : cart.getCustomizationsForItem(realm, item.getId())) {
                total += Integer.parseInt(customization.getPrice()) * amountFor;
            }
        }
        return total;
    }

    public int calculateMyOrderAmount(Realm realm) {
        int total = 0;
        Cart cart = Cart.instance();
        for (Item item : cart.getItems(realm, mMyOrders)) {
            int amountFor = cart.getAmountFor(item.getId(), mMyOrders);
            total += Integer.parseInt(item.getPrice()) * amountFor;
            for (Customization customization : cart.getCustomizationsForItem(realm, item.getId())) {
                total += Integer.parseInt(customization.getPrice()) * amountFor;
            }
        }
        return total;
    }



    public List<Customization> getCustomizationsForItem(Realm realm, int itemId) {
        Order order = new Order(itemId);
        List<Customization> result = new ArrayList<>();
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            List<Integer> custIds = mOrders.get(indexOf).getCustomizations();
            for (int custId : custIds) {
                result.add(realm.where(Customization.class).equalTo("id", custId).findFirst());
            }
            return result;
        }
        return Collections.emptyList();
    }

    public boolean addCustomization(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            boolean customizationStatus = mOrders.get(indexOf).addCustomization(custId);
            saveOrders();
            return customizationStatus;
        }
        return false;
    }


    public boolean addCustomizationConfirmed(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            boolean customizationStatus = mOrders.get(indexOf).addCustomization(custId);
            saveOrders();
            return customizationStatus;
        }
        return false;
    }


    public void removeCustomization(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            mOrders.get(indexOf).removeCustomization(custId);
            saveOrders();
        }
    }

    public int getNoOfCustomizations(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int index = mOrders.indexOf(order);
            return mOrders.get(index).getCustomizationNumber(custId);
        }
        return 0;
    }

    public boolean hasCustomization(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            return mOrders.get(indexOf).hasCustomization(custId);
        }
        return false;
    }

    public void removeAllFromCart() {

        if(mOrders != null)
        mPreviousOrder = new ArrayList<>(mOrders);
        isSplit = false;
        MainApplication.data.saveData(LAST_ORDER, new Gson().toJson(mConfirmed));
        if(mConfirmed != null)
        mConfirmed.clear();
        if(mOrders != null)
        mOrders.clear();
        saveOrders();
        saveOrdersConfirmed();
    }

    public boolean restoreLastOrder() {
        isSplit = false;
        if (mPreviousOrder != null) {
            mergeOrders();
            return true;
        } else {
            String savedData = MainApplication.data.loadData(LAST_ORDER);
            if (true) {
                TypeToken<List<Order>> token = new TypeToken<List<Order>>() { };
                mPreviousOrder = new Gson().fromJson(savedData, token.getType());
                mergeOrders();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean restoreCurrentOrder() {
        String savedData = MainApplication.data.loadData(CURRENT_ORDER);
            if (true) {
                TypeToken<List<Order>> token = new TypeToken<List<Order>>() { };
                mOrders = new Gson().fromJson(savedData, token.getType());
                return true;
            } else {
                return false;
            }
    }

    public boolean restoreCurrentOrderConfirmed() {
        String savedData = MainApplication.data.loadData(CURRENT_ORDER_CONFIRMED);
        if (true) {
            TypeToken<List<Order>> token = new TypeToken<List<Order>>() { };
            mConfirmed = new Gson().fromJson(savedData, token.getType());
            return true;
        } else {
            return false;
        }
    }

    public boolean restoreTags() {
        String tags = MainApplication.data.loadData(USER_TAGS);
        if (true) {
            TypeToken<List<CustomerMini>> token = new TypeToken<List<CustomerMini>>() { };
            mTags = new Gson().fromJson(tags, token.getType());
            return true;
        } else {
            return false;
        }
    }

    public void restoreCartData()
    {
//        SharedPreferences prefs = .getSharedPreferences(saveTag, Context.MODE_PRIVATE);
//        return prefs.getBoolean(name, false);
        sentMySplit = MainApplication.data.loadBooleanData(SPLIT_SENT_MINE);
    }


    public void addTag(CustomerMini tagUser)
    {
        Log.i(LOG_TAG, " add tag 1");
        boolean isNew = true;
        if( mTags == null)
            mTags = new ArrayList<>();
        for(CustomerMini tag : mTags)
        {
            if(tag.getId() == tagUser.getId()) {
                isNew = false;
                break;
            }
        }
        Log.i(LOG_TAG, " add tag 2");
        if(isNew) {
            Log.i(LOG_TAG, " add tag 3");
            mTags.add(tagUser);
            Log.i(LOG_TAG, " add tag 4");
        }
        saveTags();
    }

    public int getTagTotal() {
        Log.i(LOG_TAG, " getTag total 1");
        int tempTotal = 0;
        for (CustomerMini tag : mTags) {
            if (tag.getAmount() != null) {
                tempTotal += tag.getAmount();
            }
        }
        return tempTotal;
    }

        public List<CustomerMini> getTags()
    {
        return mTags;
    }


    public void orderConfirmed() {
        mConfirmed.addAll(mOrders);
        mOrders.clear();
    }

    public void orderConfirmedFor(int itemId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            if(mConfirmed == null) {
                restoreCurrentOrderConfirmed();
                if(mConfirmed == null)
                    mConfirmed = new ArrayList<>();
            }

            mConfirmed.add(mOrders.remove(indexOf));
            saveOrders();
            saveOrdersConfirmed();
        }
    }

    public void orderConfirmedWithoutCustomizationFor(int itemId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            mOrders.get(indexOf).setOrderConfirmed(true);
            saveOrders();
        }
    }

    public void orderConfirmedCustomizationFor(int itemId, int custId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            mOrders.get(indexOf).addCustomizationConfirmed(custId);
            saveOrders();
        }
    }

    public void orderConfirmedFullyFor(int itemId) {
        Order order = new Order(itemId);
        if (mOrders.contains(order)) {
            int indexOf = mOrders.indexOf(order);
            mOrders.get(indexOf).setOrderConfirmed(true);
            mConfirmed.add(mOrders.remove(indexOf));
            saveOrders();
            saveOrdersConfirmed();
        }
    }


    public List<Order> getOrders() {
        return mOrders;
    }

    private void mergeOrders() {
        for (Order order : mPreviousOrder) {
            if (mOrders.contains(order)) {
                int indexOf = mOrders.indexOf(order);
                Order present = mOrders.get(indexOf);
                present.setAmount(present.getAmount() + order.getAmount());
            } else {
                mOrders.add(order);
            }
        }
        saveOrders();
        saveOrdersConfirmed();
    }

    public int getOrderId() {
        if(orderId == -1) {
            orderId = MainApplication.data.loadIntData(ORDER_ID);
            if(orderId == 0)
                orderId = -1;
        }
        return orderId;
    }

    public void setOrderId(int orderId) {
        MainApplication.data.saveIntData(ORDER_ID, orderId);
        Log.i(LOG_TAG, "Order id set - "+orderId);
        this.orderId = orderId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setSplitAmount(int splitAmount) {
        isSplit = true;
        this.splitAmount = splitAmount;
        mSplitAmount = splitAmount;
        MainApplication.data.saveIntData(SPLIT_AMOUNT, splitAmount);
    }

    public int getSplitAmount() {
        if(mSplitAmount == 0) {
            mSplitAmount = MainApplication.data.loadIntData(SPLIT_AMOUNT);
        }
        return mSplitAmount;
    }


    public int getPosOrderId() {
        if(posOrderId == -1) {
            posOrderId = MainApplication.data.loadIntData(POS_ID);
            if(posOrderId == 0)
                posOrderId = -1;
        }
        return posOrderId;
    }

    public void setPosOrderId(int posOrderId) {
        MainApplication.data.saveIntData(POS_ID, posOrderId);
        this.posOrderId = posOrderId;
    }

    public int getTableNo(Context context) {
        //tableNo = PreferenceManager.getDefaultSharedPreferences(context).getInt(TABLE_NO, tableNo);
        tableNo = MainApplication.data.loadIntData(TABLE_NO);
        if(tableNo == 0)
            tableNo = -1;
        return tableNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void releaseTable() {
        tableNo = -1;
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit().remove(TABLE_NO)
//                .apply();
        MainApplication.data.saveIntData(TABLE_NO, tableNo);
        posOrderId = -1;
        MainApplication.data.saveIntData(POS_ID, posOrderId);
        orderId = -1;
        MainApplication.data.saveIntData(ORDER_ID, orderId);
        // clearing checkin parameters
        MainApplication.data.saveData("checkedIn", false);
        MainApplication.data.saveIntData("tableCode", 0);
        MainApplication.data.saveIntData("taggerId", 0);
        MainApplication.data.saveData("temp.comment", "");
        MainApplication.data.saveData("temp.tagtext", "");
        MainApplication.data.saveData("temp.special", "");
        MainApplication.data.saveData(SETTLEMENT_TYPE, "");
        MainApplication.data.saveIntData(SETTLEMENT_PENDING_AMOUNT, 0);
        MainApplication.data.saveIntData(SPLIT_ID, 0);
        MainApplication.data.saveData(SPLIT_SENT_MINE, false);
        if(mTags!= null)
        mTags.clear();
        saveTags();
        mSplitPayments.clear();
        saveSplitPayments();
        setSplitAmount(0);
        setSettlementType("");


    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit().putInt(TABLE_NO, tableNo)
//                .apply();;
        MainApplication.data.saveIntData(TABLE_NO, tableNo);
    }

    private int getAmountFor(int itemId, List<Order> inCollection) {
        Order order = new Order(itemId);
        if (inCollection.contains(order)) {
            return inCollection.get(inCollection.indexOf(order)).getAmount();
        }
        return 0;
    }

    private List<Item> getItems(Realm realm, List<Order> inCollection) {
        List<Item> result = new ArrayList<>();
        for (Order order : inCollection) {
            result.add(realm.where(Item.class).equalTo("id", order.itemId).findFirst());
        }
        return result;
    }

    private int itemsInCart(List<Order> inCollection) {
        int items = 0;
        for (Order order : inCollection) {
            items += order.getAmount();
        }
        return items;
    }

    public List<Item> getFullOrderedItems(Realm realm) {
        List<Item> result = new ArrayList<>();
        List<OrderedItem> orderedList = realm.where(OrderedItem.class).findAll();
        mFullOrders.clear();
        for(OrderedItem orderedItem : orderedList)
        {
            boolean alreadyAdded = false;
            for(Item item :result)
            {
                if(item.getId() == orderedItem.getItemId()) {
                    alreadyAdded = true;
                    break;
                }
            }

            if(!alreadyAdded)
                result.add(realm.where(Item.class).equalTo("id", orderedItem.getItemId()).findFirst());

            int quantity = Integer.parseInt(orderedItem.getQuantity());
            for(int i = 0; i < quantity; i++)
                putItemFullOrder(orderedItem.getItemId());
        }
        return result;
    }

    public List<Item> getMyOrderedItems(Realm realm) {
        List<Item> result = new ArrayList<>();
        int customerId = MainApplication.data.user.getId();
        List<OrderedItem> orderedList = realm.where(OrderedItem.class).equalTo("customerId", "" + customerId).findAll();
        mMyOrders.clear();
        for(OrderedItem orderedItem : orderedList)
        {
            boolean alreadyAdded = false;
            for(Item item :result)
            {
                if(item.getId() == orderedItem.getItemId()) {
                    alreadyAdded = true;
                    break;
                }
            }

            if(!alreadyAdded)
                result.add(realm.where(Item.class).equalTo("id", orderedItem.getItemId()).findFirst());

            int quantity = Integer.parseInt(orderedItem.getQuantity());
            for(int i = 0; i < quantity; i++)
                putItemMyOrder(orderedItem.getItemId());
        }
        return result;
    }

    public void putItemFullOrder(int itemId) {
        Order order = new Order(itemId);
        if (!mFullOrders.contains(order)) {
            mFullOrders.add(new Order(itemId));
        } else {
            mFullOrders.get(mFullOrders.indexOf(order)).incrementAmount();
        }
        //Log.i(LOG_TAG, "FullOrders size after put - "+mFullOrders.size());
    }

    public void putItemMyOrder(int itemId) {
        Order order = new Order(itemId);
        if (!mMyOrders.contains(order)) {
            mMyOrders.add(new Order(itemId));
        } else {
            mMyOrders.get(mMyOrders.indexOf(order)).incrementAmount();
        }
        Log.i(LOG_TAG, "MyOrders size after put - " + mMyOrders.size());
    }

    public void saveSettledAmount(String amount)
    {
        mSettledAmount = amount;
        MainApplication.data.saveData("cart.settled_amount", amount);
    }

    public String getSettledAmount()
    {
        if(mSettledAmount.length() > 0)
            return mSettledAmount;
        else
        {
            String settledAmountTemp = MainApplication.data.loadData("cart.settled_amount");
            if(settledAmountTemp.length() > 0)
            {
                mSettledAmount = settledAmountTemp;
                return mSettledAmount;
            }else
            {
                mSettledAmount = "0";
                return mSettledAmount;
            }
        }
    }

    public void setPaymentUrl(String url)
    {
        PAYMENT_URL = url;
    }

    public String getPaymentUrl()
    {
        return PAYMENT_URL;
    }

    public void setSettlementType(String type)
    {
        settlementType = type;
        MainApplication.data.saveData(SETTLEMENT_TYPE, type);
    }

//    public void setSettlementAmount(int amount)
//    {
//        mSplitAmount = amount;
//        MainApplication.data.saveData(SETTLEMENT_TYPE, type);
//    }

    public String getSettlementType()
    {
        if(settlementType != null && settlementType.length() > 0)
            return settlementType;
        else
        {
            String settlementTypeTemp = MainApplication.data.loadData(SETTLEMENT_TYPE);
            if(settlementTypeTemp.length() > 0)
            {
                settlementType = settlementTypeTemp;
                return settlementType;
            }else
            {
                settlementType = "";
                return settlementType;
            }
        }
    }

    public void setSettlementPendingAmount(int amount)
    {
        settlementPendingAmount = amount;
        MainApplication.data.saveIntData(SETTLEMENT_PENDING_AMOUNT, amount);
    }

    public int getSettlementPendingAmount()
    {
        if(settlementPendingAmount > 0)
            return settlementPendingAmount;
        else
        {
            int settlementPendingAmountTemp = MainApplication.data.loadIntData(SETTLEMENT_PENDING_AMOUNT);
            if(settlementPendingAmountTemp  > 0)
            {
                settlementPendingAmount = settlementPendingAmountTemp;
                return settlementPendingAmount;
            }else
            {
                settlementPendingAmount = 0;
                return settlementPendingAmount;
            }
        }
    }

    public void setSplitId(int id)
    {
        splitId = id;
        MainApplication.data.saveIntData(SPLIT_ID, id);
    }

    public int getSplitId()
    {
        return MainApplication.data.loadIntData(SPLIT_ID);
    }

    public void addSplitPayment(SplitPayment splitPayment)
    {
        boolean isNew = true;
        for(SplitPayment sp : mSplitPayments)
        {
            if((int)sp.getId() == (int)splitPayment.getId()) {
                isNew = false;
                break;
            }
        }
        if(isNew) {
            mSplitPayments.add(splitPayment);
        }
        saveSplitPayments();
    }

    public void saveSplitPayments()
    {
        String splitString = new Gson().toJson(mSplitPayments);
        MainApplication.data.saveData(SPLIT_PAYMENTS, splitString);
    }

    public List<SplitPayment> getSplitPayments() {
        String splitPaymentsTemp = MainApplication.data.loadData(SPLIT_PAYMENTS);
        if(mSplitPayments.size() == 0) {
            TypeToken<List<SplitPayment>> token = new TypeToken<List<SplitPayment>>() {};
            mSplitPayments = new Gson().fromJson(splitPaymentsTemp, token.getType());
        }
        if(mSplitPayments == null)
            mSplitPayments = new ArrayList<>();

        return mSplitPayments;
    }

    public boolean isMySPlitSent()
    {
        return sentMySplit;
    }

    public void saveSentMySplit(boolean mySplitStatus)
    {
        MainApplication.data.saveData(SPLIT_SENT_MINE, mySplitStatus);
        sentMySplit = mySplitStatus;
    }

    public boolean isSplitSendingDone()
    {
        if(mSplitPayments.size() == mTags.size() && sentMySplit)
            return true;
        else
            return false;
    }

    public void clearAll() {
        clearCart();
        releaseTable();
    }

    public void clearCart() {
        mPreviousOrder = new ArrayList<>();
        isSplit = false;
        //MainApplication.data.saveData(LAST_ORDER, "");
        if(mConfirmed != null)
            mConfirmed.clear();
        else
            mConfirmed =  new ArrayList<>();

        if(mOrders != null)
            mOrders.clear();
        else
            mOrders =  new ArrayList<>();;

        if(mPreviousOrder != null)
            mPreviousOrder.clear();
        else
            mPreviousOrder =  new ArrayList<>();;
        saveOrders();
        saveOrdersConfirmed();
    }

    public float calculateFullTaxes(Realm realm)
    {
        float total = calculateFullOrderAmount(realm);

        float taxes = 0;
        Cart cart = Cart.instance();
        for (Item item : cart.getItems(realm, mFullOrders)) {
            int amountFor = cart.getAmountFor(item.getId(), mFullOrders);
            //taxes += Integer.parseInt(item.getPrice()) * amountFor;
            boolean food = false;
            if(realm.where(Category.class).equalTo("id", item.getCategoryId()).findFirst().getCatType().equalsIgnoreCase("Food"))
            {
                food = true;
                taxes += (Integer.parseInt(item.getPrice()) + Integer.parseInt(item.getPrice())/10) * amountFor * 14.5/100;
            }else {
                food = false;
                taxes += (Integer.parseInt(item.getPrice()) + Integer.parseInt(item.getPrice())/10) * amountFor * 5.5/100;
            }

            for (Customization customization : cart.getCustomizationsForItem(realm, item.getId())) {
                //taxes += Integer.parseInt(customization.getPrice()) * amountFor;
                if(food)
                    taxes += (Integer.parseInt(customization.getPrice()) + Integer.parseInt(customization.getPrice())/10) * amountFor * 14.5/100;
                else
                    taxes += (Integer.parseInt(customization.getPrice()) + Integer.parseInt(customization.getPrice())/10) * amountFor * 5.5/100;
            }
        }

        taxes += total * 5.8/100; // 5.6 service tax, 10 service charge, .2 swatch

        return taxes;
    }

    public float calculateMyTaxes(Realm realm)
    {
        float total = calculateFullOrderAmount(realm);

        float taxes = 0;
        Cart cart = Cart.instance();
        for (Item item : cart.getItems(realm, mMyOrders)) {
            int amountFor = cart.getAmountFor(item.getId(), mMyOrders);
            //taxes += Integer.parseInt(item.getPrice()) * amountFor;
            boolean food = false;
            if(realm.where(Category.class).equalTo("id", item.getCategoryId()).findFirst().getCatType().equalsIgnoreCase("Food"))
            {
                food = true;
                taxes += (Integer.parseInt(item.getPrice()) + Integer.parseInt(item.getPrice())/10) * amountFor * 14.5/100;
            }else {
                food = false;
                taxes += (Integer.parseInt(item.getPrice()) + Integer.parseInt(item.getPrice())/10) * amountFor * 5.5/100;
            }

            for (Customization customization : cart.getCustomizationsForItem(realm, item.getId())) {
                //taxes += Integer.parseInt(customization.getPrice()) * amountFor;
                if(food)
                    taxes += (Integer.parseInt(customization.getPrice()) + Integer.parseInt(customization.getPrice())/10) * amountFor * 14.5/100;
                else
                    taxes += (Integer.parseInt(customization.getPrice()) + Integer.parseInt(customization.getPrice())/10) * amountFor * 5.5/100;
            }
        }

        taxes += total * 5.8/100; // 5.6 service tax, 10 service charge, .2 swatch

        return taxes;
    }


}
