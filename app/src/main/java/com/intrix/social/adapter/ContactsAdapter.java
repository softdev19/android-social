package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 14.01.2016.
 */
public class ContactsAdapter extends ArrayAdapter<Contact> {

    private boolean[] checked;

    public ContactsAdapter(Context context, List<Contact> data) {
        super(context, 0, data);
        checked = new boolean[data.size()];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactHolder ch;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            ch = new ContactHolder(convertView);
            convertView.setTag(ch);
        } ch = (ContactHolder) convertView.getTag();

        ch.position = position;

        Contact contact = getItem(position);

        ch.name.setText(contact.name);
        ch.phone.setText(contact.phoneNum);
        ch.box.setChecked(checked[position]);

        return convertView;
    }

    public List<Contact> getChecked() {
        List<Contact> result = new ArrayList<>();
        for (int index = 0; index < checked.length; index++) {
            if (checked[index]) {
                result.add(getItem(index));
            }
        }
        return result;
    }

    private class ContactHolder implements View.OnClickListener {

        int position;

        TextView name;
        TextView phone;
        CheckBox box;

        public ContactHolder(View v) {
            name = (TextView) v.findViewById(R.id.name);
            phone = (TextView) v.findViewById(R.id.phone);
            box = (CheckBox) v.findViewById(R.id.box);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            boolean isChecked = box.isChecked();
            box.setChecked(!isChecked);
            checked[position] = !isChecked;
        }
    }
}
