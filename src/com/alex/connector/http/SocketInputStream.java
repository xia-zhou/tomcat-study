package com.alex.connector.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangsong on 15/8/17.
 */
public class SocketInputStream extends InputStream{
    public SocketInputStream(InputStream inputStream,int size){

    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
