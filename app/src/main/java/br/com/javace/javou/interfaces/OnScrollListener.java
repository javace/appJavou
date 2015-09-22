package br.com.javace.javou.interfaces;

/**
 * Created by Rudson Lima on 12/07/15.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

public abstract class OnScrollListener extends RecyclerView.OnScrollListener {

    private int currentPage = 1;
    private int totalItemCount;
    private int visibleItemCount;
    private int firstVisibleItem;

    private int lastVisibleItem = 0;
    private boolean checkScroll = true;

    private boolean loading = false;
    private int currentTotalItems = 0;

    private LinearLayoutManager mLinearLayoutManager;
    private FloatingActionButton mFloatingActionButton;
    public OnScrollListener(LinearLayoutManager linearLayoutManager, FloatingActionButton floatingActionButton) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.mFloatingActionButton = floatingActionButton;
    }

    public OnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (totalItemCount < currentTotalItems) {
            this.currentPage = 1;
            this.currentTotalItems = totalItemCount;

            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > currentTotalItems)) {
            loading = false;
            currentTotalItems = totalItemCount;
        }

        if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + 5)) {
            onLoadMore(currentPage++);
            loading = true;
        }

        if (lastVisibleItem != firstVisibleItem) {
            if (lastVisibleItem < firstVisibleItem) {
                if (checkScroll) {
                    checkScroll = false;
                    showHideFloatButton(true);
                    onScroll(recyclerView, dx, dy, true);
                }
            } else {
                if (!checkScroll) {
                    checkScroll = true;
                    showHideFloatButton(false);
                    onScroll(recyclerView, dx, dy, false);
                }
            }
        }

        lastVisibleItem = firstVisibleItem;
    }

    public void resetEndlessRecyclerView(){
        this.currentTotalItems = 0;
        this.loading = false;
    }

    public abstract void onScroll(RecyclerView recyclerView, int dx, int dy, boolean onScroll);
    public abstract void onLoadMore(int currentPage);

    //Hide or show the FloatingActionButton based on the list scroll
    private void showHideFloatButton(boolean status) {
        if (status) {
            mFloatingActionButton.setAlpha(1f);
            mFloatingActionButton.setTranslationY(0f);
            mFloatingActionButton.animate().alpha(0f)
                    .translationY(mFloatingActionButton.getHeight())
                    .setDuration(175L)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFloatingActionButton.setVisibility(FrameLayout.GONE);
                        }
                    }).start();
        } else {
            mFloatingActionButton.setVisibility(View.VISIBLE);
            mFloatingActionButton.setAlpha(0f);
            mFloatingActionButton.setTranslationY(mFloatingActionButton.getHeight());
            mFloatingActionButton.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(175L)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFloatingActionButton.setVisibility(FrameLayout.VISIBLE);
                        }
                    })
                    .start();
        }
    }
}