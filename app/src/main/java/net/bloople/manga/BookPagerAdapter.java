package net.bloople.manga;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.util.Collections;
import java.util.List;

class BookPagerAdapter extends FragmentStateAdapter implements ListPreloader.PreloadModelProvider<GlideUrl> {
    private Book book;
    private RequestManager requestManager;
    private RecyclerViewPreloader<GlideUrl> preloader;

    BookPagerAdapter(FragmentActivity fa, Book book) {
        super(fa);
        this.book = book;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return PageFragment.newInstance(book.pageUrl(i));
    }

    @Override
    public int getItemCount() {
        return book.getPages();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        requestManager = Glide.with(recyclerView);
        preloader = new RecyclerViewPreloader<>(
            requestManager,
            this,
            new ViewPreloadSizeProvider<>(recyclerView),
            5
        );

        recyclerView.addOnScrollListener(preloader);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(preloader);
        preloader = null;
        requestManager = null;
    }

    @Override
    @NonNull
    public List<GlideUrl> getPreloadItems(int position) {
        MangosUrl pageUrl = book.pageUrl(position);
        return Collections.singletonList(pageUrl.toGlideUrl());
    }

    @Override
    @Nullable
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull GlideUrl url) {
        return requestManager.load(url).transform(new MatchWidthTransformation());
    }
}