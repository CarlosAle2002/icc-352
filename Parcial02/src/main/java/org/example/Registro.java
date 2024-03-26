package org.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String nombre;
    private String Sector;
    private String NivelEscolar;
    private String UsuarioRegistrador;
    private float latitud;
    private float longitud;

    public Registro() {
        // Constructor vacío
    }

    public Registro(String nombre, String Sector, String NivelEscolar, String UsuarioRegistrador, float latitud, float longitud) {
        this.nombre = nombre;
        this.Sector = Sector;
        this.NivelEscolar = NivelEscolar;
        this.UsuarioRegistrador = UsuarioRegistrador;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return Sector;
    }

    public void setSector(String Sector) {
        this.Sector = Sector;
    }

    public String getNivelEscolar() {
        return NivelEscolar;
    }

    public void setNivelEscolar(String NivelEscolar) {
        this.NivelEscolar = NivelEscolar;
    }

    public String getIdUsuarioRegistrador() {
        return UsuarioRegistrador;
    }

    public void setIdUsuarioRegistrador(String UsuarioRegistrador) {
        this.UsuarioRegistrador = UsuarioRegistrador;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }
}
