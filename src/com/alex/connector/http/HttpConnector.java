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
            //  监听本地 8080 端口
            serverSocket = new ServerSocket(8080,1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stopped){
            Socket socket = null;
            try {
                // 接收到请求的 socket
                socket = serverSocket.accept();
            } catch (IOException e) {
                continue;
            }
            // 创建处理类
            HttpProcess httpProcess = new HttpProcess();
            // 处理类，根据socket 处理请求
            httpProcess.process(socket);
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

}
