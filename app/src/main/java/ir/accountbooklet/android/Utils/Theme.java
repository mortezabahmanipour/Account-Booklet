package ir.accountbooklet.android.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import ir.accountbooklet.android.ApplicationLoader;
import ir.accountbooklet.android.R;

public class Theme {
  public static final int NONE = 0;
  public static final int RECT = 1;
  public static final int OVAL = 2;

  public static final int TOP = 1;
  public static final int BOTTOM = 2;
  public static final int LEFT = 4;
  public static final int RIGHT = 8;

  public static final String key_app_primary = "app_primary";
  public static final String key_app_accent = "app_accent";
  public static final String key_app_background = "app_background";
  public static final String key_app_wrong = "app_wrong";
  public static final String key_action_bar_default = "app_action_bar_default";
  public static final String key_app_background_message = "app_background_message";
  public static final String key_app_advertises_text_title = "app_advertises_text_title";
  public static final String key_app_advertises_text_price = "app_advertises_text_price";
  public static final String key_app_base_tab_layout_text_name = "app_base_tab_layout_text_name";
  public static final String key_app_base_tab_layout_text_name_select = "app_base_tab_layout_text_name_select";
  public static final String key_app_advertises_text_time = "app_advertises_text_time";
  public static final String key_app_edit_text_stroke = "app_edit_text_stroke";
  public static final String key_app_media_lister_stroke = "app_media_lister_stroke";
  public static final String key_app_like = "app_like";
  public static final String key_app_request = "app_request";
  public static final String key_app_advertises_shadow = "app_advertises_shadow";
  public static final String key_app_text_light = "app_text_light";
  public static final String key_app_drawer_text_item = "app_drawer_text_item";
  public static final String key_app_drawer_icon_item = "app_drawer_icon_item";
  public static final String key_app_drawer_shadow_item = "app_drawer_shadow_item";
  public static final String key_app_default = "app_text_default";
  public static final String key_app_default2 = "app_text_default2";
  public static final String key_app_default3 = "app_text_default3";
  public static final String key_app_default4 = "app_text_default4";
  public static final String key_app_selector = "app_selector";
  public static final String key_app_selector_x = "app_selector_x";
  public static final String key_app_selector_white = "app_selector_white";
  public static final String key_app_selector_color = "app_selector_color";
  public static final String key_app_choose_media = "app_choose_media";
  public static final String key_app_text = "app_text";
  public static final String key_app_text2 = "app_text2";
  public static final String key_app_text3 = "app_text3";
  public static final String key_app_text4 = "app_text4";
  public static final String key_app_text5 = "app_text5";
  public static Drawable app_check_dubble_drawable;
  public static Drawable app_report_drawable;
  public static Drawable app_check_drawable;
  public static Drawable app_time_drawable;
  public static Drawable app_radio_button_on_drawable;
  public static Drawable app_radio_button_off_drawable;
  public static Drawable check_box_off_drawable;
  public static Drawable check_box_on_drawable;
  public static Drawable arrow_left_drawable;
  public static Drawable filter_list_drawable;
  public static Drawable call_account_drawable;
  public static Drawable edit_account_drawable;
  public static Drawable content_copy_account_drawable;
  public static Drawable sort_drawable;
  public static Drawable delete_drawable;
  public static Drawable edit_drawable;
  public static Drawable positive_drawable;
  public static Drawable positive_media_lister_drawable;
  public static Drawable close_media_lister_drawable;
  public static Drawable play_arrow_drawable;
  public static Drawable fingerprint_drawable;
  private static HashMap<String, Integer> colors = new HashMap<>();
  private static boolean instance;

  static {
    setColors(ApplicationLoader.applicationContext);
  }

  public static void createInstance(Context context) {
    if (instance) {
      return;
    }
    setResources(context);
  }

