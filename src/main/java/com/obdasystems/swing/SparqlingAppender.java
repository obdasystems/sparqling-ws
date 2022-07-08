package com.obdasystems.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.obdasystems.server.SparqlingServer;

import java.io.IOException;


public class SparqlingAppender extends AppenderBase<ILoggingEvent> {

    private PatternLayoutEncoder encoder;

    @Override
    public void start() {
        if (this.encoder == null) {
            addError("No encoder set for the appender named ["+ name +"].");
            return;
        }

        try {
            encoder.init(System.out);
        } catch (IOException e) {
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        SparqlingServer.ca.append(encoder.getLayout().doLayout(iLoggingEvent), iLoggingEvent.getLevel().equals(Level.ERROR));
    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }
}
