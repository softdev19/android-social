package com.intrix.social.common;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestManager;

/**
 * Created by yarolegovich on 07.01.2016.
 */
public final class RecyclerViewPreloader<T> extends RecyclerView.OnScrollListener {

    private final RecyclerToListViewScrollListener recyclerScrollListener;

    /**
     * Helper constructor that accepts an {@link Activity}.
     */
    public RecyclerViewPreloader(Activity activity,
                                 ListPreloader.PreloadModelProvider<T> preloadModelProvider,
                                 ListPreloader.PreloadSizeProvider<T> preloadDimensionProvider, int maxPreload) {
        this(Glide.with(activity), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /**
     * Helper constructor that accepts an {@link FragmentActivity}.
     */
    public RecyclerViewPreloader(FragmentActivity fragmentActivity,
                                 ListPreloader.PreloadModelProvider<T> preloadModelProvider, ListPreloader.PreloadSizeProvider<T> preloadDimensionProvider,
                                 int maxPreload) {
        this(Glide.with(fragmentActivity), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /**
     * Helper constructor that accepts an {@link Fragment}.
     */
    public RecyclerViewPreloader(Fragment fragment,
                                 ListPreloader.PreloadModelProvider<T> preloadModelProvider, ListPreloader.PreloadSizeProvider<T> preloadDimensionProvider,
                                 int maxPreload) {
        this(Glide.with(fragment), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }

    /**
     * Helper constructor that accepts an {@link android.support.v4.app.Fragment}.
     */
    public RecyclerViewPreloader(android.app.Fragment fragment,
                                 ListPreloader.PreloadModelProvider<T> preloadModelProvider, ListPreloader.PreloadSizeProvider<T> preloadDimensionProvider,
                                 int maxPreload) {
        this(Glide.with(fragment), preloadModelProvider, preloadDimensionProvider, maxPreload);
    }
    /**
     * Constructor that accepts interfaces for providing the dimensions of images to preload, the list
     * of models to preload for a given position, and the request to use to load images.
     *
     * @param preloadModelProvider     Provides models to load and requests capable of loading them.
     * @param preloadDimensionProvider Provides the dimensions of images to load.
     * @param maxPreload               Maximum number of items to preload.
     */
    public RecyclerViewPreloader(RequestManager requestManager,
                                 ListPreloader.PreloadModelProvider<T> preloadModelProvider,
                                 ListPreloader.PreloadSizeProvider<T> preloadDimensionProvider,
                                 int maxPreload) {

        ListPreloader<T> listPreloader = new ListPreloader<T>(preloadModelProvider,
                preloadDimensionProvider,
                maxPreload);
        recyclerScrollListener = new RecyclerToListViewScrollListener(listPreloader);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        recyclerScrollListener.onScrolled(recyclerView, dx, dy);
    }

    public final class RecyclerToListViewScrollListener extends RecyclerView.OnScrollListener {
        public static final int UNKNOWN_SCROLL_STATE = Integer.MIN_VALUE;
        private final AbsListView.OnScrollListener scrollListener;
        private int lastFirstVisible = -1;
        private int lastVisibleCount = -1;
        private int lastItemCount = -1;

        public RecyclerToListViewScrollListener(AbsListView.OnScrollListener scrollListener) {
            this.scrollListener = scrollListener;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            int listViewState;
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_IDLE;
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    listViewState = ListView.OnScrollListener.SCROLL_STATE_FLING;
                    break;
                default:
                    listViewState = UNKNOWN_SCROLL_STATE;
            }

            scrollListener.onScrollStateChanged(null /*view*/, listViewState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
            int itemCount = recyclerView.getAdapter().getItemCount();

            if (firstVisible != lastFirstVisible || visibleCount != lastVisibleCount
                    || itemCount != lastItemCount) {
                scrollListener.onScroll(null, firstVisible, visibleCount, itemCount);
                lastFirstVisible = firstVisible;
                lastVisibleCount = visibleCount;
                lastItemCount = itemCount;
            }
        }
    }
}
