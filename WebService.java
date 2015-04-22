/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservicewg;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import webservicewg.model.FlatShare;

/**
 *
 * @author Thomas
 */
public class WebServiceWG {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
    HttpServer server = null;
    try {
      //Session session = HibernateUtil.getSessionFactory().openSession();
      server = HttpServerFactory.create( "http://localhost:8080/rest" );
      server.start();
      
      //server.stop( 0 );
    } catch (IOException | IllegalArgumentException ex) {
      Logger.getLogger(WebServiceWG.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
   
    }
  }
}
