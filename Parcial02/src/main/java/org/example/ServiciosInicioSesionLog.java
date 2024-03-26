package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Date;

public class ServiciosInicioSesionLog {
    private static ServiciosInicioSesionLog instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private ServiciosInicioSesionLog() {}

    public static ServiciosInicioSesionLog getInstance() {
        if (instancia == null) {
            instancia = new ServiciosInicioSesionLog();
        }
        return instancia;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void registrarInicioSesion(String username) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            InicioSesionLog inicioSesionLog = new InicioSesionLog();
            inicioSesionLog.setUsername(username);
            inicioSesionLog.setFecha(new Date());

            session.save(inicioSesionLog);

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
    }
}
