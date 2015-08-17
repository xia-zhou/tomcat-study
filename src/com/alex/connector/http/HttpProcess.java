package com.alex.connector.http;

import com.alex.connector.ServletProcess;
import com.alex.connector.StaticResourceProcess;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by zhangsong on 15/8/17.
 */
public class HttpProcess {
    private HttpRequest request;
    private HttpResponse response;
    public HttpProcess(HttpConnector httpConnector){
    }

    public void process(Socket socket){
        SocketInputStream socketInputStream = null;
        OutputStream outputStream = null;
        try {
            socketInputStream = new SocketInputStream(socket.getInputStream(),2048);
            outputStream = socket.getOutputStream();
            request  = new HttpRequest(socketInputStream);
            response = new HttpResponse(outputStream);
            response.setHttpRequest(request);
            response.setHeader("Service", "Original Vampires");
            parseRequest(socketInputStream, outputStream);
            parseHeadder(socketInputStream);
            if(request.getRequestURI().startsWith("/servlet/")){
                ServletProcess servletProcess = new ServletProcess();
                servletProcess.process(request,response);
            }else{
                StaticResourceProcess staticResourceProcess = new StaticResourceProcess();
                staticResourceProcess.process(request,response);
            }
            socket.close();
        } catch (IOException e) {

        }
    }

    private void parseHeadder(SocketInputStream socketInputStream) {

    }

    private void parseRequest(SocketInputStream socketInputStream, OutputStream outputStream) {

    }


}
