package com.intrix.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.fragment.DishExpandableListFragment;
import com.intrix.social.fragment.DishPagerFragment;
import com.intrix.social.model.Category;

import java.util.List;

/**
 * Created by yarolegovich on 02.01.2016.
 * !!!!!THIS IS NOT A TYPO!!!!!
 */
public class CategoriesListListAdapter extends ArrayAdapter<Category> {


    public CategoriesListListAdapter(Context context, List<Category> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryHolder ch;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_simple_list, parent, false);
            ch = new CategoryHolder(convertView);
            convertView.setTag(ch);
        } ch = (CategoryHolder) convertView.getTag();

        Category category = getItem(position);

        ch.mCategory = category;

        ch.name.setText(category.getName());

        return convertView;
    }

    private static class CategoryHolder implements View.OnClickListener {

        private Category mCategory;

        TextView name;

        public CategoryHolder(View v) {
            name = (TextView) v.findViewById(R.id.text);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, UniversalActivity.class);
            intent.putExtra(UniversalActivity.EXTRA_TOKEN, DishExpandableListFragment.class);
            intent.putExtra(DishPagerFragment.EXTRA_CATEGORY_ID, mCategory.getId());
            context.startActivity(intent);
        }
    }
}
