package poizzy.railworks.library;

import java.util.List;

public class CircularList<T> {
    private final List<T> list;
    private int index = 0;

    public CircularList(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        this.list = list;
    }

    public T next() {
        T element = list.get(index);
        index = (index + 1) % list.size();
        return element;
    }

    public T last() {
        index = (index - 1 + list.size()) % list.size();
        return list.get(index);
    }

    public T peek() {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }
}
