package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ServiciosUsuario {
    private static ServiciosUsuario instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        usuarioActivo = usuario;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    private ServiciosUsuario() {}

    public static ServiciosUsuario getInstance() {
        if (instancia == null) {
            instancia = new ServiciosUsuario();
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

    public Usuario guardarUsuario(Usuario usuario) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(usuario);

            transaction.commit();
            return usuario;
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

    public List<Usuario> obtenerTodosLosUsuarios() {
        Session session = null;
        Transaction transaction = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            usuarios = session.createQuery("FROM Usuario", Usuario.class).list();

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

        return usuarios;
    }

    public Usuario obtenerUsuarioPorId(long id) {
        Session session = null;
        Transaction transaction = null;
        Usuario usuario = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            usuario = session.get(Usuario.class, id);

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

        return usuario;
    }

    public void eliminarUsuarioPorId(long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Usuario usuario = session.get(Usuario.class, id);
            if (usuario != null) {
                session.delete(usuario);
                System.out.println("Usuario eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún usuario con el ID proporcionado.");
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

    public void imprimirTodosLosUsuarios() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<Usuario> query = session.createQuery("from Usuario", Usuario.class);
            List<Usuario> usuarios = query.list();

            System.out.println("Lista de todos los usuarios:");
            for (Usuario usuario : usuarios) {
                System.out.println("ID: " + usuario.getId());
                System.out.println("Username: " + usuario.getUsername());
                System.out.println("Nombre: " + usuario.getNombre());
                System.out.println("Contrasena: " + usuario.getPassword());
                System.out.println("Rol: " + usuario.getRol());
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }






    public boolean existeUsuario(String username, String password) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE username = :username AND password = :password", Usuario.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            Usuario usuario = query.uniqueResult();

            if (usuario == null) {
                return false; // No se encontró ningún usuario
            }

            setUsuarioActivo(usuario); // Set the active user

            return true; // Se encontró al menos un usuario
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
