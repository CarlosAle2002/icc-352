package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ServiciosFoto {
    private static ServiciosFoto instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private ServiciosFoto() {
        // Constructor privado para evitar la creación de instancias fuera de la clase
    }

    public static ServiciosFoto getInstance() {
        if (instancia == null) {
            instancia = new ServiciosFoto();
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

    // CREAR FOTO
    public Foto crearFoto( String base64Foto, Long idUsuario) {
        Foto nuevaFoto = null;
        // Configurar y construir la sesión de Hibernate
        SessionFactory sessionFactory = buildSessionFactory();
        Session session = null;
        Transaction transaction = null;

        try {
            // Abrir una sesión de Hibernate
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear una nueva foto
            Foto foto = new Foto( base64Foto, idUsuario);
            nuevaFoto = foto;
            // Guardar la foto en la base de datos
            session.save(foto);

            // Confirmar la transacción
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                // Cerrar la sesión de Hibernate
                session.close();
            }
        }

        return nuevaFoto;
    }

    // OBTENER FOTOS POR ID DE USUARIO
    public List<Foto> obtenerFotosPorUsuario(Long idUsuario) {
        // Configurar y construir la sesión de Hibernate
        SessionFactory sessionFactory = buildSessionFactory();
        Session session = null;
        Transaction transaction = null;
        List<Foto> fotos = null;

        try {
            // Abrir una sesión de Hibernate
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear consulta HQL para obtener fotos por ID de usuario
            fotos = session.createQuery("FROM Foto WHERE idUsuario = :idUsuario", Foto.class)
                    .setParameter("idUsuario", idUsuario)
                    .list();

            // Confirmar la transacción
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                // Cerrar la sesión de Hibernate
                session.close();
            }
        }

        return fotos;
    }

    public Foto obtenerFotoPorIdUsuario(Long idUsuario) {
        // Configurar y construir la sesión de Hibernate
        SessionFactory sessionFactory = buildSessionFactory();
        Session session = null;
        Transaction transaction = null;
        Foto foto = null;

        try {
            // Abrir una sesión de Hibernate
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear consulta HQL para obtener la foto por ID de usuario
            foto = (Foto) session.createQuery("FROM Foto WHERE idUsuario = :idUsuario")
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();

            // Confirmar la transacción
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                // Cerrar la sesión de Hibernate
                session.close();
            }
        }

        return foto;
    }

}
