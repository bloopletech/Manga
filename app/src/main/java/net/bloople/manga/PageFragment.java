package net.bloople.manga;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class PageFragment extends Fragment {
    public static final String MAX_IMAGE_DIMENSION = "1500";
    private Context context;
    private ScrollView scroller;
    private String url;

    static PageFragment newInstance(String url) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scroller = view.findViewById(R.id.scroller);

        ImageView imageView = view.findViewById(R.id.image);

        RequestListener<GlideUrl, GlideDrawable> requestListener = new LoadedRequestListener();

        Glide
                .with(context)
                .load(new GlideUrl(url))
                .transform(new MatchWidthTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(requestListener)
                .into(imageView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

//    private void cacheNextPage() {
//        Glide
//                .with(context)
//                .load(urlWithContentHint(session.url(session.nextPage())))
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .preload();
//    }

    private GlideUrl urlWithContentHint(String uri) {
        return new GlideUrl(uri, new LazyHeaders.Builder()
                .addHeader("Width", MAX_IMAGE_DIMENSION)
                .build());
    }

    private class LoadedRequestListener implements RequestListener<GlideUrl, GlideDrawable> {
        @Override
        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            //loadingImage = false;
            if(e != null) {
                e.printStackTrace();
                System.out.println("URL: " + model.toStringUrl());
            }
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            scroller.scrollTo(0, 0);

            //loadingImage = false;

            return false;
        }
    }
}
