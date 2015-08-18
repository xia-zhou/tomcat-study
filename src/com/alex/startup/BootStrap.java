package com.alex.startup;

import com.alex.connector.http.HttpConnector;

/**
 * Created by zhangsong on 15/8/17.
 */
public class BootStrap {
    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.start();
    }
}
