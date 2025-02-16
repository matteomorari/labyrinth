package it.unibs.pajc.labyrinth.core;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class BaseModel {
  protected EventListenerList list = new EventListenerList();

  public void addChangeListener(ChangeListener l) {
    list.add(ChangeListener.class, l);
  }

  public void removeChangeListener(ChangeListener l) {
    list.remove(ChangeListener.class, l);
  }

  protected void fireChangeListener() {
    ChangeEvent e = new ChangeEvent(this);

    Object[] listeners = list.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        ((ChangeListener) listeners[i + 1]).stateChanged(e);
      }
    }
  }
}
