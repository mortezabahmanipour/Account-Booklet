package ir.accountbooklet.android.Utils;

import android.util.SparseArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

public class NotificationCenter {

  private static volatile NotificationCenter Instance;

  public static NotificationCenter getInstance() {
    NotificationCenter localInstance = Instance;
    if (localInstance == null) {
      synchronized (NotificationCenter.class) {
        localInstance = Instance;
        if (localInstance == null) {
          Instance = localInstance = new NotificationCenter();
        }
      }
    }
    return localInstance;
  }

  private static int totalEvents = 1;
  public static final int dataChanged = totalEvents++;

  private final SparseArray<ArrayList<MessageControllerListener>> listeners = new SparseArray<>();

  public interface MessageControllerListener {
    void onNotificationReceived(int event, @NonNull Object... data);
  }

  private NotificationCenter() {

  }

  @UiThread
  public void postNotification(int event, Object... data) {
    ArrayList<MessageControllerListener> objects = listeners.get(event);
    if (objects != null && !objects.isEmpty()) {
      for (MessageControllerListener listener : objects) {
        listener.onNotificationReceived(event, data);
      }
    }
  }

  public void addListener(MessageControllerListener listener, int event) {
    ArrayList<MessageControllerListener> objects = listeners.get(event);
    if (objects == null) {
      listeners.put(event, objects = new ArrayList<>());
    }
    if (objects.contains(listener)) {
      return;
    }
    objects.add(listener);
  }

  public void removeListener(MessageControllerListener listener, int event) {
    ArrayList<MessageControllerListener> objects = listeners.get(event);
    if (objects != null) {
      objects.remove(listener);
    }
  }
}
