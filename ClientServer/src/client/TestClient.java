/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas
 */
public class TestClient {

    private static final int port = 1234;
    private static String hostname = "localhost";

    public static Object sendRequest(Object requestObject) throws UnknownHostException, IOException, ClassNotFoundException {
        InetAddress addr = InetAddress.getByName(hostname);
        Socket socket = new Socket(addr, port);

        //Gegenseitig Streams öffnen
        //Hier (Client) zuerst OutputStream - beim Server zuerst InputStream
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        InputStream is = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);

        oos.writeObject(requestObject);
        Object responseObj = ois.readObject();



        //Wenn äußere schließe z. B. oos wird innere automatisch geschlossen z. B. os
        oos.close();
        ois.close();

        return responseObj;
    }

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 10; i++) {
                //JVM_Bind Exception wenn z. B. Server 2 mal starten
                String response = TestClient.sendRequest("request one").toString();
                System.out.println(response);
                //Thread.sleep(500);
            }
        } catch (Exception ex) {
            System.out.println("");
        }
    }
}
