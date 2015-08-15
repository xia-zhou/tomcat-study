package com.alex.response;

import com.alex.request.AlexRequest;
import com.alex.server.AlexServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zhangsong on 15/8/14.
 */
public class AlexResponse {
    private OutputStream outputStream;

    private AlexRequest alexRequest;

    public AlexResponse(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    public void setAlexRequest(AlexRequest alexRequest) {
        this.alexRequest = alexRequest;
    }

    public void sendStaticResource(){
        byte[] buffer = new byte[1024];
        FileInputStream fileInputStream = null;
        try {
            File file = new File(AlexServer.ROOT+alexRequest.getUri());
            if(file.exists()){
                fileInputStream = new FileInputStream(file);
                int ch = fileInputStream.read(buffer,0,1024);
                while(ch!=-1){
                    outputStream.write(buffer);
                    ch = fileInputStream.read(buffer,0,1024);
                }
            }else{
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                        "Content-Type: text/html\r\n" + "Content-Length: 23\r\n" + "\r\n" +
                        "<h1>File Not Found</h1>";
                outputStream.write(errorMessage.getBytes());
            }
        }catch (IOException e){

        }finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
