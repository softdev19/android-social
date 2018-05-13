package com.intrix.social.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.common.view.RateBar;
import com.intrix.social.model.Transaction;
import com.intrix.social.model.Transactions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class TransactionsAdapter extends ArrayAdapter<TransactionsAdapter.TransactionInfo> {

    public TransactionsAdapter(Context context, List<Transactions> data) {
        super(context, 0, new ArrayList<>());
        addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewManager vh;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = inflater.inflate(getLayout(position), parent, false);
            if (isTransaction(position)) {
                vh = new TransactionViewHolder(convertView);
            } else {
                vh = new DateViewHolder(convertView);
            }
            convertView.setTag(vh);
        } else vh = (ViewManager) convertView.getTag();

        TransactionInfo info = getItem(position);
        vh.processView(info);

        return convertView;
    }

    @LayoutRes
    public int getLayout(int position) {
        return isTransaction(position) ?
                R.layout.item_single_transaction :
                R.layout.item_transactions;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        TransactionInfo info = getItem(position);
        if (info instanceof TransactionDetails) {
            return 1;
        } else if (info instanceof TransactionDate) {
            return 0;
        } else {
            throw new RuntimeException("Unknown element in the list");
        }
    }

    public boolean isTransaction(int position) {
        return getItemViewType(position) == 1;
    }

    /*
     * We need to map list passed into constructor to out internal representation.
     */
    @SuppressWarnings("unchecked")
    public void addAll(List<Transactions> data) {
        List<? super TransactionInfo> structuredData = new ArrayList<>();
        for (Transactions transactions : data) {
            structuredData.add(new TransactionDate(transactions.getDate()));
            List<Transaction> dateTrans = transactions.getTransactions();
            for (Transaction transaction : dateTrans) {
                structuredData.add(new TransactionDetails(transaction));
            }
        }
        addAll((Collection<? extends TransactionInfo>) structuredData);
    }

    /*
     * Classes that know how to process certain view types.
     */
    public interface ViewManager {
        void processView(TransactionInfo info);
    }

    public static class DateViewHolder implements ViewManager {

        private SimpleDateFormat FORMAT = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());

        TextView date;

        public DateViewHolder(View itemView) {
            date = (TextView) itemView.findViewById(R.id.date);
        }

        @Override
        public void processView(TransactionInfo info) {
            Date dateObj = ((TransactionDate) info).mDate;
            date.setText(FORMAT.format(dateObj));
        }
    }

    public static class TransactionViewHolder implements ViewManager {

        TextView comment;
        TextView amount;
        TextView feedbackLeave;
        RateBar rateBar;

        public TransactionViewHolder(View itemView) {
            amount = (TextView) itemView.findViewById(R.id.transaction_amount);
            comment = (TextView) itemView.findViewById(R.id.feedback_comment);
            rateBar = (RateBar) itemView.findViewById(R.id.rate_bar);
            feedbackLeave = (TextView) itemView.findViewById(R.id.feedback_leave);
        }

        @Override
        public void processView(TransactionInfo info) {
            Transaction t = ((TransactionDetails) info).mTransaction;
            amount.setText(t.getAmount());
            String rating = t.getRating();
            String c = comment(rating);
            comment.setText(c);
            if (rating != null && rating.length() > 0) {
                feedbackLeave.setVisibility(View.GONE);
                rateBar.setVisibility(View.VISIBLE);
                rateBar.setRating((int)Double.parseDouble(rating));
            } else {
                feedbackLeave.setVisibility(View.VISIBLE);
                rateBar.setVisibility(View.GONE);
            }
        }

        private String comment(String rating) {
            return (rating != null && rating.length() > 0) ?
                    "Feedback provided" :
                    "Feedback not provided";

//            return rating != -1 ?
//                    "Feedback provided" :
//                    "Feedback not provided";
        }
    }

    /*
     * Classes used for pattern-matching our current element.
     */
    public static class TransactionInfo {}
    private static class TransactionDate extends TransactionInfo {
        private Date mDate;

        private TransactionDate(Date date) {
            mDate = date;
        }
    }
    private static class TransactionDetails extends TransactionInfo {
        private Transaction mTransaction;

        private TransactionDetails(Transaction transaction) {
            mTransaction = transaction;
        }
    }
}
