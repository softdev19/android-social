package com.intrix.social.fragment.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.intrix.social.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yarolegovich on 16.12.2015.
 */
public class PaymentNetFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_net, container, false);

        List<String> items = Arrays.asList("Other banks", "Other banks", "Other banks");
        Spinner spinner = (Spinner) v.findViewById(R.id.other_banks);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_bank_spinner, android.R.id.text1, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return v;
    }
}
