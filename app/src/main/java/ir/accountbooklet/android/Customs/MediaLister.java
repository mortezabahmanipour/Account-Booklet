package ir.accountbooklet.android.Customs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.Theme;

public class MediaLister extends FrameLayout {
    private Context context;
    private List<Media> medias = new ArrayList<>();
    private int itemMediaSize;
    private int margin = AndroidUtilities.dp(10);
    private int spanCount = 4;
    private int maxMedia = 16;
    private int mode = MODE_EDIT;
    public final static int MODE_EDIT = 0;
    public final static int MODE_GALLERY = 1;
    private int flag = FLAG_LEFT_TO_RIGHT;
    public final static int FLAG_RIGHT_TO_LEFT = 0;
    public final static int FLAG_LEFT_TO_RIGHT = 1;
    private MediaListerListener listener;

    public interface MediaListerListener {
        void onMediaAdd();
        void onMediaChanged();
    }

    public MediaLister(Context context) {
        this(context, null);
    }

    public MediaLister(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaLister(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setMotionEventSplittingEnabled(false);
        updateMediaItemSize();
        addAddButton();
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == MODE_EDIT) {
            boolean needAdd = true;
            for (Media media : medias) {
                if (media.itemView instanceof AddView) {
                    needAdd = false;
                } else if (media.itemView instanceof MediaView) {
                    MediaView mediaView = (MediaView) media.itemView;
                    mediaView.notifyData();
                }
            }
            if (needAdd) {
                addAddButton();
            }
        } else if (mode == MODE_GALLERY) {
            Media addMedia = null;
            for (Media media : medias) {
                if (media.itemView instanceof AddView) {
                    addMedia = media;
                } else if (media.itemView instanceof MediaView) {
                    MediaView mediaView = (MediaView) media.itemView;
                    mediaView.notifyData();
                }
            }
            if (addMedia != null) {
                medias.remove(addMedia);
                removeView(addMedia.itemView);
            }
        }
    }

    public void setFlag(int flag) {
        this.flag = flag;
        updateMargins();
    }

    private void setSpanCount(int spanCount) {
        this.spanCount = Math.max(spanCount, 1);
        updateMargins();
    }

    private void setMaxMedia(int maxMedia) {
        this.maxMedia = maxMedia;
        checkMaxMedia();
        updateMargins();
    }

    public void setOnMediaChangeListener(MediaListerListener listener) {
        this.listener = listener;
    }

    public int getMediaSize() {
        int count = 0;
        for(Media media : medias) {
            if(!media.isAddView()) {
                count++;
            }
        }
        return count;
    }

    private void addAddButton() {
        Media media = new Media(null, null, null, null, "اضافه کردن", false);
        media.setItemView(new AddView(context));
        media.getItemView().setOnClickListener(view -> {
            if (listener != null) {
                listener.onMediaAdd();
            }
        });
        medias.add(0, media);
        addView(media.getItemView(), 0);
        updateMargins();
    }

    public void addMedia(String id, File file, String url, Bitmap bitmap) {
        int mediaSize = getMediaSize();
        Media media = new Media(id, file, url, bitmap, null, mediaSize == 0);
        medias.add(mediaSize, media);
        addMediaLayout(media);
        checkMaxMedia();
    }

    public List<Media> getMedias() {
        final List<Media> list = new ArrayList<>();
        for(Media media : medias) {
            if (!media.isAddView()) {
                list.add(media);
            }
        }
        return list;
    }

    private void removeMedia(Media media) {
      medias.remove(media);

      removeView(media.getItemView());
      checkMaxMedia();
      updateMargins();

      if (media.isSelect() && medias.size() > 1) {
        Media mMedia = medias.get(0);
        mMedia.setSelect(true);

        ((MediaView) mMedia.getItemView()).checkSelect();
      }
    }

    public void removeAllMedia() {
        Media mMedia = null;
        for (Media media : medias) {
            if (!media.isAddView()) {
                removeView(media.getItemView());
            } else {
                mMedia = media;
            }
        }
        medias.clear();
        if (mMedia != null) {
            medias.add(mMedia);
        }
        updateMargins();
    }

