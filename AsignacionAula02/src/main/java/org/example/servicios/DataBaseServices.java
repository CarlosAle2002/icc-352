package org.example.servicios;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase con patrón Singleton
 * Created by vacax on 27/05/16.
 */
public class DataBaseServices {

    private static DataBaseServices instancia;
    private String URL = "jdbc:h2:tcp://localhost/~/mdb";

    /**
     *Implementando el patron Singleton
     */
    private  DataBaseServices(){
        registrarDriver();
    }

    /**
     * Retornando la instancia.
     * @return
     */
    public static DataBaseServices getInstancia(){
        if(instancia==null){
            instancia = new DataBaseServices();
        }
        return instancia;
    }

    /**
     * Metodo para el registro de driver de conexión.
     */
    private void registrarDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConexion() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, "sa", "");
        } catch (SQLException exception) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
        }
        return connection;
    }

    public void testConexion() {
        try {
            getConexion().close();
            System.out.println("Conexión realizado con exito!");
        } catch (SQLException exception) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
        }
    }


}

