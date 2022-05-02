package ir.accountbooklet.android.Listeners;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int totalItem = linearLayoutManager.getItemCount();
        int VI = linearLayoutManager.findLastVisibleItemPosition();

        int first  = totalItem - 25;
        int second = totalItem - 15;
        int third  = totalItem - 1;

        if ( VI == first || VI == second || VI == third ) {
            onStart();
        }

        if (VI == 0) {
            if(!mControlsVisible) {
                onShow(true);
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                onShow(false);
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                onShow(true);
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        }
        if((mControlsVisible && dy>0) || (!mControlsVisible && dy<0)) {
            mScrolledDistance += dy;
        }
    }

    public abstract void onStart();

    public abstract void onShow(boolean show);
}
