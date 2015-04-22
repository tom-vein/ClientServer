/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;

/**
 *
 * @author Thomas
 */
public class TestServer extends DefaultServer{

    public TestServer(int port) {
        super(port);
    }

    public TestServer() {
    }

    public TestServer(int port, JEditorPane logPane) {
        super(port, logPane);
    }

    @Override
    protected Object handleRequest(Object requestObj) {
        return "request " + requestObj.toString() + "  dispatched";
    }
    
    public static void main(String[] args) {
        TestServer ts = new TestServer() {

            @Override
            protected Object handleRequest(Object requestObj) {
                return "request obtained";
            }
        };
        ts.startServer();
        try {
            //Gilt nur für aktuellen Thread
            Thread.sleep(150000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DefaultServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        ts.stopServer();
    }
}
