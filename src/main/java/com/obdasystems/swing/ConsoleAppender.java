package com.obdasystems.swing;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConsoleAppender implements Runnable {
    private final JTextPane textPane;
    private final int maxLines;                                                   // maximum lines allowed in text area
    private final LinkedList<Integer> lengths;                                                    // length of lines within text area
    private final List<String> values;                                                     // values waiting to be appended
    private final List<Boolean> errors;

    private int curLength;                                                  // length of current line
    private boolean clear;
    private boolean queue;

    public ConsoleAppender(JTextPane textarea, int maxlines) {
        textPane = textarea;
        maxLines = maxlines;
        lengths = new LinkedList<>();
        values = new ArrayList<>();
        errors = new ArrayList<>();

        curLength = 0;
        clear = false;
        queue = true;
    }

    public synchronized void append(String val, boolean error) {
        values.add(val);
        errors.add(error);
        if (queue) {
            queue = false;
            EventQueue.invokeLater(this);
        }
    }

    public synchronized void appendTitle(String val) {
        values.add(val);
        if (queue) {
            queue = false;
            EventQueue.invokeLater(this);
        }
    }

    public synchronized void clear() {
        clear = true;
        curLength = 0;
        lengths.clear();
        values.clear();
        errors.clear();
        if (queue) {
            queue = false;
            EventQueue.invokeLater(this);
        }
    }

    // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
    public synchronized void run() {
        if (clear) {
            textPane.setText("");
        }
        StyleContext sc = StyleContext.getDefaultStyleContext();
        StyledDocument doc = textPane.getStyledDocument();
        int len = doc.getLength();
        textPane.setCaretPosition(len);
        int index = 0;
        for (String val : values) {
            curLength += val.length();
            if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                if (lengths.size() >= maxLines) {
                    int start = 0;
                    int end = lengths.removeFirst();
                    try {
                        doc.remove(start, end - start);
                        doc.insertString(start, "", null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                lengths.addLast(curLength);
                curLength = 0;
            }
            Color color;
            if (errors.isEmpty()) color = new Color(91, 134, 229);
            else color = errors.get(index) ? Color.RED : Color.BLACK;
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "monospaced");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

            try {
                doc.insertString(doc.getLength(), val, aset);
            } catch (BadLocationException e) {
                System.out.println(val);
            }
            index++;
        }
        values.clear();
        errors.clear();
        clear = false;
        queue = true;
    }

    static private final String EOL1 = "\n";
    static private final String EOL2 = System.getProperty("line.separator", EOL1);
}