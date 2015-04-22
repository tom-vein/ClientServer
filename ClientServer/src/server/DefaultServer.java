/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;

/**
 *
 * @author Thomas
 */
public abstract class DefaultServer {

    private final int port;
    private JEditorPane logPane;
    private ServerThread st;

    public DefaultServer(int port) {
        this.port = port;
    }

    public DefaultServer() {
        this.port = 1234;
    }

    public DefaultServer(int port, JEditorPane logPane) {
        this.port = port;
        this.logPane = logPane;
    }

    /**
     * Write log information to logPane or to output
     *
     * @param logText
     */
    private void log(String logText) {
        if (logPane != null) {
            logPane.setText(logPane + System.getProperty("line.separator") + logText);
        } else {
            System.out.println(logText);
        }
    }

    /**
     * start ServerThread if not already running
     */
    public void startServer() {
        if (st == null || !st.isAlive()) {
            st = new ServerThread();
        }
    }

    /**
     * stop ServerThread if running
     */
    public void stopServer() {
        if (st != null || st.isAlive()) {
            st.interrupt();
        }
    }

    /*
     * Create internal class ServerThread
     */
    class ServerThread extends Thread {

        private ServerSocket server;

        public ServerThread() {
            try {
                server = new ServerSocket(port);
                //accept wird alle 1000 milisekunden unterbrochen damit interrup zu überprüfen
                //um blockierende von accept zu unterbrechen
                //kann danach isInterrupted abfragen
                //Ohne dem kann man den ServerThread nicht beenden, ohne dass eine Anfrage vom Client kommt
                //Man würde bei server.accept hängen bleiben - auch wenn Thread.sleep() schon abgelaufen ist
                server.setSoTimeout(1000);
                this.setPriority(Thread.MIN_PRIORITY + 1);
                this.start();
                log("Server started on port: " + port);
            } catch (IOException ex) {
                log("Error starting server: " + ex.toString());
            }
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Socket socket = server.accept();
                    log("Connected with: " + socket.getRemoteSocketAddress().toString());
                    ClientCommunicationThread cct = new ClientCommunicationThread(socket);
                } catch (SocketTimeoutException ex) {
                } catch (IOException ex) {
                    log("Problem in accept: " + ex.toString());
                }
            }

            try {
                server.close();
            } catch (IOException ex) {
                log("Error when closing server: " + ex.toString());
            }
            log("Server done...");
        }
    }

    class ClientCommunicationThread extends Thread {

        private Socket socket;

        public ClientCommunicationThread(Socket socket) {
            this.socket = socket;
            //Norm_Priority mittlere priorität = 5
            this.setPriority(Thread.NORM_PRIORITY + 2);
            try {
                //Gibt client 5 sec Zeit für Objekt senden, dann wird geschlossen
                this.socket.setSoTimeout(5000);
            } catch (SocketException ex) {
                log("connection closed with: " + socket.getRemoteSocketAddress().toString());
            }

            this.start();
        }

        @Override
        public void run() {
            InputStream is = null;
            OutputStream os = null;
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;

            log("starting communication with: " + socket.getRemoteSocketAddress().toString());
            try {
                is = socket.getInputStream();
                ois = new ObjectInputStream(is);

                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);

                //readObject wartet solang bis Object kommt - ist blockierende methode wie accept

                Object requestObj = ois.readObject();
                Object responseObj = handleRequest(requestObj);
                oos.writeObject(responseObj);

                oos.close();
                ois.close();

                log("communication finished successfully with: " + socket.getRemoteSocketAddress().toString());
            } catch (ClassNotFoundException ex) {
                log("communication error with: " + socket.getRemoteSocketAddress().toString());
            } catch (IOException ex) {
                log("communication error with: " + socket.getRemoteSocketAddress().toString());
                log(ex.toString());
            } finally {
                try {
                    oos.close();
                    ois.close();
                } catch (IOException ex) {
                    log("communication error with: " + socket.getRemoteSocketAddress().toString());
                    log(ex.toString());
                }
            }
        }
    }

    protected abstract Object handleRequest(Object requestObj);

    public static void main(String[] args) {
        DefaultServer ds = new DefaultServer(1234) {
            @Override
            protected Object handleRequest(Object requestObj) {
                return "request obtained";
            }
        };
        ds.startServer();
        try {
            //Gilt nur für aktuellen Thread
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DefaultServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.stopServer();
    }
}
