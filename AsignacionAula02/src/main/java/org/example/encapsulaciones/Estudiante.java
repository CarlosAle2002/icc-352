package org.example.encapsulaciones;

import java.util.Objects;

public class Estudiante {
    private int matricula;
    private String nombre;
    private String carrera;

    public Estudiante() {
    }

    public Estudiante(int matricula, String nombre, String carrera) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.carrera = carrera;
    }

    public int getMatricula() {
        return this.matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarrera() {
        return this.carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public void mezclar(Estudiante e) {
        this.matricula = e.getMatricula();
        this.nombre = e.getNombre();
        this.carrera = e.getCarrera();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Estudiante that = (Estudiante)o;
            return this.matricula == that.matricula;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.matricula});
    }

    public String toString() {
        return "Estudiante{matricula=" + this.matricula + ", nombre='" + this.nombre + "', carrera='" + this.carrera + "'}";
    }
}

