package ir.accountbooklet.android.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.ActionBarPopupWindow;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.RecyclerListView;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.MediasModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.FileUtils;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.PermissionManager;
import ir.accountbooklet.android.Utils.Theme;

public class MediasFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener, RecyclerListView.RecyclerListViewClickListener, RecyclerListView.RecyclerListViewListener, ActionBarPopupWindow.ListPopupView.PopupItemClickListener {

  public static MediasFragment newInstance() {
    return new MediasFragment();
  }

  private SelectorImageView ivBack;
  private TextView tvTitle;
  private TextView tvCount;
  private TextView tvFolders;
  private ViewGroup toolbar;
  private View viewTop;
  private View layoutTop;
  private View shdToolbar;
  private RecyclerListView recyclerListView;
  private MediaAdapter adapter;
  private List<MediasModel> medias = new ArrayList<>();
  private List<MediasModel> mediaFolder = new ArrayList<>();
  private List<String> folders = new ArrayList<>();
  private MediaSelectListener listener;
  private String selection;
  private ActionBarPopupWindow actionBarPopupWindows;
  private ActionBarPopupWindow.ListPopupView listPopupView;
  private AnimatorSet animator;
  private int flag = Constants.FLAG_IMAGE;
  private int toolbarProgress;
  private boolean isToolbarShow;

  private class MediaAdapter extends RecyclerListView.BaseAdapter<MediasModel> {

    private MediaAdapter(Context context, List<MediasModel> list) {
      super(context, list);
    }

    @Override
    public @NonNull RecyclerListView.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
      return new RecyclerListView.Holder(new MediaRow(context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListView.Holder viewHolder, final int position) {
      ((MediaRow) viewHolder.itemView).setMediaFile(list.get(position));
    }
  }

  private class MediaRow extends FrameLayout {
    private ImageView ivThumbnail;
    private TextView tvVideoInfo;

    public MediaRow(@NonNull Context context) {
      super(context);

      int radii = AndroidUtilities.dp(3);

      ivThumbnail = new ImageView(context);
      ivThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
      ivThumbnail.setRadius(radii);
      addView(ivThumbnail, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

      tvVideoInfo = new TextView(context);
      tvVideoInfo.setTextColor(0xFFFFFFFF);
      tvVideoInfo.setTextSize(10);
      tvVideoInfo.setGravity(Gravity.CENTER_VERTICAL);
      tvVideoInfo.setDrawableSize(AndroidUtilities.dp(15));
      tvVideoInfo.setCompoundDrawablePadding(AndroidUtilities.dp(3));
      tvVideoInfo.setCompoundDrawables(Theme.play_arrow_drawable, null, null, null);
      tvVideoInfo.setBackground(Theme.createDrawable(0x70000000, 0, 0, Theme.RECT, AndroidUtilities.dp(10f)));
      tvVideoInfo.setPadding(AndroidUtilities.dp(3), 0, AndroidUtilities.dp(6), 0);
      addView(tvVideoInfo, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20), Gravity.BOTTOM | 3, AndroidUtilities.dp(5), 0, 0, AndroidUtilities.dp(5)));

