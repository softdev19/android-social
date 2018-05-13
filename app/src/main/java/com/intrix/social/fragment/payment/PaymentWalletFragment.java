package com.intrix.social.fragment.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;

/**
 * Worldtrack 16.12.15.
 */
public class PaymentWalletFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_wallet, container, false);

        return v;
    }
}
