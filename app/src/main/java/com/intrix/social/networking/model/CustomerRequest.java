package com.intrix.social.networking.model;

import com.intrix.social.model.CustomerMini;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class CustomerRequest {
    private CustomerMini customer;

    public CustomerRequest(CustomerMini customer) {
        this.customer = customer;
    }

    public CustomerMini getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerMini customer) {
        this.customer = customer;
    }
}
