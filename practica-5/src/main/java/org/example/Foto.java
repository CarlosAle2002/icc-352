package org.example;

import javax.persistence.*;

@Entity
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 300000) // Ajusta la longitud según tus necesidades
    private String base64Foto;
    private Long idUsuario;

    // Constructor
    public Foto( String base64Foto, Long idUsuario) {

        this.base64Foto = base64Foto;
        this.idUsuario = idUsuario;
    }

    public Foto() {

    }

    // Getters y Setters
    public Long getId() {
        return id;
    }


    public String getBase64Foto() {
        return base64Foto;
    }

    public void setBase64Foto(String base64Foto) {
        this.base64Foto = base64Foto;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Método toString para imprimir información de la foto
    @Override
    public String toString() {
        return "Foto{" +
                "id=" + id +
                ", base64Foto='" + base64Foto + '\'' +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
