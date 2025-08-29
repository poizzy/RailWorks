package poizzy.railworks.library;

import cam72cam.mod.event.ClientEvents;
import cam72cam.mod.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MouseHelper {
    public static final List<MouseEvent> listeners = new CopyOnWriteArrayList<>();

    public static void addListener(MouseEvent listener) {
        listeners.add(listener);
    }

    public static void removeListener(MouseEvent listener) {
        listeners.remove(listener);
    }

    public static void onMouseEvent(ClientEvents.MouseGuiEvent event) {
        for (MouseEvent listener : listeners) {
            listener.onMouseEvent(event);
        }
    }

    public interface MouseEvent {
        void onMouseEvent(ClientEvents.MouseGuiEvent event);
    }
}
