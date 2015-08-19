package com.alex.connector.http;

import java.io.*;

/**
 * Created by zhangsong on 15/8/17.
 */
public class SocketInputStream extends InputStream {
    /**
     * CR.
     */
    private static final byte CR = (byte) '\r';


    /**
     * LF.
     */
    private static final byte LF = (byte) '\n';


    /**
     * SP.
     */
    private static final byte SP = (byte) ' ';


    /**
     * HT.
     */
    private static final byte HT = (byte) '\t';


    /**
     * COLON.
     */
    private static final byte COLON = (byte) ':';


    /**
     * Lower case offset.
     */
    private static final int LC_OFFSET = 'A' - 'a';


    /**
     * Internal buffer.
     */
    protected byte buf[];


    /**
     * Last valid byte.
     */
    protected int count;


    /**
     * Position in the buffer.
     */
    protected int pos;


    /**
     * Underlying input stream.
     */
    private InputStream inputStream;

    public SocketInputStream(InputStream inputStream, int size) {
        this.inputStream = inputStream;
        buf = new byte[size];
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count)
                return -1;
        }
        int a = buf[pos++]&0xff;
        return a;
    }
    protected void fill()
            throws IOException {
        pos = 0;
        count = 0;
        int nRead = inputStream.read(buf, 0, buf.length);
        if (nRead > 0) {
            count = nRead;
        }
    }
    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        if (requestLine.getMethodEnd() != 0) {
            requestLine.recycle();
        }
        int chr = 0;
        do {
            try {
                chr = read();
            }catch (IOException ex){
                chr = -1;
            }
        } while (chr == CR || chr == LF);

        if(chr==-1){
            throw new EOFException("HttpRequestLine has a exception");
        }
        pos--;
        int maxRead = requestLine.getMethod().length;
        int readStart = pos;
        int readCount = 0;

        boolean space = false;

        while (!space) {
            // if the buffer is full, extend it
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.getMethod(), 0, newBuffer, 0,
                            maxRead);
                    requestLine.setMethod(newBuffer);
                    maxRead = requestLine.getMethod().length;
                } else {
                    throw new IOException
                            ("requestStream.readline.toolong");
                }
            }
            // We're at the end of the internal buffer
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException
                            ("requestStream.readline.error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.getMethod()[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }

        requestLine.setMethodEnd(readCount - 1);

        // Reading URI

        maxRead = requestLine.getUri().length;
        readStart = pos;
        readCount = 0;
        space = false;
        boolean eol = false;
        while(!space){
            if(readCount>=maxRead){
                if((2*maxRead)<=HttpRequestLine.MAX_METHOD_SIZE){
                    char[] newBuffer = new char[2*maxRead];
                    System.arraycopy(requestLine.getMethod(),0,newBuffer,0,maxRead);
                    requestLine.setMethod(newBuffer);
                    maxRead = requestLine.getMethod().length;
                }else{
                    throw new IOException("Request.readLine too long");
                }
            }

            if(pos>=count){
                int val = read();
                if (val == -1)
                    throw new IOException
                            ("requestStream.readline.error");
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            } else if ((buf[pos] == CR) || (buf[pos] == LF)) {
                // HTTP/0.9 style request
                eol = true;
                space = true;
            }
            requestLine.getUri()[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }

        requestLine.setUriEnd(readCount - 1);

        maxRead = requestLine.getProtocol().length;
        readStart = pos;
        readCount = 0;

        while (!eol) {
            // if the buffer is full, extend it
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.getProtocol(), 0, newBuffer, 0,
                            maxRead);
                    requestLine.setProtocol(newBuffer);
                    maxRead = requestLine.getProtocol().length;
                } else {
                    throw new IOException
                            ("requestStream.readline.toolong");
                }
            }
            // We're at the end of the internal buffer
            if (pos >= count) {
                // Copying part (or all) of the internal buffer to the line
                // buffer
                int val = read();
                if (val == -1)
                    throw new IOException
                            ("requestStream.readline.error");
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == CR) {
                // Skip CR.
            } else if (buf[pos] == LF) {
                eol = true;
            } else {
                requestLine.getProtocol()[readCount] = (char) buf[pos];
                readCount++;
            }
            pos++;
        }
        requestLine.setProtocolEnd(readCount);
    }

    public void readHeader(HttpHeader header)
            throws IOException {

        // Recycling check
        if (header.nameEnd != 0)
            header.recycle();

        // Checking for a blank line
        int chr = read();
        if ((chr == CR) || (chr == LF)) { // Skipping CR
            if (chr == CR)
                read(); // Skipping LF
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        } else {
            pos--;
        }

        // Reading the header name

        int maxRead = header.name.length;
        int readStart = pos;
        int readCount = 0;

        boolean colon = false;

        while (!colon) {
            // if the buffer is full, extend it
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpHeader.MAX_NAME_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(header.name, 0, newBuffer, 0, maxRead);
                    header.name = newBuffer;
                    maxRead = header.name.length;
                } else {
                    throw new IOException
                            ("requestStream.readline.toolong");
                }
            }
            // We're at the end of the internal buffer
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException
                            ("requestStream.readline.error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == COLON) {
                colon = true;
            }
            char val = (char) buf[pos];
            if ((val >= 'A') && (val <= 'Z')) {
                val = (char) (val - LC_OFFSET);
            }
            header.name[readCount] = val;
            readCount++;
            pos++;
        }

        header.nameEnd = readCount - 1;

        // Reading the header value (which can be spanned over multiple lines)

        maxRead = header.value.length;
        readStart = pos;
        readCount = 0;

        int crPos = -2;

        boolean eol = false;
        boolean validLine = true;

        while (validLine) {

            boolean space = true;

            // Skipping spaces
            // Note : Only leading white spaces are removed. Trailing white
            // spaces are not.
            while (space) {
                // We're at the end of the internal buffer
                if (pos >= count) {
                    // Copying part (or all) of the internal buffer to the line
                    // buffer
                    int val = read();
                    if (val == -1)
                        throw new IOException
                                ("requestStream.readline.error");
                    pos = 0;
                    readStart = 0;
                }
                if ((buf[pos] == SP) || (buf[pos] == HT)) {
                    pos++;
                } else {
                    space = false;
                }
            }

            while (!eol) {
                // if the buffer is full, extend it
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException
                                ("requestStream.readline.toolong");
                    }
                }
                // We're at the end of the internal buffer
                if (pos >= count) {
                    // Copying part (or all) of the internal buffer to the line
                    // buffer
                    int val = read();
                    if (val == -1)
                        throw new IOException
                                ("requestStream.readline.error");
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == CR) {
                } else if (buf[pos] == LF) {
                    eol = true;
                } else {
                    // FIXME : Check if binary conversion is working fine
                    int ch = buf[pos] & 0xff;
                    header.value[readCount] = (char) ch;
                    readCount++;
                }
                pos++;
            }

            int nextChr = read();

            if ((nextChr != SP) && (nextChr != HT)) {
                pos--;
                validLine = false;
            } else {
                eol = false;
                // if the buffer is full, extend it
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException
                                ("requestStream.readline.toolong");
                    }
                }
                header.value[readCount] = ' ';
                readCount++;
            }

        }

        header.valueEnd = readCount;

    }
    /**
     * Returns the number of bytes that can be read from this input
     * stream without blocking.
     */
    public int available()
            throws IOException {
        return (count - pos) + inputStream.available();
    }


    /**
     * Close the input stream.
     */
    public void close()
            throws IOException {
        if (inputStream == null)
            return;
        inputStream.close();
        inputStream = null;
        buf = null;
    }

}
