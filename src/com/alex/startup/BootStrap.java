package com.alex.startup;

import com.alex.core.SimpleContainer;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.http.HttpConnector;

import java.io.IOException;

/**
 * Created by zhangsong on 15/8/17.
 */
public class BootStrap {
    public static void main(String[] args) {
        // 全局启动容器
        HttpConnector httpConnector = new HttpConnector();
        Container container = new SimpleContainer();
        httpConnector.setContainer(container);
        try {
            httpConnector.initialize();
            httpConnector.start();
            System.in.read();
        } catch (LifecycleException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
