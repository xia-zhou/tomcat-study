package com.alex.request;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by zhangsong on 15/8/14.
 */
public class AlexRequest implements ServletRequest{
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

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }
}
