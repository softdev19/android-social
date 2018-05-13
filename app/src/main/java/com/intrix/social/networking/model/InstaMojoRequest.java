package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sutharsha on 29/02/16.
 */
public class InstaMojoRequest {
    String id;
    String purpose;
    int amount;
    @SerializedName("buyer_name")
    String buyerName;
    String email;
    String phone;
    @SerializedName("send_email")
    boolean sendEmail;
    @SerializedName("send_sms")
    boolean sendSms;
    @SerializedName("redirect_url")
    String redirectUrl;
    @SerializedName("allow_repeated_payments")
    boolean repeatedPayments;
    String webhook;
    String shorturl;
    String longurl;
    @SerializedName("sms_status")
    String smsStatus;
    @SerializedName("email_status")
    String emailStatus;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendSms() {
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
        this.sendSms = sendSms;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isRepeatedPayments() {
        return repeatedPayments;
    }

    public void setRepeatedPayments(boolean repeatedPayments) {
        this.repeatedPayments = repeatedPayments;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

    public String getLongurl() {
        return longurl;
    }

    public void setLongurl(String longurl) {
        this.longurl = longurl;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }

    public String getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(String emailStatus) {
        this.emailStatus = emailStatus;
    }
}
