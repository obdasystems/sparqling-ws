package com.obdasystems.server;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.swing.*;

import com.obdasystems.swing.ConsoleAppender;
import com.obdasystems.swing.TextAreaOutputStream;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlingServer {

    private static Server server;
    private static PrintStream stdout = System.out;
    private static PrintStream stderr = System.err;
    static int port = 7979;
    public static ConsoleAppender ca;

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> createAndShowGUI());

        server = new Server(port);
        HandlerList handlers = new HandlerList();
//		MAIN HANDLER FOR REST
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/sparqling/1.0.0/");
        FilterHolder cors = ctx.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                "origin, access-control-allow-origin, content-type, accept, authorization, x-requested-with");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages", "io.swagger.v3.jaxrs2.integration.resources, com.obdasystems.sparqling.api");
        serHol.setInitParameter("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature");
        serHol.setInitParameter("jersey.config.server.wadl.disableWadl", "true");
        serHol.setInitParameter("openApi.configuration.prettyPrint", "true");

        handlers.addHandler(ctx);

//		DEPLOY Sparling js
        if (args.length >= 1) {
            ContextHandler ch = new ContextHandler("/ui");
            ResourceHandler rh = new ResourceHandler();
            rh.setResourceBase(args[0]);
            rh.setWelcomeFiles(new String[]{"index.html"});
            rh.setCacheControl("no-store");
            ch.setHandler(rh);
            handlers.addHandler(ch);
        }


        server.setHandler(handlers);
        server.start();

        if (args.length >= 1)
            openSparqling();

        server.join();
        System.out.println("****** EXIT ******");
    }

    private static void openSparqling() {
        if (Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:" + port + "/ui"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot open Sparqling!");
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Sparqling");
        List<Image> icons = new LinkedList<>();
        for (int i = 16; i <= 64; i *= 2) {
            Image icon = new ImageIcon(SparqlingServer.class.getResource("/icons/icon_" + i + "@1x.png")).getImage();
            icons.add(icon);
        }
        frame.setIconImages(icons);
        Container contentPane = frame.getContentPane();
        addComponentsToPane(contentPane);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    server.stop();
                    System.out.println("****** SERVER STOPPED ******");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private static void addComponentsToPane(Container pane) {

        JButton button;
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JTextPane ta = new JTextPane();
        ta.setEditable(false);
        ca = new ConsoleAppender(ta, 1000);
        ca.appendTitle("\n" +
                        "                                                      ___                            \n" +
                        "                                                     (   )  .-.                      \n" +
                        "    .--.       .-..     .---.   ___ .-.      .--.     | |  ( __)  ___ .-.     .--.   \n" +
                        "  /  _  \\     /    \\   / .-, \\ (   )   \\    /    \\    | |  (''\") (   )   \\   /    \\  \n" +
                        " . .' `. ;   ' .-,  ; (__) ; |  | ' .-. ;  |  .-. '   | |   | |   |  .-. .  ;  ,-. ' \n" +
                        " | '   | |   | |  . |   .'`  |  |  / (___) | |  | |   | |   | |   | |  | |  | |  | | \n" +
                        " _\\_`.(___)  | |  | |  / .'| |  | |        | |  | |   | |   | |   | |  | |  | |  | | \n" +
                        "(   ). '.    | |  | | | /  | |  | |        | |  | |   | |   | |   | |  | |  | |  | | \n" +
                        " | |  `\\ |   | |  ' | ; |  ; |  | |        | '  | |   | |   | |   | |  | |  | '  | | \n" +
                        " ; '._,' '   | `-'  ' ' `-'  |  | |        ' `-'  |   | |   | |   | |  | |  '  `-' | \n" +
                        "  '.___.'    | \\__.'  `.__.'_. (___)        `._ / |  (___) (___) (___)(___)  `.__. | \n" +
                        "             | |                                | |                          ( `-' ; \n" +
                        "            (___)                              (___)                          `.__.  \n" +
                        "\n" +
                        "Powered by OBDASystems https://obdasystems.com\n");

        button = new JButton("Clear");
        button.addActionListener(a -> ca.clear());
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("Open Sparqling");
        button.addActionListener(a -> openSparqling());
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(button, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(new JScrollPane(ta), c);

    }

}
