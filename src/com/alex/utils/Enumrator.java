package com.alex.utils;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by zhangsong on 15/8/19.
 */
public class Enumrator<T> implements Enumeration<T> {


    private Iterator<T> iterator = null;

    public Enumrator(Iterator<T> iterator){
        super();
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}
