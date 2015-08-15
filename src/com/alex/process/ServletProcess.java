package com.alex.process;

import com.alex.request.AlexRequest;
import com.alex.request.AlexRequestFacade;
import com.alex.response.AlexResponse;
import com.alex.response.AlexResponseFacade;
import com.alex.utils.Const;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by zhangsong on 15/8/15.
 */
public class ServletProcess {
    public void process(AlexRequest alexRequest,AlexResponse alexResponse){
        String uri = alexRequest.getUri();
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
            servlet.service(new AlexRequestFacade(alexRequest),new AlexResponseFacade(alexResponse));
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
