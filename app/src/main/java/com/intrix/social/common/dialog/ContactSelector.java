package com.intrix.social.common.dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;

import com.intrix.social.adapter.ContactsAdapter;
import com.intrix.social.model.Contact;
import com.intrix.social.utils.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yarolegovich on 14.01.2016.
 */
public class ContactSelector {

    public static void selectContacts(Context context, Callback<List<Contact>> selectedListener) {
        ContactsAdapter adapter = new ContactsAdapter(context, getContacts(context));
        new AlertDialog.Builder(context)
                .setTitle("Select friends")
                .setAdapter(adapter, null)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    selectedListener.onResult(adapter.getChecked());
                }).show();
    }

    public static List<Contact> getContacts(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return Collections.emptyList();
        }
        ArrayList<Contact> contacts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (hasPhoneNumber(cursor)) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null
                    );
                    if (pCur == null) {
                        continue;
                    }
                    while (pCur.moveToNext()) {
                        int numberIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String contactNumber = pCur.getString(numberIndex);
                        int nameIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        String name = pCur.getString(nameIndex);
                        contacts.add(new Contact(name, contactNumber));
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        return contacts;
    }

    private static boolean hasPhoneNumber(Cursor cursor) {
        return Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
        ) > 0;
    }
}
