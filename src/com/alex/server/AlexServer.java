package com.alex.server;

import com.alex.process.ServletProcess;
import com.alex.process.StaticResourceProcess;
import com.alex.request.AlexRequest;
import com.alex.response.AlexResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhangsong on 15/8/14.
 */
public class AlexServer {
    public static final String ROOT = System.getProperty("user.dir") + File.separator + "webroot";
    private String SHUTDOWN = "/shutdown";
    private boolean shutdown = false;

    public static void main(String[] args) {
        AlexServer alexServer = new AlexServer();
        alexServer.await();
    }
    public void await(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080,1, InetAddress.getByName("127.0.0.1"));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!shutdown){
            Socket socket = null;
            InputStream inputStream;
            OutputStream outputStream;
            if(serverSocket!=null){
                try {
                    socket = serverSocket.accept();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    AlexRequest alexRequest = new AlexRequest(inputStream);
                    alexRequest.parse();
                    AlexResponse alexResponse = new AlexResponse(outputStream);
                    alexResponse.setAlexRequest(alexRequest);
                    if(alexRequest.getUri().startsWith("/servlet/")){
                        ServletProcess servletProcess = new ServletProcess();
                        servletProcess.process(alexRequest,alexResponse);
                    }else{
                        StaticResourceProcess staticResourceProcess = new StaticResourceProcess();
                        staticResourceProcess.process(alexRequest,alexResponse);
                    }
                    //alexResponse.sendStaticResource();
                    socket.close();
                    shutdown = alexRequest.getUri().equals(SHUTDOWN);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }
    }

}