    private void addMediaLayout(final Media media) {
        final MediaView mediaView = new MediaView(context, media);
        mediaView.setOnClickListener(v -> {
            if (mode == MODE_EDIT) {
                for (Media mMedia : medias) {
                    if (!mMedia.isAddView()) {
                        mMedia.setSelect(mMedia == media);
                        ((MediaView) mMedia.getItemView()).checkSelect();
                    }
                }
            } else if (mode == MODE_GALLERY) {
                AndroidUtilities.showFile(context, media.file);
            }
        });

        media.setItemView(mediaView);

        addView(media.getItemView());
        updateMargins();

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(mediaView, View.ALPHA, 0.0f, 1.0f));
        animatorSet.setDuration(400);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mediaView.getAlpha() == 0) {
                    removeMedia(media);
                    if (listener != null) {
                        listener.onMediaChanged();
                    }
                } else {
                    mediaView.setBtnDeleteListener(view -> {
                        mediaView.setBtnDeleteListener(null);
                        animatorSet.playTogether(ObjectAnimator.ofFloat(mediaView, View.ALPHA, 1.0f, 0.0f));
                        animatorSet.start();
                    });
                }
            }
        });
        animatorSet.start();
    }

    private void updateMediaItemSize() {
        itemMediaSize = ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(40)) - (margin * (spanCount - 1))) / spanCount;
    }

    private void updateMargins() {
        for(int i=0; i < medias.size(); i++) {
            View view = medias.get(i).getItemView();
            int computing = i > 0 ? i / spanCount : 0;
            int number = (spanCount - 1) - (i - computing * spanCount);
            int marginTop = computing == 0 ? 0 : (itemMediaSize + margin) * computing;
            if (flag == FLAG_LEFT_TO_RIGHT) {
                number = spanCount - (number + 1);
            }
            int marginLeft = number == 0 ? 0 : (itemMediaSize + margin) * number;
            view.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, marginLeft, marginTop, 0, 0));
        }
    }

    private void checkMaxMedia() {
        AndroidUtilities.setVisibility(medias.get(medias.size() - 1).getItemView(), getMediaSize() >= maxMedia ? View.GONE : View.VISIBLE);
    }

    private class MediaView extends FrameLayout {
        private Media media;
        private TextView tvName;
        private ImageView ivThumbnail;
        private SelectorImageView ivDelete;

        public MediaView(Context context, Media media) {
            super(context);
            this.media = media;
            setMotionEventSplittingEnabled(false);
            ivThumbnail = new ImageView(context);
            ivThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivThumbnail.setRadius(AndroidUtilities.dp(7));
            if (media.getFile() != null) {
                Glide.with(context).asBitmap().load(media.getFile()).centerCrop().override(itemMediaSize).placeholder(R.drawable.ic_post_no_image).into(ivThumbnail);
            } else if (media.bitmap != null) {
                Glide.with(context).asBitmap().load(media.bitmap).centerCrop().override(itemMediaSize).placeholder(R.drawable.ic_post_no_image).into(ivThumbnail);
            } else {
                Glide.with(context).asBitmap().load(media.getUrl()).centerCrop().override(itemMediaSize).placeholder(R.drawable.ic_post_no_image).into(ivThumbnail);
            }
            tvName = new TextView(context);
            tvName.setSelectorBuilder(new Theme.SelectorBuilder().selectable(false).color(0xA0FFFFFF).radii(AndroidUtilities.dp(7)).disableShape(Theme.TOP).style(Theme.RECT));
            tvName.setEllipsize(TextUtils.TruncateAt.END);
            tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
            tvName.setSingleLine(true);
            tvName.setTextColor(Theme.getColor(Theme.key_app_text));
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(10);
            tvName.setText(media.getText());
            tvName.setPadding(AndroidUtilities.dp(2) , AndroidUtilities.dp(2), AndroidUtilities.dp(2), AndroidUtilities.dp(2));
            checkSelect();

            ivDelete = new SelectorImageView(context);
            ivDelete.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(0xFFFFFFFF).style(Theme.OVAL));
            ivDelete.setImageDrawable(Theme.close_media_lister_drawable);
            ivDelete.setPadding((itemMediaSize / 3) / 6 , (itemMediaSize / 3) / 6, (itemMediaSize / 3) / 6, (itemMediaSize / 3) / 6);
            addView(ivThumbnail, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            addView(tvName, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
            addView(ivDelete, LayoutHelper.createFrame(itemMediaSize / 3, itemMediaSize / 3, 5));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setForeground(Theme.createSelectorDrawable(new Theme.SelectorBuilder().pressedColor(0xFFFFFFFF).radii(AndroidUtilities.dp(7)).style(Theme.RECT)));
            }
            notifyData();
        }

        public void notifyData() {
            AndroidUtilities.setVisibility(ivDelete, mode == MODE_EDIT ? View.VISIBLE : View.GONE);
        }

        public void checkSelect() {
            AndroidUtilities.setVisibility(tvName, !TextUtils.isEmpty(tvName.getText()) && media.isSelect() ? View.VISIBLE : View.GONE);
        }

        private void setBtnDeleteListener(OnClickListener listener) {
            ivDelete.setOnClickListener(listener);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(itemMediaSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemMediaSize, MeasureSpec.EXACTLY));
        }
    }

    private class AddView extends AppCompatImageView {

        public AddView(Context context) {
            super(context);
            setBackgroundDrawable(createDashGapDrawable());
            setPadding(itemMediaSize / 3, itemMediaSize / 3, itemMediaSize / 3, itemMediaSize / 3);
            setImageDrawable(Theme.positive_media_lister_drawable);
        }

        private Drawable createDashGapDrawable() {
            GradientDrawable gradientDrawable = new GradientDrawable();
            final int size = AndroidUtilities.dp(7);
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadii(new float[] {size, size, size, size, size, size, size, size});
            gradientDrawable.setColor(Color.WHITE);
            gradientDrawable.setStroke(AndroidUtilities.dp(1), Theme.getColor(Theme.key_app_media_lister_stroke), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
            return gradientDrawable;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(itemMediaSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemMediaSize, MeasureSpec.EXACTLY));
        }
    }

    public static class Media {
        public String id;
        public String url;
        public File file;
        public Bitmap bitmap;
        private String text;
        private boolean select;
        private View itemView;

        private Media(String id, File file, String url, Bitmap bitmap, String text, boolean select) {
            this.id = id;
            this.url = url;
            this.file = file;
            this.bitmap = bitmap;
            this.text = text;
            this.select = select;
        }

        private Media(String id, File file, String text, boolean select) {
            this(id, file, null, null, text, select);
        }

        private Media(String id, String url, String text, boolean select) {
            this(id, null, url, null, text, select);
        }

        private Media(String id, Bitmap bitmap, String text, boolean select) {
            this(id, null, null, bitmap, text, select);
        }

        public boolean isAddView() {
            return TextUtils.isEmpty(id) && TextUtils.isEmpty(url) && file == null;
        }

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public File getFile() {
            return file;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        public boolean isSelect() {
            return select;
        }

        public View getItemView() {
            return itemView;
        }

        public void setItemView(View itemView) {
            this.itemView = itemView;
        }
    }
}
