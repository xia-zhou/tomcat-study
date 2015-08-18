package com.alex.connector.http;

import com.alex.connector.ServletProcess;
import com.alex.connector.StaticResourceProcess;
import com.sun.tools.internal.ws.processor.modeler.annotation.AnnotationProcessorContext;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by zhangsong on 15/8/17.
 */
public class HttpProcess {
    private HttpRequest request;
    private HttpResponse response;
    private HttpRequestLine requestLine = new HttpRequestLine();
    public HttpProcess(HttpConnector httpConnector){
    }

    public void process(Socket socket) {
        SocketInputStream socketInputStream = null;
        OutputStream outputStream = null;
        try {
            socketInputStream = new SocketInputStream(socket.getInputStream(),2048);
            outputStream = socket.getOutputStream();
            request  = new HttpRequest(socketInputStream);
            response = new HttpResponse(outputStream);
            response.setRequest(request);
            response.setHeader("Service", "Original Vampires");
            parseRequest(socketInputStream, outputStream);
            parseHeadder(socketInputStream);
            request.getParameterMap();
            if(request.getRequestURI().startsWith("/servlet/")){
                ServletProcess servletProcess = new ServletProcess();
                servletProcess.process(request,response);
            }else{
                StaticResourceProcess staticResourceProcess = new StaticResourceProcess();
                staticResourceProcess.process(request,response);
            }
            socket.close();
        } catch (IOException e) {

        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private void parseHeadder(SocketInputStream socketInputStream) throws IOException, ServletException {
        while (true) {
            HttpHeader header = new HttpHeader();;
            // Read the next header
            socketInputStream.readHeader(header);
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                }
                else {
                    throw new ServletException
                            ("httpProcessor.parseHeaders.colon");
                }
            }
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            request.addHeader(name, value);
            if (name.equals("cookie")) {
                Cookie cookies[] = parseCookieHeader(value);
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("jsessionid")) {
                        // Override anything requested in the URL
                        if (!request.isRequestedSessionIdFromCookie()) {
                            // Accept only the first session id cookie
                            request.setRequestedSessionId(cookies[i].getValue());
                            request.setRequestedSessionCookie(true);
                            request.setRequestedSessionURL(false);
                        }
                    }
                    request.addCookie(cookies[i]);
                }
            }
            else if (name.equals("content-length")) {
                int n = -1;
                try {
                    n = Integer.parseInt(value);
                }
                catch (Exception e) {
                    throw new ServletException("httpProcessor.parseHeaders.contentLength");
                }
                request.setContentLength(n);
            }
            else if (name.equals("content-type")) {
                request.setContentType(value);
            }
        }
    }
    public static Cookie[] parseCookieHeader(String header) {
        if ((header == null) || (header.length() < 1) )
            return (new Cookie[0]);

        ArrayList cookies = new ArrayList();
        while (header.length() > 0) {
            int semicolon = header.indexOf(';');
            if (semicolon < 0)
                semicolon = header.length();
            if (semicolon == 0)
                break;
            String token = header.substring(0, semicolon);
            if (semicolon < header.length())
                header = header.substring(semicolon + 1);
            else
                header = "";
            try {
                int equals = token.indexOf('=');
                if (equals > 0) {
                    String name = token.substring(0, equals).trim();
                    String value = token.substring(equals+1).trim();
                    cookies.add(new Cookie(name, value));
                }
            }
            catch (Throwable e) {
                ;
            }
        }
        return ((Cookie[]) cookies.toArray (new Cookie [cookies.size ()]));
    }
    private void parseRequest(SocketInputStream socketInputStream, OutputStream outputStream) throws IOException, ServletException {
        socketInputStream.readRequestLine(requestLine);
        String method = new String(requestLine.getMethod(),0,requestLine.getMethodEnd());
        String uri = null;
        new String(requestLine.getUri(),0,requestLine.getUriEnd());
        String protocol = new String(requestLine.getProtocol(),0,requestLine.getProtocolEnd());
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method"); }
        else if (requestLine.getUriEnd() < 1) {
            throw new ServletException("Missing HTTP request URI");
        }
        int question = requestLine.indexOf("?");
        if (question >= 0) {
            request.setQueryString(new String(requestLine.getUri(), question + 1, requestLine.getUriEnd() - question - 1));
            uri = new String(requestLine.getUri(), 0, question);
        }else {
            request.setQueryString(null);
            uri = new String(requestLine.getUri(), 0, requestLine.getUriEnd());
        }
        if(!uri.startsWith("/")){
            int pos = uri.indexOf("://");
            if(pos>-1){
                pos = uri.indexOf("/",pos+3);
                if(pos==-1){
                    uri = "";
                }else{
                    uri = uri.substring(pos);
                }
            }
        }

        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if(semicolon>=0){
            String rest = uri.substring(semicolon+match.length());
            int semicolon2 = rest.indexOf(";");
            if(semicolon2>=0){
                request.setRequestedSessionId(rest.substring(0,semicolon2));
                rest = rest.substring(semicolon2);
            }else{
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri = uri.substring(0,semicolon) + rest;
        }else{
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }
        String normalizedUri = normalize(uri);
        request.setMethod(method); request.setProtocol(protocol);
        if (normalizedUri != null) {
            request.setRequestURI(normalizedUri);
        }else {
            request.setRequestURI(uri);
        }
        if (normalizedUri == null) {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
    }
    protected String normalize(String path) {
        if (path == null)
            return null;
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.indexOf("%25") >= 0)
                || (normalized.indexOf("%2F") >= 0)
                || (normalized.indexOf("%2E") >= 0)
                || (normalized.indexOf("%5C") >= 0)
                || (normalized.indexOf("%2f") >= 0)
                || (normalized.indexOf("%2e") >= 0)
                || (normalized.indexOf("%5c") >= 0)) {
            return null;
        }

        if (normalized.equals("/."))
            return "/";

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null);  // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0)
            return (null);

        // Return the normalized path that we have completed
        return (normalized);

    }

}
