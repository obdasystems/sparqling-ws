package com.obdasystems.swing;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class TextAreaOutputStream
        extends OutputStream {

    private ConsoleAppender appender;
    private final boolean errors;

    public TextAreaOutputStream(ConsoleAppender appender, boolean errors) {
        this.appender = appender;
        this.errors = errors;
    }

    public synchronized void close() {
        appender = null;
    }

    public synchronized void flush() {
    }

    public synchronized void write(int val) {
        write(new byte[1], 0, 1);
    }

    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len), errors);
        }
    }

    static private String bytesToString(byte[] ba, int str, int len) {
        try {
            return new String(ba, str, len, "UTF-8");
        } catch (UnsupportedEncodingException thr) {
            return new String(ba, str, len);
        } // all JVMs are required to support UTF-8
    }
} /* END PUBLIC CLASS */