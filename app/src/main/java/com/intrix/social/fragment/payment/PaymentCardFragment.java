package com.intrix.social.fragment.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.intrix.social.R;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class PaymentCardFragment extends Fragment implements
        View.OnClickListener, TextWatcher {

    private static final String LOG_TAG = PaymentCardFragment.class.getSimpleName();

    private EditText mCardNumber;
    private EditText mCardMonth;
    private EditText mCardYear;
    private EditText mCardSecurityCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_card, container, false);

        mCardNumber = (EditText) v.findViewById(R.id.card_number);
        mCardMonth = (EditText) v.findViewById(R.id.card_month);
        mCardYear = (EditText) v.findViewById(R.id.card_year);
        mCardSecurityCode = (EditText) v.findViewById(R.id.card_security);

        mCardNumber.addTextChangedListener(this);

        v.findViewById(R.id.pay_now).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
     /*   if (validate()) {
            CitrusClient citrusClient = CitrusClient.getInstance(getActivity());
            String cardNumber = mCardNumber.getText().toString().replaceAll("[- ]", "");
            CreditCardOption creditCardOption = new CreditCardOption("Card Holder Name",
                    cardNumber, mCardSecurityCode.getText().toString(),
                    Month.getMonth(mCardMonth.getText().toString()),
                    Year.getYear(mCardYear.getText().toString())
            );

            try {
                Realm realm = Realm.getInstance(getActivity());
                String amountStr = String.valueOf(Cart.instance().calculateAmount(realm));
                realm.close();
                //TODO: In production replace '5' with amountStr
                Amount amount = new Amount("5");
                PaymentType.PGPayment pgPayment = new PaymentType.PGPayment(amount,
                        SOCIAL_API_URL + BILL_GENERATOR, creditCardOption,
                        CitrusUser.DEFAULT_USER
                );

                citrusClient.pgPayment(pgPayment, new Callback<TransactionResponse>() {
                    @Override
                    public void success(TransactionResponse transactionResponse) {
                        Log.d(LOG_TAG, transactionResponse.getJsonResponse());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                });
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                Toaster.showToast("Something went wrong");
            }
        }
        */
    }

    //TODO: validate card number, month, year and security code here
    private boolean validate() {
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    private boolean mInEdit;

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!mInEdit) {
            mInEdit = true;
            String digits = s.toString().replaceAll("[- ]", "")
                    .replaceAll("\\d{4}", "$0 - ");
            if (before > count && digits.endsWith(" ")) {
                digits = digits.substring(0, digits.length() - 3);
            }
            mCardNumber.setText(digits);
            mCardNumber.setSelection(mCardNumber.length());
            mInEdit = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
