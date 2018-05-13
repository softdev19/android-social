package com.intrix.social.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by yarolegovich on 05.01.2016.
 */
public class DateValidateWatcher implements TextWatcher {

    public static void bindTo(EditText editText) {
        editText.addTextChangedListener(new DateValidateWatcher(editText));
    }

    private String mCurrent = "";
    @SuppressWarnings("FieldCanBeLocal")
    private String mTemplate = "DDMMYYYY";
    private Calendar mCalendar = Calendar.getInstance();

    private EditText mWrapped;

    public DateValidateWatcher(EditText wrapped) {
        mWrapped = wrapped;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(mCurrent)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = mCurrent.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            int sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2) {
                sel++;
            }

            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8) {
                clean = clean + mTemplate.substring(clean.length());
            } else {
                int day = Integer.parseInt(clean.substring(0, 2));
                int mon = Integer.parseInt(clean.substring(2, 4));
                int year = Integer.parseInt(clean.substring(4, 8));

                if (mon > 12) mon = 12;
                mCalendar.set(Calendar.MONTH, mon - 1);
                year = (year < 1900) ? 1900 : (year > 2000) ? 2000 : year;
                mCalendar.set(Calendar.YEAR, year);

                day = (day > mCalendar.getActualMaximum(Calendar.DATE)) ? mCalendar.getActualMaximum(Calendar.DATE) : day;
                clean = String.format("%02d%02d%02d", day, mon, year);
            }

            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8));

            sel = sel < 0 ? 0 : sel;
            mCurrent = clean;
            mWrapped.setText(mCurrent);
            mWrapped.setSelection(sel < mCurrent.length() ? sel : mCurrent.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
