package com.alex.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by zhangsong on 15/8/14.
 */
public class AlexRequest {
    Logger logger = Logger.getLogger("AlexRequest");
    private String uri;

    private InputStream inputStream;

    public AlexRequest(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public void parse(){
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[2048];
        int i = 0;
        try {
            i = inputStream.read(buffer);
        }catch (IOException e){
            e.printStackTrace();
        }
        /*for(int j = 0;j<i;j++){
            stringBuilder = stringBuilder.append((char)buffer[j]);
        }*/
        stringBuilder = stringBuilder.append(new String(buffer,0,2048));
        String s = stringBuilder.toString();
        logger.info(s);
        uri = parseUri(s);

    }

    private String parseUri(String s) {
        int index=0,index2 = 0;
        if(s==null||s==""){
            return null;
        }else{
            index = s.indexOf(" ");
            if(index!=-1){
                index2 = s.indexOf(" ",index+1);
            }
            if(index2>index){
                return s.substring(index+1,index2);
            }
        }
        return null;
    }

    public String getUri(){
        return uri;
    }

}
