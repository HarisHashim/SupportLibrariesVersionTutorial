package com.byoutline.secretsauce.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.squareup.picasso.Picasso;

/**
 * Pauses picasso operations marked with given context tag, when view
 * is scrolled quickly.
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class PicassoScrollListener extends RecyclerView.OnScrollListener {
    private final Context context;
    private static final int SPEED_THRESHOLD = 24;

    /**
     * @param context must by equal to tag that was set in picasso(usually activity context).
     */
    public PicassoScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int currentSpeed = Math.abs(dx) + Math.abs(dy);
        final Picasso picasso = Picasso.with(context);
        if (currentSpeed > SPEED_THRESHOLD) {
            picasso.pauseTag(context);
        } else {
            picasso.resumeTag(context);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        final Picasso picasso = Picasso.with(context);
        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }
}

