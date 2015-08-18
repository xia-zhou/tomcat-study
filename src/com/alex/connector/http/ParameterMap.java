package com.alex.connector.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsong on 15/8/18.
 */
public class ParameterMap extends HashMap {
    public ParameterMap(){
        super();
    }
    public ParameterMap(int initialCapacity) {
        super(initialCapacity);
    }
    public ParameterMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public ParameterMap(Map map) {
        super(map);
    }
    private boolean locked = false;
    public boolean isLocked() {
        return (this.locked);

    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public void clear() {
        if (locked)
            throw new IllegalStateException("parameterMap.locked");
        super.clear();
    }
    public Object put(Object key, Object value) {
        if (locked)
            throw new IllegalStateException
                    ("parameterMap.locked");
        return (super.put(key, value));
    }
    public void putAll(Map map) {
        if (locked)
            throw new IllegalStateException
                    ("parameterMap.locked");
        super.putAll(map);
    }

    public Object remove(Object key) {
        if (locked)
            throw new IllegalStateException
                    ("parameterMap.locked");
        return (super.remove(key));
    }
}
