package org.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class InicioSesionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private Date fecha;

    // Constructor por defecto
    public InicioSesionLog() {}

    // Constructor con parámetros
    public InicioSesionLog(String username, Date fecha) {
        this.username = username;
        this.fecha = fecha;
    }

    // Métodos getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    // Método para representar la información del objeto como cadena
    @Override
    public String toString() {
        return "InicioSesionLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
