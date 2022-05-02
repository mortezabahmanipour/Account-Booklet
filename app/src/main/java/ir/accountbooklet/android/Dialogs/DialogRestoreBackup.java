package ir.accountbooklet.android.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.accountbooklet.android.Customs.RecyclerListView;
import ir.accountbooklet.android.Customs.Row.RestoresRow;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.FileUtils;
import ir.accountbooklet.android.Utils.Theme;

public class DialogRestoreBackup extends BaseDialog implements View.OnClickListener, RecyclerListView.RecyclerListViewListener, RecyclerListView.RecyclerListViewClickListener {
  private ViewGroup baseLayout;
  private SelectorImageView ivClose;
  private RecyclerListView recyclerListView;
  private RestoresAdapter adapter;
  private List<RecyclerListView.BaseModel> restores = new ArrayList<>();
  private RestoreBackupListener listener;

  public static class RestoreModel extends RecyclerListView.BaseModel {
    public String name;
    public String path;

    public RestoreModel(String name, String path) {
      this.name = name;
      this.path = path;
    }
  }

  private static class RestoresAdapter extends RecyclerListView.BaseAdapter<RecyclerListView.BaseModel> {
    public RestoresAdapter(Context context, List<RecyclerListView.BaseModel> list) {
      super(context, list);
    }

    @Override
    public @NonNull
    RecyclerListView.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
      return new RecyclerListView.Holder(new RestoresRow(context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListView.Holder holder, int position) {
      ((RestoresRow) holder.itemView).setModel((RestoreModel) list.get(position));
    }
  }

  public interface RestoreBackupListener {
    void onRestore(RestoreModel restore);
  }

  public static DialogRestoreBackup newInstance(Context context) {
    return new DialogRestoreBackup(context);
  }

  private DialogRestoreBackup(Context context) {
    super(context);
  }

  public DialogRestoreBackup setListener(RestoreBackupListener listener) {
    this.listener = listener;
    return this;
  }

  private void initializeViews() {
    baseLayout = findViewById(R.id.baseLayout);
    ivClose = findViewById(R.id.ivClose);
    recyclerListView = findViewById(R.id.recyclerListView);
  }

  private void initializeDefaults() {
    baseLayout.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(7)));
  }

  private void initializeRecyclerView() {
    recyclerListView.addItemDecoration(new RecyclerListView.Decoration(AndroidUtilities.dp(10)) {
      @Override
      protected void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state, int position, int count) {
        if (position > 0) {
          outRect.top = space;
        }
      }
    });
    recyclerListView.setAdapter(adapter = new RestoresAdapter(context, restores));
  }

  private void initializeListeners() {
    ivClose.setOnClickListener(this);
    recyclerListView.setRecyclerListViewListener(this);
    recyclerListView.setRecyclerListViewClickListener(this);
  }

  private void setInfo() {
    new Thread(() -> {
      File[] restoreFiles = new File(FileUtils.BACKUP_PATH).listFiles();
      if (restoreFiles == null || restoreFiles.length <= 0) {
        AndroidUtilities.runOnUIThread(() -> {
          AndroidUtilities.toast(context, R.string.not_found_restore_backup);
          dismiss();
        });
        return;
      }
      Arrays.sort(restoreFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
      for(File file : restoreFiles) {
        restores.add(new RestoreModel(file.getName(), file.getPath()));
      }
      AndroidUtilities.runOnUIThread(() -> adapter.notifyDataSetChanged());
    }).start();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_restore);
    initializeViews();
    initializeDefaults();
    initializeRecyclerView();
    initializeListeners();
    setInfo();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.ivClose) {
      dismiss();
    }
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
    if (listener != null) {
      listener.onRestore((RestoreModel) restores.get(position));
    }
    dismiss();
  }

  @Override
  public void onRecyclerListViewPositionLongClicked(RecyclerView recyclerView, int position) {

  }
}
