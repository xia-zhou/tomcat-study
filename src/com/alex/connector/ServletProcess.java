package com.alex.connector;

import com.alex.connector.http.HttpRequest;
import com.alex.connector.http.HttpRequestFacade;
import com.alex.connector.http.HttpResponse;
import com.alex.connector.http.HttpResponseFacade;
import com.alex.utils.Const;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by zhangsong on 15/8/17.
 */
public class ServletProcess {
    public void process(HttpRequest request,HttpResponse response){
        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader urlClassLoader = null;
        URL[] urls = new URL[1];
        URLStreamHandler urlStreamHandler = null;
        File file = new File(Const.ROOT);
        try {
            String repository = new URL("file",null,file.getCanonicalPath()+File.separator).toString();
            urls[0] = new URL(null,repository,urlStreamHandler);
            urlClassLoader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class myClass = null;
        try {
            myClass = urlClassLoader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service(new HttpRequestFacade(request),new HttpResponseFacade(response));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
