package com.intrix.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrix.social.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yarolegovich on 02.01.2016.
 */
public class ShareAdapter extends ArrayAdapter<ShareAdapter.ShareOption> {

    public static List<ShareOption> SHARE_OPTIONS = Arrays.asList(
            new ShareOption(R.drawable.twitter, "#Tweet It", "com.twitter.android"),
            new ShareOption(R.drawable.snap, "#Snap It", "com.snapchat.android"),
            new ShareOption(R.drawable.vine, "#Vine It", "co.vine.android"),
            new ShareOption(R.drawable.instagram, "#Instagram It", "com.instagram.android"),
            new ShareOption(R.drawable.facebook, "#Share It", "com.facebook.katana"),
            new ShareOption(R.drawable.per, "#Stream It", "tv.periscope.android")
    );

    public ShareAdapter(Context context) {
        super(context, 0, SHARE_OPTIONS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShareHolder sh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_share, parent, false);
            sh = new ShareHolder(convertView);
            convertView.setTag(sh);
        } else sh = (ShareHolder) convertView.getTag();

        ShareOption option = getItem(position);

        sh.icon.setImageResource(option.icon);
        sh.text.setText(option.text);

        return convertView;
    }

    private static class ShareHolder {

        private ShareOption mShareOption;

        ImageView icon;
        TextView text;

        public ShareHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.icon);
            text = (TextView) v.findViewById(R.id.text);
        }
    }

    public static class ShareOption {
        public final int icon;
        public final String text;
        public final String appPackage;

        public ShareOption(int icon, String text, String appPackage) {
            this.icon = icon;
            this.text = text;
            this.appPackage = appPackage;
        }
    }
}
