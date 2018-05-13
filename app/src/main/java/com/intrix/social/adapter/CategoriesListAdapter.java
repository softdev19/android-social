package com.intrix.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.fragment.DishPagerFragment;
import com.intrix.social.model.Category;

import java.util.List;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder> {

    private Context mContext;
    private int mItemTintColor;

    private List<Category> mCategories;

    public CategoriesListAdapter(List<Category> categories) {
        mCategories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = mCategories.get(position);

        holder.mCategory = category;

        holder.comments.setText(category.getComment());
        holder.likes.setText(category.getFavorite());
        holder.name.setText(category.getName());
        String image = "";
        if(category.getImage() != null)
        {
            image = category.getImage().replaceAll("\\.\\w+$", ".webp");
        }else
            image = "http://lostandfoundtravel.net/wp-content/themes/AiwazMag/images/no-img.png";

        Log.d(getClass().getSimpleName(), image);
        Glide.with(mContext).load(image).into(holder.itemImage);

        holder.itemImage.setColorFilter(mItemTintColor);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mContext = recyclerView.getContext();
        mItemTintColor = ContextCompat.getColor(mContext, R.color.item_dish_tint);
        super.onAttachedToRecyclerView(recyclerView);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Category mCategory;

        ImageView itemImage;
        TextView comments;
        TextView likes;
        TextView name;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            comments = (TextView) itemView.findViewById(R.id.comment_number);
            likes = (TextView) itemView.findViewById(R.id.liked_number);
            name = (TextView) itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, UniversalActivity.class);
            intent.putExtra(UniversalActivity.EXTRA_TOKEN, DishPagerFragment.class);
            intent.putExtra(DishPagerFragment.EXTRA_CATEGORY_ID, mCategory.getId());
            mContext.startActivity(intent);
        }
    }
}
