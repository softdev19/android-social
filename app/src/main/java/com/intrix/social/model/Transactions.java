package com.intrix.social.model;

import java.util.Date;
import java.util.List;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class Transactions {

    private Date mDate;
    private List<Transaction> mTransactions;

    public void setDate(Date date) {
        mDate = date;
    }

    public void setTransactions(List<Transaction> transactions) {
        mTransactions = transactions;
    }

    public Date getDate() {
        return mDate;
    }

    public List<Transaction> getTransactions() {
        return mTransactions;
    }

    /*
    public static class Transaction {
        private String paymentType;
        private String amount;
        private String comment;
        private int rating = -1;

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "paymentType='" + paymentType + '\'' +
                    ", amount='" + amount + '\'' +
                    ", comment='" + comment + '\'' +
                    ", rating=" + rating +
                    '}';
        }
    }
    */
}
