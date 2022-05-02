package ir.accountbooklet.android.Customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class EditText extends AppCompatEditText implements TextWatcher {
    private Context context;
    private String currentText = "";
    private int backgroundColor;
    private int strokeColor;
    private int strokeSelectedColor;
    private float strokeWidth;
    private float strokeSelectedWidth;
    private int radius;
    private boolean isMany;
    private boolean showTitle;
    private boolean errorStatus;
    private EditTextListener listener;

    public interface EditTextListener {
        void onEditTextShowTitle(EditText editText, boolean show);
        void onEditTextChanged(EditText editText, String str, String oldStr, int len, int oldLen);
    }

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initializeStyle(attrs, defStyle);
        initialize();
    }

    private void initializeStyle(AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditText, defStyle, 0);
        isMany = a.getBoolean(R.styleable.EditText_et_text_many, false);
        backgroundColor = a.getColor(R.styleable.EditText_et_background_color, Color.TRANSPARENT);
        strokeColor = a.getColor(R.styleable.EditText_et_stroke_color, Theme.getColor(Theme.key_app_edit_text_stroke));
        strokeSelectedColor = a.getColor(R.styleable.EditText_et_stroke_selected_color, Theme.getColor(Theme.key_app_text3));
        strokeWidth = a.getDimensionPixelSize(R.styleable.EditText_et_stroke_width, AndroidUtilities.dp(1));
        strokeSelectedWidth = a.getDimensionPixelSize(R.styleable.EditText_et_stroke_select_width, AndroidUtilities.dp(2));
        radius = a.getDimensionPixelSize(R.styleable.EditText_et_radius, AndroidUtilities.dp(7));
        a.recycle();
    }

    private void initialize() {
        setIncludeFontPadding(false);
        setTypeface(AndroidUtilities.IRANSans_FaNum);
        addTextChangedListener(this);
        setSelector();
    }

    public void setListener(EditTextListener listener) {
        this.listener = listener;
    }

    public void setMany(boolean value) {
        if (isMany == value) {
            return;
        }
        isMany = value;
    }

    private void setSelector() {
        int[] padding = {getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom()};
        if (getBackground() != null) {
            setBackground(getListDrawable(Theme.createDrawable(backgroundColor, strokeColor, strokeWidth, Theme.RECT, radius), Theme.createDrawable(backgroundColor, strokeSelectedColor, strokeSelectedWidth, Theme.RECT, radius)));
        }
        setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    private Drawable getListDrawable(Drawable normal, Drawable focused) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled }, normal);
        drawable.addState(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_enabled }, normal);
        drawable.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
        drawable.addState(new int[] { android.R.attr.state_pressed }, focused);
        drawable.addState(new int[] { android.R.attr.state_enabled }, normal);
        drawable.addState(new int[] { android.R.attr.state_focused }, focused);
        return drawable;
    }

    public void setErrorStatus() {
        errorStatus = true;
        setBackground(Theme.createDrawable(backgroundColor, Theme.getColor(Theme.key_app_accent), strokeSelectedWidth, Theme.RECT, radius));
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(currentText);
    }

    @NonNull
    public String getString() {
        Editable editable = getText();
        return editable != null ? editable.toString().trim() : "";
    }

    public int getInt() {
        try {
            return Integer.parseInt(getString().replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong() {
        try {
            return Long.parseLong(getString().replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        final String oldCurrentText = currentText;
        if (isMany && !editable.toString().equals(currentText)) {
            removeTextChangedListener(this);
            currentText = AndroidUtilities.formatPrice(getLong(), true);
            Editable befText = getText();
            setText(currentText);
            Editable aftText = getText();
            if (aftText != null && !aftText.toString().equals(currentText)) {
                setText(befText);
                currentText = befText != null ? befText.toString() : "";
            }
            setSelection(currentText.length());
            addTextChangedListener(this);
        } else {
            currentText = editable.toString();
        }
        if (errorStatus) {
            errorStatus = false;
            setSelector();
        }
        if (listener != null) {
            listener.onEditTextChanged(this, currentText, oldCurrentText, currentText.length(), oldCurrentText.length());
        }
        if (showTitle == isEmpty()) {
            showTitle = !isEmpty();
            if (listener != null) {
                listener.onEditTextShowTitle(this, showTitle);
            }
        }
    }
}
