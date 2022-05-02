package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import ir.accountbooklet.android.Utils.AndroidUtilities;

public class RecyclerListView extends RecyclerView {
  private Context context;
  private RecyclerListViewListener listener;
  private RecyclerListViewClickListener clickListener;
  private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private AnimatorSet animatorSet;
  private boolean[] checkingPosition = new boolean[2];
  private int pointCheckPosition = 15;

  public interface RecyclerListViewListener {
    void onRecyclerListViewPointPosition(RecyclerView recyclerView, int position, boolean up);
    void onRecyclerListViewPositionChanged(RecyclerView recyclerView, int firstPosition, int lastPosition);
    void onRecyclerListViewScrollValueChanged(RecyclerView recyclerView, boolean value);
    void onRecyclerListViewScrollChanged(RecyclerView recyclerView, int currentScroll, int dx, int dy);
    void onRecyclerListViewScrollStateChanged(RecyclerView recyclerView, int newState);
  }

  public interface RecyclerListViewClickListener {
    void onRecyclerListViewPositionClicked(RecyclerView recyclerView, int position);
    void onRecyclerListViewPositionLongClicked(RecyclerView recyclerView, int position);
  }

  public static abstract class BaseModel implements Serializable {
    public int type;
  }

  public static class Holder extends ViewHolder {
    public Holder(@NonNull View itemView) {
      super(itemView);
    }
  }

  public static abstract class BaseAdapter<T extends BaseModel> extends Adapter<ViewHolder> {
    protected Context context;
    protected List<T> list;
    protected RecyclerListView recyclerListView;

    protected BaseAdapter(Context context) {
      this(context, new ArrayList<>());
    }

    protected BaseAdapter(Context context, List<T> list) {
      this.context = context;
      this.list = list;
    }

    public BaseModel getItem(int position) {
      return list != null ? list.get(position) : null;
    }

    @Override
    public int getItemViewType(int position) {
      return list != null && list.size() > 0 ? list.get(position).type : -1;
    }

    @Override
    public int getItemCount() {
      return list != null ? list.size() : 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
      recyclerListView = (RecyclerListView) recyclerView;
    }

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
      viewHolder.itemView.setOnClickListener(v -> {
        if (position < 0 || position >= getItemCount()) {
          return;
        }
        if (recyclerListView.clickListener != null) {
          recyclerListView.clickListener.onRecyclerListViewPositionClicked(recyclerListView, position);
        }
      });
      viewHolder.itemView.setOnLongClickListener(view -> {
        if (position < 0 || position >= getItemCount()) {
          return false;
        }
        if (recyclerListView.clickListener != null) {
          recyclerListView.clickListener.onRecyclerListViewPositionLongClicked(recyclerListView, position);
        }
        return false;
      });
      onBindViewHolder((RecyclerListView.Holder) viewHolder, position);
    }