  public static void setColors(Context context) {
    if (context == null) {
      return;
    }
    Resources res = context.getResources();
    colors.put(key_app_primary, res.getColor(R.color.colorPrimary));
    colors.put(key_app_accent, res.getColor(R.color.colorAccent));
    colors.put(key_app_background, res.getColor(R.color.colorBackground));
    colors.put(key_app_wrong, 0xFFFFD600);
    colors.put(key_action_bar_default, 0xFFFFFFFF);
    colors.put(key_app_background_message, res.getColor(R.color.colorMessage));
    colors.put(key_app_selector, res.getColor(R.color.colorDefaultSelector));
    colors.put(key_app_selector_x, res.getColor(R.color.colorDefaultSelector2));
    colors.put(key_app_advertises_text_title, res.getColor(R.color.colorBlackPrimary));
    colors.put(key_app_advertises_text_price, 0xFF44CC74);
    colors.put(key_app_advertises_text_time, 0xFFFFFFFF);
    colors.put(key_app_edit_text_stroke, 0x15000000);
    colors.put(key_app_media_lister_stroke, 0x20000000);
    colors.put(key_app_like, 0xFFD50000);
    colors.put(key_app_request, 0xFF3E85CE);
    colors.put(key_app_base_tab_layout_text_name, 0x60000000);
    colors.put(key_app_base_tab_layout_text_name_select, 0xFF000000);
    colors.put(key_app_advertises_shadow, 0x3E848484);
    colors.put(key_app_text_light, res.getColor(R.color.colorBlackLight));
    colors.put(key_app_drawer_text_item, 0x90000000);
    colors.put(key_app_drawer_icon_item, 0x70000000);
    colors.put(key_app_drawer_shadow_item, 0x20000000);
    colors.put(key_app_default, res.getColor(R.color.colorDefault));
    colors.put(key_app_default2, res.getColor(R.color.colorDefault2));
    colors.put(key_app_default3, res.getColor(R.color.colorDefault3));
    colors.put(key_app_default4, res.getColor(R.color.colorDefault4));
    colors.put(key_app_selector_white, 0x90FFFFFF);
    colors.put(key_app_selector_color, ColorUtils.setAlphaComponent(res.getColor(R.color.colorDefault), 150));
    colors.put(key_app_choose_media, 0x6D000000);
    colors.put(key_app_text, res.getColor(R.color.colorText));
    colors.put(key_app_text2, res.getColor(R.color.colorText2));
    colors.put(key_app_text3, res.getColor(R.color.colorText3));
    colors.put(key_app_text4, res.getColor(R.color.colorText4));
    colors.put(key_app_text5, res.getColor(R.color.colorText5));
  }

