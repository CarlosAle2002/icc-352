package org.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long idUsuario;
    private String contenido;
    private boolean isFromServer;

    // Constructor
    public Mensaje(Long idConeccion, String contenido, boolean isFromServer) {
        this.idUsuario = idConeccion;
        this.contenido = contenido;
        this.isFromServer = isFromServer;
    }

    public Mensaje() {

    }


    public Long getIdConeccion() {
        return idUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public boolean isFromServer() {
        return isFromServer;
    }

    // Setters
    public void setIdConeccion(Long idConeccion) {
        this.idUsuario = idConeccion;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }
}
