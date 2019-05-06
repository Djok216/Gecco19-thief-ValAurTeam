package model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:aurelian.hreapca@gmail.com">Aurelian Hreapca</a> (created on 5/6/19)
 */
public class SparseMatrix<T> {

    private T defaultValue;
    private Map<Integer, Map<Integer, T>> lines = new HashMap<>();

    public SparseMatrix() {
    }

    public SparseMatrix(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T get(int line, int column) {
        Map<Integer, T> columns = lines.get(line);

        return columns == null ? defaultValue : columns.getOrDefault(column, defaultValue);
    }

    public void add(int line, int column, T value) {
        Map<Integer, T> columns = lines.get(line);

        if (columns == null) {
            columns = new HashMap<>();
            lines.put(line, columns);
        }

        columns.put(column, value);
    }
}