      Theme.setSelectorView(this, new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector_white)).style(Theme.RECT).radii(radii));
    }

    public void setMediaFile(MediasModel media) {
      if (media != null) {
        Glide.with(context).load(media.file).transition(GenericTransitionOptions.with(view -> ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).setDuration(200).start())).centerCrop().override(AndroidUtilities.dp(90), AndroidUtilities.dp(90)).placeholder(R.drawable.ic_post_no_image).into(ivThumbnail);

        if (media.isVideo) {
          AndroidUtilities.setVisibility(tvVideoInfo, View.VISIBLE);
          tvVideoInfo.setText(AndroidUtilities.formatDuration(media.duration, false));
        } else {
          AndroidUtilities.setVisibility(tvVideoInfo, View.GONE);
          tvVideoInfo.setText(null);
        }
      } else {
        ivThumbnail.setImageBitmap(null);
      }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int makeMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(widthMeasureSpec));
      super.onMeasure(makeMeasureSpec, makeMeasureSpec);
    }
  }

  public interface MediaSelectListener {
    void onMediaSelect(File file);
  }

  public MediasFragment setFlag(int flag) {
    this.flag = flag;
    return this;
  }

  private void initialize(View view) {
    ivBack = view.findViewById(R.id.ivBack);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvCount = view.findViewById(R.id.tvCount);
    tvFolders = view.findViewById(R.id.tvFolders);
    toolbar = view.findViewById(R.id.toolbar);
    viewTop = view.findViewById(R.id.viewTop);
    layoutTop = view.findViewById(R.id.layoutTop);
    shdToolbar = view.findViewById(R.id.shdToolbar);
    recyclerListView = view.findViewById(R.id.recyclerView);
  }

  private void initializeDefault() {
    if (flag == Constants.FLAG_VIDEO) {
      tvTitle.setText(R.string.select_video);

      selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    } else if(flag == Constants.FLAG_IMAGE_VIDEO) {
      tvTitle.setText(R.string.select_image_video);

      selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    } else {
      tvTitle.setText(R.string.select_image);

      selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    }

    layoutTop.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), 0, 0, 0, 0));
    viewTop.setBackground(Theme.createDrawable(0x20000000, 0, 0, Theme.RECT, AndroidUtilities.dp(2.5f)));
  }

  private void initializeRecyclerViews() {
    recyclerListView.addItemDecoration(new RecyclerListView.Decoration(AndroidUtilities.dp(1.5f)) {
      @Override
      protected void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state, int position, int count) {
        if (position < 4) {
          outRect.top = space * 2;
        } else {
          outRect.top = space;
        }
        if (position > ((count / 3) * 3)) {
          outRect.bottom = space * 2;
        } else {
          outRect.bottom = space;
        }
        outRect.left = space;
        outRect.right = space;
      }
    });
    recyclerListView.setAdapter(adapter = new MediaAdapter(context, mediaFolder));
  }

  private void initializeListeners() {
    ivBack.setOnClickListener(this);
    tvFolders.setOnClickListener(this);
    recyclerListView.setRecyclerListViewListener(this);
    recyclerListView.setRecyclerListViewClickListener(this);
  }

  private void loadMedias() {
    new Thread(() -> {
      List<MediasModel> mMedias = new ArrayList<>();
      String[] mediaProjections = {
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        Build.VERSION.SDK_INT > 28 ? MediaStore.Files.FileColumns.DATE_MODIFIED : MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Video.VideoColumns.DURATION
      };
      Cursor cursor = null;
      try {
        cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), mediaProjections, selection, null, Build.VERSION.SDK_INT > 28 ? MediaStore.Files.FileColumns.DATE_MODIFIED : MediaStore.Video.Media.DATE_TAKEN);
        if (cursor != null) {
          int mediaIdColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
          int dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
          int dateColumn = cursor.getColumnIndex(Build.VERSION.SDK_INT > 28 ? MediaStore.Files.FileColumns.DATE_MODIFIED : MediaStore.Video.Media.DATE_TAKEN);
          int durationColumn = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
          int widthColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.WIDTH);
          int heightColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT);
          int sizeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
          folders.add(context.getString(R.string.all_media));
          if (flag == Constants.FLAG_IMAGE_VIDEO) {
            folders.add(context.getString(R.string.all_Videos));
          }
          while (cursor.moveToNext()) {
            String path = cursor.getString(dataColumn);
            if (TextUtils.isEmpty(path)) {
              continue;
            }
            File file = new File(path);
            int mediaId = cursor.getInt(mediaIdColumn);
            long dateTaken = cursor.getLong(dateColumn);
            String durationStr = cursor.getString(durationColumn);
            long duration = TextUtils.isEmpty(durationStr) ? 0 : Long.parseLong(durationStr);
            int width = cursor.getInt(widthColumn);
            int height = cursor.getInt(heightColumn);
            long size = cursor.getLong(sizeColumn);
            boolean isVideo = !AndroidUtilities.isImage(file);
            String parent = file.getParentFile().getName();
            if (!TextUtils.isEmpty(parent) && !folders.contains(parent)) {
              folders.add(parent);
            }
            mMedias.add(new MediasModel(file, null, null, mediaId, dateTaken, (int)(duration / 1000f), width, height, size, isVideo));
          }
          int sizeList = mMedias.size();
          for (int i=0; i < sizeList; i++) {
            medias.add(mMedias.get((sizeList - 1) - i));
          }
          AndroidUtilities.runOnUIThread(() -> setListFolder(0));
        }
      } catch (Exception e) {
        AppLog.e(MediasFragment.class, e.getMessage());
      } finally {
        if (cursor != null) {
          try {
            cursor.close();
          } catch (Exception e) {
            AppLog.e(MediasFragment.class, e.getMessage());
          }
        }
      }
    }).start();
  }

  private void setListFolder(int value) {
    recyclerListView.scrollToPosition(0);
    String folder = folders.get(value);
    tvFolders.setText(folder);
    mediaFolder.clear();
    if (value == 0) {
      mediaFolder.addAll(medias);
      tvFolders.setText(context.getString(R.string.gallery));
    } else if (flag == Constants.FLAG_IMAGE_VIDEO && value == 1) {
      for (MediasModel media : medias) {
        if (media != null && media.file != null && media.isVideo) {
          mediaFolder.add(media);
        }
      }
    } else {
      for (MediasModel media : medias) {
        if (media != null && media.file != null && !TextUtils.isEmpty(folder) && TextUtils.equals(folder, media.file.getParentFile().getName())) {
          mediaFolder.add(media);
        }
      }
    }
    tvCount.setText(String.valueOf(mediaFolder.size()));
    adapter.notifyDataSetChanged();
  }

  private void animationToolbar(final boolean show) {
    if (show == isToolbarShow) {
      return;
    }
    isToolbarShow = show;

    if (animator != null && animator.isRunning()) {
      animator.removeAllListeners();
      animator.cancel();
    }
    if (show) {
      AndroidUtilities.setVisibility(toolbar, View.VISIBLE);
      AndroidUtilities.setVisibility(shdToolbar, View.VISIBLE);
    }
    animator = new AnimatorSet();
    animator.playTogether(
      ObjectAnimator.ofFloat(toolbar, View.ALPHA, toolbar.getAlpha(), show ? 1.0f : 0.0f),
      ObjectAnimator.ofFloat(shdToolbar, View.ALPHA, shdToolbar.getAlpha(), show ? 1.0f : 0.0f));
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        animator = null;
        if (!show) {
          AndroidUtilities.setVisibility(toolbar, View.INVISIBLE);
          AndroidUtilities.setVisibility(shdToolbar, View.INVISIBLE);
          toolbar.setAlpha(0);
          shdToolbar.setAlpha(0);
        }
      }
    });
    animator.setDuration(150);
    animator.start();
  }

  private void setToolbarProgress(int progress) {
    viewTop.setAlpha(1.0f - (progress / 100f));
    ViewGroup.LayoutParams params = layoutTop.getLayoutParams();
    params.height = AndroidUtilities.dp(22) + AndroidUtilities.dp((int)(progress / 3.58f));
    layoutTop.setLayoutParams(params);
    int radii = AndroidUtilities.dp(10 - (progress / 10f));
    layoutTop.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, radii, radii, radii, radii, 0, 0, 0, 0));
  }

  private void actionFolders() {
    if (listPopupView == null) {
      listPopupView = new ActionBarPopupWindow.ListPopupView(tvFolders.getContext());
      listPopupView.setPopupItemClickListener(this);

      actionBarPopupWindows = new ActionBarPopupWindow(listPopupView);
      actionBarPopupWindows.setOnDismissListener(() -> listPopupView.clearItems());
    }
    for (int i=0; i < folders.size(); i++) {
      listPopupView.addPopupItem(i, folders.get(i), null);
    }
    if (listPopupView.getItemCount() > 0) {
      actionBarPopupWindows.show(tvFolders, true);
    }
  }

  public MediasFragment setListener(MediaSelectListener listener) {
    this.listener = listener;
    return this;
  }

  @Override
  public View onCreateViewButtonSheet(Bundle savedInstanceState) {
    return View.inflate(context, R.layout.fragment_medias, null);
  }

  @Override
  public void onViewCreatedButtonSheet(Bundle savedInstanceState, View view) {
    super.onViewCreatedButtonSheet(savedInstanceState, view);
    initialize(view);
    initializeDefault();
    initializeRecyclerViews();
    initializeListeners();
    PermissionManager.requestPermissions(context, Constants.REQUEST_PERMISSION_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        dismiss();
        break;
      case R.id.tvFolders:
        actionFolders();
        break;
    }
  }

  @Override
  public void onPopupItemClicked(int action) {
    actionBarPopupWindows.dismiss(false);
    AndroidUtilities.runOnUIThread(() -> setListFolder(action));
  }

  @Override
  public void onRecyclerListViewPointPosition(RecyclerView recyclerView, int position, boolean up) {

  }

  @Override
  public void onRecyclerListViewPositionChanged(RecyclerView recyclerView, int firstPosition, int lastPosition) {

  }

  @Override
  public void onRecyclerListViewScrollValueChanged(RecyclerView recyclerView, boolean value) {

  }

  @Override
  public void onRecyclerListViewScrollChanged(RecyclerView recyclerView, int currentScroll, int dx, int dy) {

  }

  @Override
  public void onRecyclerListViewScrollStateChanged(RecyclerView recyclerView, int newState) {

  }

  @Override
  public void onRecyclerListViewPositionClicked(RecyclerView recyclerView, int position) {
    MediasModel media = mediaFolder.get(position);
    if (media == null) {
      return;
    }
    if (media.file != null && media.file.exists()) {
      if (media.isVideo) {
        long length = media.file.length();
        if ((length / 1024f) / 1024f > Constants.MAX_LENGTH_FILE) {
          AndroidUtilities.toast(context, String.format(Locale.US, context.getString(R.string.err_max_video_length), Constants.MAX_LENGTH_FILE));
          return;
        }
      }
      if (listener != null) {
        listener.onMediaSelect(media.file);
      }
      dismiss();
    } else {
      AndroidUtilities.toast(context, R.string.file_not_exists);
    }
  }

  @Override
  public void onRecyclerListViewPositionLongClicked(RecyclerView recyclerView, int position) {

  }

  @Override
  public void onStateChanged(@NonNull View bottomSheet, int newState) {
    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
      dismiss();
    }
  }

  @Override
  public void onSlide(@NonNull View bottomSheet, float slideOffset) {
    int progress = slideOffset >= 1.0f ? 100 : slideOffset <= 0.8f ? 0 : (int)((slideOffset - 0.8f) * 500);
    if (progress > 0 && progress < 100) {
      setToolbarProgress(progress);
    } else {
      if (progress == 0 && toolbarProgress != 0) {
        setToolbarProgress(progress);
      } else if (progress == 100 && toolbarProgress != 100) {
        setToolbarProgress(progress);
      }
    }
    toolbarProgress = progress;
    animationToolbar(slideOffset >= 1.0f);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == Constants.REQUEST_PERMISSION_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      FileUtils.checkCreateFolders();
      loadMedias();
    } else {
      AndroidUtilities.toast(context, R.string.err_permission_storage);
      dismiss();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    actionBarPopupWindows = null;
    listPopupView = null;
  }
}