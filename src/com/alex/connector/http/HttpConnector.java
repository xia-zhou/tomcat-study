package com.alex.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhangsong on 15/8/17.
 */
public class HttpConnector implements Runnable{
    private boolean stopped = false;
    private String scheme = "http";

    public String getScheme() {
        return scheme;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080,1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stopped){
            Socket socket = null;
            try {
               socket = serverSocket.accept();
            } catch (IOException e) {
                continue;
            }
            HttpProcess httpProcess = new HttpProcess(this);
            httpProcess.process(socket);
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

}