    public void onBindViewHolder(@NonNull RecyclerListView.Holder holder, int position) {

    }
  }

  public static abstract class Decoration extends ItemDecoration {
    protected int space;

    protected Decoration(int space) {
      this.space = space;
    }

    @Override
    public final void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {

    }

    @Override
    public final void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
      int count = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
      if (count > 0) {
        getItemOffsets(outRect, view, parent, state, parent.getChildLayoutPosition(view), count);
      }
    }

    protected void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state, int position, int count) {

    }
  }

  public RecyclerListView(@NonNull Context context) {
    this(context, null);
  }

  public RecyclerListView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RecyclerListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    OnScrollListener onScrollListener = new OnScrollListener() {
      private int currentPosition = -1;
      private int lastPosition = -1;
      private int scrollValue = AndroidUtilities.dp(100);
      private int totalValueD;
      private boolean value = true;

      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (listener != null) {
          listener.onRecyclerListViewScrollStateChanged(recyclerView, newState);
        }
      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager == null) {
          return;
        }
        if (listener != null) {
          listener.onRecyclerListViewScrollChanged(recyclerView, recyclerView.computeVerticalScrollOffset() + recyclerView.computeHorizontalScrollOffset(), dx, dy);
        }
        int totalItem = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        int pointCheckingPosition = totalItem - pointCheckPosition;
        if (totalItem > pointCheckPosition && lastVisibleItem >= pointCheckingPosition && checkingPosition[0]) {
          checkingPosition[0] = false;
          if (listener != null) {
            listener.onRecyclerListViewPointPosition(recyclerView, pointCheckingPosition, false);
          }
        }
        if (totalItem > pointCheckPosition && firstVisibleItem <= pointCheckPosition && checkingPosition[1]) {
          checkingPosition[1] = false;
          if (listener != null) {
            listener.onRecyclerListViewPointPosition(recyclerView, pointCheckingPosition, true);
          }
        }
        if (firstVisibleItem != currentPosition || lastPosition != lastVisibleItem) {
          currentPosition = firstVisibleItem;
          lastPosition = lastVisibleItem;
          if (listener != null) {
            listener.onRecyclerListViewPositionChanged(recyclerView, currentPosition, lastPosition);
          }
        }
        if (firstVisibleItem == 0) {
          if (!value) {
            value = true;
            if (listener != null) {
              listener.onRecyclerListViewScrollValueChanged(recyclerView, true);
            }
          }
        } else {
          if (totalValueD > scrollValue && value) {
            totalValueD = 0;
            value = false;
            if (listener != null) {
              listener.onRecyclerListViewScrollValueChanged(recyclerView, false);
            }
          } else if (totalValueD < -scrollValue && !value) {
            totalValueD = 0;
            value = true;
            if (listener != null) {
              listener.onRecyclerListViewScrollValueChanged(recyclerView, true);
            }
          }
        }
        if ((value && dy > 0) || (!value && dy < 0)) {
          totalValueD += dy;
        }
      }
    };
    setOnScrollListener(onScrollListener);
    setMotionEventSplittingEnabled(false);
  }

  public void setRecyclerListViewListener(RecyclerListViewListener recyclerListViewListener) {
    this.listener = recyclerListViewListener;
  }

  public void setRecyclerListViewClickListener(RecyclerListViewClickListener recyclerListViewClickListener) {
    this.clickListener = recyclerListViewClickListener;
  }

  public void setPointCheckPosition(int pointCheckPosition) {
    if (pointCheckPosition < 0) {
      return;
    }

    this.pointCheckPosition = pointCheckPosition;
  }

  public boolean isCheckingPosition() {
    return checkingPosition[0];
  }

  public boolean isCheckingUpPosition() {
    return checkingPosition[1];
  }

  public void setCheckingPosition(boolean checkingPosition, boolean checkingUpPosition) {
    this.checkingPosition[0] = checkingPosition;
    this.checkingPosition[1] = checkingUpPosition;
  }

  public void setCheckingPosition(boolean checkingPosition) {
    this.checkingPosition[0] = checkingPosition;
  }

  public void scrollByWithAnimation(int x, int y) {
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }
    final float[] p = new float[1];
    animatorSet = new AnimatorSet();
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
    valueAnimator.addUpdateListener(animation -> {
      float progress = (float) animation.getAnimatedValue() - p[0];
      p[0] += progress;
      scrollBy((int) (x * progress), (int) (y * progress));
    });
    animatorSet.play(valueAnimator);
    animatorSet.setInterpolator(decelerateInterpolator);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        if (animation.equals(animatorSet)) {
          animatorSet = null;
        }
      }
    });
    animatorSet.setDuration(200);
    animatorSet.start();
  }

  public void scrollToFirstPosition(int startPosition) {
    LayoutManager layoutManager = getLayoutManager();
    if (!(layoutManager instanceof LinearLayoutManager)) {
      return;
    }
    if (startPosition > 0 && startPosition < ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition()) {
      scrollToPosition(startPosition);
    }
    SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
      @Override
      protected int getVerticalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_START;
      }
    };
    smoothScroller.setTargetPosition(0);
    layoutManager.startSmoothScroll(smoothScroller);
  }

  public void scrollToEndPosition(int startPosition) {
    LayoutManager layoutManager = getLayoutManager();
    if (layoutManager == null) {
      return;
    }
    int count = getAdapter() != null ? getAdapter().getItemCount() : 0;
    startPosition = count - startPosition - 1;
    if (startPosition < count && startPosition + 1 > ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition()) {
      scrollToPosition(startPosition);
    }

    SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
      @Override
      protected int getVerticalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_END;
      }
    };
    smoothScroller.setTargetPosition(count);
    layoutManager.startSmoothScroll(smoothScroller);
  }
}
