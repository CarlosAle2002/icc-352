package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ServiciosMensaje {
    private static ServiciosMensaje instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private ServiciosMensaje() {
        // Constructor privado para evitar la creación de instancias fuera de la clase
    }

    public static ServiciosMensaje getInstance() {
        if (instancia == null) {
            instancia = new ServiciosMensaje();
        }
        return instancia;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Crear la SessionFactory a partir del archivo de configuración hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // En caso de error, imprimir el mensaje y lanzar una excepción
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Mensaje enviarMensaje(Long idUsuario, String contenido, boolean isFromServer) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = null;
        Transaction transaction = null;
        Mensaje mensaje = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear un nuevo mensaje
            mensaje = new Mensaje(idUsuario, contenido, isFromServer);
            session.save(mensaje);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return mensaje;
    }

    public List<Mensaje> obtenerMensajes() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = null;
        List<Mensaje> mensajes = null;

        try {
            session = sessionFactory.openSession();
            mensajes = session.createQuery("FROM Mensaje", Mensaje.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return mensajes;
    }

    public List<Mensaje> obtenerMensajesPorUsuario(Long idUsuario) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = null;
        List<Mensaje> mensajes = null;

        try {
            session = sessionFactory.openSession();
            // Crear consulta HQL para obtener los mensajes del usuario dado
            mensajes = session.createQuery("FROM Mensaje WHERE idUsuario = :idUsuario", Mensaje.class)
                    .setParameter("idUsuario", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return mensajes;
    }


    public void imprimirMensajesPorUsuario(Long idUsuario) {
        List<Mensaje> mensajesUsuario = obtenerMensajesPorUsuario(idUsuario);

        if (mensajesUsuario == null || mensajesUsuario.isEmpty()) {
            System.out.println("No hay mensajes para el usuario con ID: " + idUsuario);
            return;
        }

        System.out.println("Mensajes para el usuario con ID: " + idUsuario);
        for (Mensaje mensaje : mensajesUsuario) {
            System.out.println("ID: " + mensaje.getIdConeccion() +
                    ", Contenido: " + mensaje.getContenido() +
                    ", De: " + (mensaje.isFromServer() ? "Servidor" : "Usuario"));
        }
    }

}