  @SuppressLint("UseCompatLoadingForDrawables")
  public static void setResources(Context context) {
    if (context == null) {
      return;
    }
    Resources res = context.getResources();
    app_check_dubble_drawable = res.getDrawable(R.drawable.ic_check_dubble).mutate();
    app_report_drawable = res.getDrawable(R.drawable.ic_report).mutate();
    app_check_drawable = res.getDrawable(R.drawable.ic_check).mutate();
    app_time_drawable = res.getDrawable(R.drawable.ic_time).mutate();
    app_radio_button_off_drawable = res.getDrawable(R.drawable.ic_radio_button_off).mutate();
    app_radio_button_on_drawable = res.getDrawable(R.drawable.ic_radio_button_on).mutate();
    check_box_on_drawable = res.getDrawable(R.drawable.ic_check_box_on).mutate();
    check_box_off_drawable = res.getDrawable(R.drawable.ic_check_box_off).mutate();
    arrow_left_drawable = res.getDrawable(R.drawable.ic_arrow_left).mutate();
    filter_list_drawable = res.getDrawable(R.drawable.ic_filter_list).mutate();
    call_account_drawable = res.getDrawable(R.drawable.ic_phone).mutate();
    edit_account_drawable = res.getDrawable(R.drawable.ic_edit).mutate();
    content_copy_account_drawable = res.getDrawable(R.drawable.ic_content_copy).mutate();
    sort_drawable = res.getDrawable(R.drawable.ic_sort).mutate();
    delete_drawable = res.getDrawable(R.drawable.ic_delete).mutate();
    edit_drawable = res.getDrawable(R.drawable.ic_edit).mutate();
    positive_drawable = res.getDrawable(R.drawable.ic_positive).mutate();
    positive_media_lister_drawable = res.getDrawable(R.drawable.ic_positive).mutate();
    close_media_lister_drawable = res.getDrawable(R.drawable.ic_close).mutate();
    play_arrow_drawable = res.getDrawable(R.drawable.ic_play_arrow).mutate();
    fingerprint_drawable = res.getDrawable(R.drawable.ic_fingerprint).mutate();

    setDrawableColor(app_radio_button_off_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(app_radio_button_on_drawable, getColor(Theme.key_app_accent));
    setDrawableColor(check_box_off_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(check_box_on_drawable, getColor(Theme.key_app_accent));
    setDrawableColor(arrow_left_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(filter_list_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(call_account_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(edit_account_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(content_copy_account_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(sort_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(delete_drawable, getColor(Theme.key_app_accent));
    setDrawableColor(edit_drawable, getColor(Theme.key_app_default4));
    setDrawableColor(positive_drawable, 0xFFFFFFFF);
    setDrawableColor(positive_media_lister_drawable, getColor(Theme.key_app_text2));
    setDrawableColor(close_media_lister_drawable, getColor(Theme.key_app_accent));
    setDrawableColor(fingerprint_drawable, getColor(Theme.key_app_text2));
  }

  public static int getColor(String key) {
    Integer value = colors.get(key);
    return value != null ? value : 0xff000000;
  }

  public static Drawable setDrawableColor(Drawable drawable, int color) {
    if (drawable == null) {
      return null;
    }
    if (drawable instanceof ShapeDrawable) {
      ((ShapeDrawable) drawable).getPaint().setColor(color);
    } else {
      drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }
    return drawable;
  }

  public static Drawable createDrawable(@NonNull SelectorBuilder builder) {
    return createDrawable(builder.color, builder.strokeColor, builder.strokeWidth, builder.style, builder.radii);
  }

  public static Drawable createDrawable(int color, int strokeColor, float strokeWidth, int style, float... radii) {
    if (color == 0 && strokeColor == 0) {
      return null;
    }
    GradientDrawable gradientDrawable = new GradientDrawable();
    if (style == RECT) {
      gradientDrawable.setShape(GradientDrawable.RECTANGLE);

      if (radii != null) {
        gradientDrawable.setCornerRadii(radii.length == 8 ? radii : new float[]{radii[0], radii[0], radii[0], radii[0], radii[0], radii[0], radii[0], radii[0]});
      }
    } else if (style == OVAL) {
      gradientDrawable.setShape(GradientDrawable.OVAL);
    }

    if (color != 0) {
      gradientDrawable.setColor(color);
    }
    if (strokeWidth > 0) {
      gradientDrawable.setStroke((int) strokeWidth, strokeColor);
    }
    return gradientDrawable;
  }

  public static Drawable createDashGapDrawable(int color, int strokeColor, int strokeWidth, float dashWidth, float dashGap, int style, float... radii) {
    GradientDrawable gradientDrawable = (GradientDrawable) createDrawable(color, strokeColor, strokeWidth, style, radii);
    gradientDrawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
    return gradientDrawable;
  }

  public static void setSelectorView(@NonNull View view, SelectorBuilder builder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      view.setBackground(builder == null ? null : Theme.createDrawable(builder));
      view.setForeground(builder == null ? null : Theme.createSelectorDrawable(builder, true));
    } else {
      view.setBackground(builder == null ? null : Theme.createSelectorDrawable(builder));
    }
  }

  public static Drawable createSelectorDrawable(SelectorBuilder builder) {
    return createSelectorDrawable(builder, false);
  }

  public static Drawable createSelectorDrawable(SelectorBuilder builder, boolean foreground) {
    if (builder == null) {
      return null;
    }

    Drawable defaultDrawable = createDrawable(foreground ? 0 : builder.color, foreground ? 0 : builder.strokeColor, foreground ? 0 : builder.strokeWidth, builder.style, builder.radii);

    if (!builder.selectable) {
      return defaultDrawable;
    }

    if (!builder.simple && Build.VERSION.SDK_INT >= 21) {
      Drawable pressedDrawable = createDrawable(0xffffffff, 0, 0, builder.style, builder.radii);
      ColorStateList colorStateList = new ColorStateList(
        new int[][]{StateSet.WILD_CARD},
        new int[]{builder.pressedColor}
      );
      return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
    } else {
      int color = builder.color == 0 ? ColorUtils.setAlphaComponent(builder.pressedColor, 125) : (builder.simple ? builder.pressedColor : ColorUtils.blendARGB(builder.pressedColor, builder.color, 0.5F));
      Drawable pressedDrawable = createDrawable(color, builder.pressedStrokeColor, builder.pressedStrokeWidth, builder.pressedStyle, builder.pressedRadii);
      StateListDrawable stateListDrawable = new StateListDrawable();
      stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
      stateListDrawable.addState(new int[]{android.R.attr.state_focused}, pressedDrawable);
      stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
      return stateListDrawable;
    }
  }

  public static Drawable createSimpleSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
    Resources resources = context.getResources();
    Drawable defaultDrawable = resources.getDrawable(resource).mutate();
    if (defaultColor != 0) {
      defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
    }
    Drawable pressedDrawable = resources.getDrawable(resource).mutate();
    if (pressedColor != 0) {
      pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
    }
    StateListDrawable stateListDrawable = new StateListDrawable();
//    stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
    stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressedDrawable);
    stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
    return stateListDrawable;
  }

  public static void disableShape(float[] floats, int flag) {
    if (floats == null || floats.length != 8) {
      return;
    }

    switch (flag) {
      case 1:
        floats[0] = floats[1] = floats[2] = floats[3] = 0;
        break;
      case 2:
        floats[4] = floats[5] = floats[6] = floats[7] = 0;
        break;
      case 4:
        floats[0] = floats[1] = floats[6] = floats[7] = 0;
        break;
      case 8:
        floats[2] = floats[3] = floats[4] = floats[5] = 0;
        break;
      case 3:
        floats[0] = floats[1] = floats[2] = floats[3] = floats[4] = floats[5] = floats[6] = floats[7] = 0;
        break;
      case 5:
        floats[0] = floats[1] = floats[2] = floats[3] = floats[6] = floats[7] = 0;
        break;
      case 9:
        floats[0] = floats[1] = floats[2] = floats[3] = floats[4] = floats[5] = 0;
        break;
      case 6:
        floats[0] = floats[1] = floats[4] = floats[5] = floats[6] = floats[7] = 0;
        break;
      case 10:
        floats[2] = floats[3] = floats[4] = floats[5] = floats[6] = floats[7] = 0;
        break;
      case 12:
        floats[0] = floats[1] = floats[2] = floats[3] = floats[4] = floats[5] = floats[6] = floats[7] = 0;
        break;
    }
  }

  public static SelectorBuilder initializeStyle(Context context, AttributeSet attrs, int defStyleAttr) {
    SelectorBuilder builder = new SelectorBuilder();

    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Theme, defStyleAttr, 0);
    if (array == null || array.getIndexCount() <= 0) {
      return null;
    }

    if (array.hasValue(R.styleable.Theme_t_background_color)) {
      builder.color(array.getColor(R.styleable.Theme_t_background_color, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_stroke_width)) {
      builder.strokeWidth(array.getDimension(R.styleable.Theme_t_stroke_width, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_stroke_color)) {
      builder.strokeColor(array.getColor(R.styleable.Theme_t_stroke_color, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_style)) {
      builder.style(array.getInteger(R.styleable.Theme_t_style, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_radii)) {
      builder.radii(array.getDimension(R.styleable.Theme_t_radii, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_selector_color)) {
      builder.pressedColor(array.getColor(R.styleable.Theme_t_selector_color, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_select_stroke_width)) {
      builder.pressedStrokeWidth(array.getDimension(R.styleable.Theme_t_select_stroke_width, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_select_stroke_color)) {
      builder.pressedStrokeColor(array.getColor(R.styleable.Theme_t_select_stroke_color, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_select_style)) {
      builder.pressedStyle(array.getInteger(R.styleable.Theme_t_select_style, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_select_radii)) {
      builder.pressedRadii(array.getDimension(R.styleable.Theme_t_select_radii, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_disable_shape)) {
      builder.disableShape(array.getInteger(R.styleable.Theme_t_disable_shape, 0));
    }
    if (array.hasValue(R.styleable.Theme_t_selectable)) {
      builder.selectable(array.getBoolean(R.styleable.Theme_t_selectable, false));
    }
    if (array.hasValue(R.styleable.Theme_t_ripple)) {
      builder.simple(!array.getBoolean(R.styleable.Theme_t_ripple, true));
    }

    array.recycle();
    return builder;
  }

  public static class SelectorBuilder {
    public boolean simple;
    public boolean selectable = true;
    public int color;
    public int strokeColor;
    public float strokeWidth;
    public int style;
    public float[] radii;
    public int pressedColor;
    public int pressedStrokeColor;
    public float pressedStrokeWidth;
    public int pressedStyle;
    public float[] pressedRadii;

    public SelectorBuilder simple(boolean simple) {
      this.simple = simple;
      return this;
    }

    public SelectorBuilder selectable(boolean selectable) {
      this.selectable = selectable;
      return this;
    }

    public SelectorBuilder color(int color) {
      this.color = color;
      if (this.pressedColor == 0) {
        this.pressedColor = color;
      }
      return this;
    }

    public SelectorBuilder strokeColor(int strokeColor) {
      this.strokeColor = strokeColor;
      if (this.pressedStrokeColor == 0) {
        this.pressedStrokeColor = strokeColor;
      }
      return this;
    }

    public SelectorBuilder strokeWidth(float strokeWidth) {
      this.strokeWidth = strokeWidth;
      if (this.pressedStrokeWidth == 0) {
        this.pressedStrokeWidth = strokeWidth;
      }
      return this;
    }

    public SelectorBuilder style(int style) {
      this.style = style;
      if (this.pressedStyle == NONE) {
        this.pressedStyle = style;
      }
      return this;
    }

    public SelectorBuilder radii(float ...radii) {
      this.radii = radii.length == 8 ? radii : radii.length == 4 ? new float[] {radii[0], radii[0], radii[1], radii[1], radii[2], radii[2], radii[3], radii[3]} : new float[]{radii[0], radii[0], radii[0], radii[0], radii[0], radii[0], radii[0], radii[0]};
      if (this.pressedRadii == null || pressedRadii.length == 0) {
        this.pressedRadii = this.radii;
      }
      return this;
    }

    public SelectorBuilder pressedColor(int pressedColor) {
      this.pressedColor = pressedColor;
      return this;
    }

    public SelectorBuilder pressedStrokeColor(int pressedStrokeColor) {
      this.pressedStrokeColor = pressedStrokeColor;
      return this;
    }

    public SelectorBuilder pressedStrokeWidth(float pressedStrokeWidth) {
      this.pressedStrokeWidth = pressedStrokeWidth;
      return this;
    }

    public SelectorBuilder pressedStyle(int pressedStyle) {
      this.pressedStyle = pressedStyle;
      return this;
    }

    public SelectorBuilder pressedRadii(float ...pressedRadii) {
      this.pressedRadii = pressedRadii.length == 8 ? pressedRadii : new float[]{pressedRadii[0], pressedRadii[0], pressedRadii[0], pressedRadii[0], pressedRadii[0], pressedRadii[0], pressedRadii[0], pressedRadii[0]};
      return this;
    }

    public SelectorBuilder disableShape(int noneShape) {
      Theme.disableShape(radii, noneShape);
      Theme.disableShape(pressedRadii, noneShape);
      return this;
    }
  }

  public static void destroy() {
    instance = false;
  }
}
