package org.example.servicios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.example.encapsulaciones.Estudiante;
import org.example.util.NoExisteEstudianteException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EstudianteServices {
    private static EstudianteServices instancia;
    private List<Estudiante> listaEstudiante = new ArrayList();

    private EstudianteServices() {
        this.listaEstudiante.add(new Estudiante(20011136, "Carlos Camacho", "ITT"));
    }

    public static EstudianteServices getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteServices();
        }

        return instancia;
    }

    public List<Estudiante> listarEstudiante() {
        List<Estudiante> estudiantes = new ArrayList<>();
        Connection conexion = null;
        try {

            String query = "select * from estudiante";
            conexion = DataBaseServices.getInstancia().getConexion();
            PreparedStatement prepareStatement = conexion.prepareStatement(query);
            ResultSet resultados = prepareStatement.executeQuery();
            while(resultados.next()){
                int matricula = resultados.getInt("matricula");
                String nombre = resultados.getString("nombre");
                String carrera = resultados.getString("carrera");
                Estudiante estudiante = new Estudiante(matricula,nombre,carrera);
                estudiantes.add(estudiante);
            }

        } catch (SQLException exception) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
        } finally{
            try {
                conexion.close();
            } catch (SQLException exception) {
                Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
            }
        }

        return estudiantes;
    }

    public Estudiante getEstudiantePorMatricula(int matricula) {
        Estudiante estudiante = null;
        Connection conexion = null;
        try {

            String query = "select * from estudiante where matricula = ?";
            conexion = DataBaseServices.getInstancia().getConexion();
            PreparedStatement prepareStatement = conexion.prepareStatement(query);
            prepareStatement.setInt(1, matricula);
            ResultSet resultados = prepareStatement.executeQuery();
            while(resultados.next()){
                int matriculaa = resultados.getInt("matricula");
                String nombre = resultados.getString("nombre");
                String carrera = resultados.getString("carrera");
                estudiante = new Estudiante(matriculaa,nombre,carrera);
            }

        } catch (SQLException exception) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
        } finally{
            try {
                conexion.close();
            } catch (SQLException exception) {
                Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
            }
        }

        return estudiante;
    }

    public boolean crearEstudiante(Estudiante estudiante) {
        boolean prueba =false;
        Connection conexion = null;

        if (this.getEstudiantePorMatricula(estudiante.getMatricula()) != null) {
            System.out.println("Estudiante registrado!");
            return prueba;
        } else {
            try {

                String query = "insert into estudiante(matricula, nombre, carrera) values(?,?,?)";
                conexion = DataBaseServices.getInstancia().getConexion();
                PreparedStatement prepareStatement = conexion.prepareStatement(query);
                prepareStatement.setInt(1, estudiante.getMatricula());
                prepareStatement.setString(2, estudiante.getNombre());
                prepareStatement.setString(3, estudiante.getCarrera());
                int fila = prepareStatement.executeUpdate();
                prueba = fila > 0 ;

            } catch (SQLException exception) {
                Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
            } finally{
                try {
                    conexion.close();
                } catch (SQLException exception) {
                    Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
        }

        return prueba;
    }

    public boolean actualizarEstudiante(Estudiante estudiante) {
        boolean prueba =false;

        Connection conexion = null;
        Estudiante temporal = this.getEstudiantePorMatricula(estudiante.getMatricula());
        if (temporal == null) {
            throw new NoExisteEstudianteException("No Existe el estudiante: " + estudiante.getMatricula());
        } else {
            try {

                String query = "update estudiante set nombre=?, carrera=? where matricula = ?";
                conexion = DataBaseServices.getInstancia().getConexion();
                PreparedStatement prepareStatement = conexion.prepareStatement(query);
                prepareStatement.setString(1, estudiante.getNombre());
                prepareStatement.setString(2, estudiante.getCarrera());
                prepareStatement.setInt(3, estudiante.getMatricula());

                int fila = prepareStatement.executeUpdate();
                prueba = fila > 0 ;

            } catch (SQLException exception) {
                Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
            } finally{
                try {
                    conexion.close();
                } catch (SQLException exception) {
                    Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
                }
            }
            temporal.mezclar(estudiante);
            return prueba;
        }
    }

    public boolean eliminandoEstudiante(int matricula) {
        boolean prueba =false;

        Connection conexion = null;
        try {

            String query = "delete from estudiante where matricula = ?";
            conexion = DataBaseServices.getInstancia().getConexion();
            PreparedStatement prepareStatement = conexion.prepareStatement(query);
            prepareStatement.setInt(1, matricula);
            int fila = prepareStatement.executeUpdate();
            prueba = fila > 0 ;

        } catch (SQLException exception) {
            Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
        } finally{
            try {
                conexion.close();
            } catch (SQLException exception) {
                Logger.getLogger(EstudianteServices.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
        return prueba;
    }
}