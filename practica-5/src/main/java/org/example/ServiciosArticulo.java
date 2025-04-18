package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiciosArticulo {
    private static ServiciosArticulo instancia;
    private static final SessionFactory sessionFactory = buildSessionFactory();


    private Articulo articuloAct;
    public void SetArticuloAct(Articulo articulo){
        articuloAct = articulo;
    }

    public Articulo GetArticuloAct(){
        return articuloAct;
    }

    // Constructor privado para evitar instanciación directa
    private ServiciosArticulo() {}

    // Método estático para obtener la instancia Singleton
    public static ServiciosArticulo getInstance() {
        if (instancia == null) {
            instancia = new ServiciosArticulo();
        }
        return instancia;
    }

    // Método estático para construir la SessionFactory una sola vez
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

    // Método para guardar un artículo ya creado
    public Articulo guardarArticulo(Articulo articulo) {

        Session session = null;
        Transaction transaction = null;

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Guardar el artículo en la base de datos
            session.saveOrUpdate(articulo);

            // Confirmar la transacción
            transaction.commit();
            return articulo;
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
        return null;
    }

    // Método para imprimir todos los artículos
    public void imprimirArticulos() {
        Session session = null;
        Transaction transaction = null;

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear consulta HQL para obtener todos los artículos
            List<Articulo> articulos = session.createQuery("FROM Articulo", Articulo.class).list();

            // Imprimir los artículos
            for (Articulo articulo : articulos) {
                System.out.println(articulo.getId() + " - " + articulo.getTitulo());
            }

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
    }



    // Método para obtener todos los artículos y devolverlos como un ArrayList
    public List<Articulo> obtenerTodosLosArticulos() {
        Session session = null;
        Transaction transaction = null;
        List<Articulo> articulos = new ArrayList<>();

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Crear consulta HQL para obtener todos los artículos
            articulos = session.createQuery("FROM Articulo", Articulo.class).list();

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

        return articulos;
    }


    public Articulo obtenerArticuloPorId(long id) {
        Session session = null;
        Transaction transaction = null;
        Articulo articulo = null;

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Obtener el artículo por su ID utilizando el método get de Hibernate
            articulo = session.get(Articulo.class, id);

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

        return articulo;
    }


    public void eliminarArticuloPorId(long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Obtener el artículo por su ID
            Articulo articulo = session.get(Articulo.class, id);
            if (articulo != null) {
                // Eliminar el artículo si se encuentra
                session.delete(articulo);
                System.out.println("Artículo eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún artículo con el ID proporcionado.");
            }

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
    }

    public void modificarArticuloPorId(long id, String nuevoTitulo, String nuevoContenido) {
        Session session = null;
        Transaction transaction = null;

        try {
            // Abrir una sesión de Hibernate desde la SessionFactory existente
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Obtener el artículo por su ID
            Articulo articulo = session.get(Articulo.class, id);
            if (articulo != null) {
                // Modificar el artículo si se encuentra
                articulo.setTitulo(nuevoTitulo);
                articulo.setCuerpo(nuevoContenido);
                session.update(articulo);
                System.out.println("Artículo modificado con éxito.");
            } else {
                System.out.println("No se encontró ningún artículo con el ID proporcionado.");
            }

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
    }
    public List<Articulo> obtenerArticulosPaginados(int pagina, int articulosPorPagina) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            int startIndex = pagina * articulosPorPagina;

            List<Articulo> articulos = session.createQuery("FROM Articulo ORDER BY fecha DESC", Articulo.class)
                    .setFirstResult(startIndex)
                    .setMaxResults(articulosPorPagina)
                    .list();

            transaction.commit();

            return articulos;
        } catch (Exception e) {
            // Manejo de excepciones (logging, rollback, etc.)
            e.printStackTrace();
            throw new RuntimeException("Error al obtener artículos paginados", e);
        }
    }

    public List<Articulo> obtenerArticulosPorEtiquetaPaginados(String etiqueta, int pagina, int articulosPorPagina) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            int startIndex = pagina * articulosPorPagina;

            // Ajusta la consulta para incluir el filtro por etiqueta solo si la etiqueta no está vacía
            String queryString;
            Query<Articulo> query;

            if (etiqueta != null && !etiqueta.isEmpty()) {
                queryString = "FROM Articulo WHERE etiqueta = :etiqueta ORDER BY fecha DESC";
                query = session.createQuery(queryString, Articulo.class)
                        .setParameter("etiqueta", etiqueta);
            } else {
                queryString = "FROM Articulo ORDER BY fecha DESC";
                query = session.createQuery(queryString, Articulo.class);
            }

            List<Articulo> articulos = query
                    .setFirstResult(startIndex)
                    .setMaxResults(articulosPorPagina)
                    .list();

            transaction.commit();

            return articulos;
        } catch (Exception e) {
            // Manejo de excepciones (logging, rollback, etc.)
            e.printStackTrace();
            throw new RuntimeException("Error al obtener artículos por etiqueta y paginados", e);
        }
    }

    public int obtenerCantidadArticulosPorEtiqueta(String etiqueta) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Ajusta la consulta para contar la cantidad de artículos por etiqueta
            String queryString = "SELECT COUNT(*) FROM Articulo WHERE etiqueta = :etiqueta";
            Long cantidad = session.createQuery(queryString, Long.class)
                    .setParameter("etiqueta", etiqueta)
                    .uniqueResult();

            transaction.commit();

            return cantidad != null ? cantidad.intValue() : 0;
        } catch (Exception e) {
            // Manejo de excepciones (logging, rollback, etc.)
            e.printStackTrace();
            throw new RuntimeException("Error al obtener la cantidad de artículos por etiqueta", e);
        }
    }

}
