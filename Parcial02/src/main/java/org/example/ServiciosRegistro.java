package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class ServiciosRegistro {
    private static ServiciosRegistro instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private ServiciosRegistro() {}

    public static ServiciosRegistro getInstance() {
        if (instancia == null) {
            instancia = new ServiciosRegistro();
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

    public Registro guardarRegistro(Registro registro) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(registro);

            transaction.commit();
            return registro;
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
        return null;
    }

    public Registro obtenerRegistroPorId(long id) {
        Session session = null;
        Transaction transaction = null;
        Registro registro = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            registro = session.get(Registro.class, id);

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

        return registro;
    }

    public void eliminarRegistroPorId(long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Registro registro = session.get(Registro.class, id);
            if (registro != null) {
                session.delete(registro);
                System.out.println("Registro eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún registro con el ID proporcionado.");
            }

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


    public void imprimirTodosLosRegistros() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<Registro> query = session.createQuery("from Registro", Registro.class);
            List<Registro> registros = query.list();
            for (Registro registro : registros) {
                System.out.println("ID: " + registro.getId());
                System.out.println("Nombre: " + registro.getNombre());
                System.out.println("Sector: " + registro.getSector());
                System.out.println("Nivel Escolar: " + registro.getNivelEscolar());
                System.out.println("Registrador: " + registro.getIdUsuarioRegistrador());
                System.out.println("Latitud: " + registro.getLatitud());
                System.out.println("Longitud: " + registro.getLongitud());
                System.out.println("-----------------------------");
                System.out.println("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public List<Registro> obtenerTodosLosRegistros() {
        Session session = null;
        List<Registro> registros = null;
        try {
            session = sessionFactory.openSession();
            Query<Registro> query = session.createQuery("from Registro", Registro.class);
            registros = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return registros;
    }
}
