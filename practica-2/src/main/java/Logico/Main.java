package Logico;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import com.google.gson.Gson;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        String loginUrl = "http://localhost:7000/";
        Controladora.getInstance();

        var app = Javalin.create(config ->
                {
                    config.staticFiles.add(staticFileConfig -> {
                        staticFileConfig.hostedPath = "/";
                        staticFileConfig.directory = "/publico";
                        staticFileConfig.location = Location.CLASSPATH;
                        staticFileConfig.precompress = false;
                        staticFileConfig.aliasCheck = null;
                    });
                })
                .start(7000);
        app.get("/", ctx -> ctx.redirect("/crearUsuarioInicial.html"));

        app.post("/registrarUsuarioInicial", ctx -> {
            String username = ctx.formParam("username");
            String name = ctx.formParam("name");
            String password = ctx.formParam("password");

            Usuario user = new Usuario(username, name, password, true, true);
            Controladora.getInstance().agregarUsuario(user);
            Controladora.getInstance().setUserLogeado(user);
        });

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            int resp = Controladora.validarUsuario(username,password,Controladora.getInstance().getListaUsuarios());

            if (resp != 0) {
                ctx.sessionAttribute("username", username);
                ctx.sessionAttribute("password", password);
            }
            Controladora.getInstance().setUserLogeado(Controladora.buscarUsuario(username,password,Controladora.getInstance().getListaUsuarios()));

            ctx.json(resp);

        });

        app.post("/registrarUsuario", ctx -> {
            String username = ctx.formParam("username");
            String name = ctx.formParam("name");
            String password = ctx.formParam("password");
            boolean esAdmin = Boolean.parseBoolean(ctx.formParam("Administrador"));
            boolean esAutor = Boolean.parseBoolean(ctx.formParam("Autor"));

            Usuario user = new Usuario(username, name, password, esAdmin, esAutor);
            Controladora.getInstance().agregarUsuario(user);
        });

        app.post("/crearComentario", ctx -> {
            String comentarioTexto = ctx.formParam("Comentario");
            Usuario autor = Controladora.getInstance().getUserLogueado();
            Long id = Controladora.getInstance().crearComentarioId();

            Comentario comentario = new Comentario(id, comentarioTexto, autor);
            Controladora.getInstance().getArticuloSeleccionado().getListaComentarios().add(comentario);

            ctx.json(comentario);
        });


        app.post("/user", ctx -> {
            String nombreUsuario = ctx.formParam("nombreUsuario");

            Usuario user = Controladora.getInstance().getUserLogueado();
            ctx.json(user.getNombre());
        });

        app.get("/obtenerUsuario", ctx -> {

            String usuarioJson = convertirUsuarioAJson(Controladora.getInstance().getUserLogueado());

            ctx.json(usuarioJson);
        });

        app.get("/obtenerArticulo", ctx -> {

            Articulo articulo = Controladora.getInstance().getArticuloSeleccionado();
            List<Comentario> comentarios = articulo.getListaComentarios();
            int cantidadComentarios = comentarios.size();
            List<String> etiquetas = new ArrayList<>();
            String nombreAutor = articulo.getAutor().getUsername();
            etiquetas.add(convertirEtiquetasAJson(articulo));
            Respuesta2 respuesta = new Respuesta2(articulo, comentarios, etiquetas,nombreAutor,cantidadComentarios);
            ctx.json(respuesta);

        });

        app.post("/envioDeArticulo", ctx -> {
            String titulo = ctx.formParam("tituloArticulo");
            String cuerpo = ctx.formParam("cuerpoArticulo");

            Articulo articuloSeleccionado = Controladora.getInstance().buscarArticuloPorTituloYCuerpo(titulo,cuerpo);
            Controladora.getInstance().setArticuloSeleccionado(articuloSeleccionado);
        });


        app.get("/cargarArticulos", ctx -> {
            List<Articulo> lista = Controladora.getInstance().getListaArticulo();
            int cantidad = lista.size();
            List<String> etiquetas = new ArrayList<>();
            for(Articulo arti: lista){
                etiquetas.add(convertirEtiquetasAJson(arti));
            }

            Respuesta respuesta = new Respuesta(lista, cantidad, etiquetas);
            ctx.json(respuesta);
        });

        app.post("/logout", ctx -> {
            String username = ctx.formParam("username");
            Controladora.getInstance().deslogearUsuario();
        });

        app.post("/crearArticulo", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            String etiquetas = ctx.formParam("etiquetas");
            Usuario autor = Controladora.getInstance().getUserLogueado();

            Articulo articulo = Controladora.getInstance().crearArticulo(titulo,cuerpo,etiquetas,autor);
        });

        app.post("/modificarArticulo", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            String etiquetas = ctx.formParam("etiquetas");

            Controladora.getInstance().modificarArticulo(titulo,cuerpo,etiquetas);
        });

        app.post("/eliminarArticulo", ctx -> {
            Articulo articulo = Controladora.getInstance().getArticuloSeleccionado();
            Controladora.getInstance().setArticuloSeleccionado(null);
            Controladora.getInstance().getListaArticulo().remove(articulo);
        });
    }

    private static String convertirComentariosAJson(Articulo articulo) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (Comentario comentario : articulo.getListaComentarios()) {
            jsonBuilder.append("{");
            jsonBuilder.append("\"autorComentario\": \"").append(comentario.getAutor().getNombre()).append("\",");
            jsonBuilder.append("\"contenidoComentario\": \"").append(comentario.getComentario()).append("\"");
            jsonBuilder.append("},");
        }

        if (articulo.getListaComentarios().size() > 0) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }

    public static String convertirArticuloAJson(Articulo articulo) {
        Gson gson = new Gson();
        String autorInfo;
        if (articulo.getAutor() != null) {
            autorInfo = convertirUsuarioAJson(articulo.getAutor());
        } else {
            autorInfo = "null";
        }

        String comentariosInfo = gson.toJson(articulo.getListaComentarios());
        String etiquetasInfo = gson.toJson(articulo.getListaEtiqueta());

        return "{"
                + "\"id\": " + articulo.getId() + ","
                + "\"titulo\": \"" + articulo.getTitulo() + "\","
                + "\"cuerpo\": \"" + articulo.getCuerpo() + "\","
                + "\"autor\": " + autorInfo + ","
                + "\"fecha\": \"" + articulo.getFecha().toString() + "\","
                + "\"comentarios\": " + comentariosInfo + ","
                + "\"etiquetas\": " + etiquetasInfo
                + "}";
    }

    private static String convertirUsuarioAJson(Usuario usuario) {
        String adminInfo;
        if (usuario.isAdministrador()) {
            adminInfo = "true";
        } else {
            adminInfo = "false";
        }

        String autorInfo;
        if (usuario.isAutor()) {
            autorInfo = "true";
        } else {
            autorInfo = "false";
        }
        return "{ \"username\": \"" + usuario.getUsername() + "\", \"nombre\": \"" + usuario.getNombre() + "\", \"contrasena\": \"" + usuario.getPassword() + "\", \"administrador\": " + adminInfo + ", \"autor\": " + autorInfo + " }";
    }

    private static String convertirEtiquetasAJson(Articulo articulo) {
        StringBuilder etiquetas = new StringBuilder();
        for (Etiqueta etiqueta : articulo.getListaEtiqueta()) {
            etiquetas.append(etiqueta.getEtiqueta()).append(", ");
        }

        if (etiquetas.length() > 0) {
            etiquetas.delete(etiquetas.length() - 2, etiquetas.length());
        }

        return etiquetas.toString();
    }


    static class Respuesta {
        private List<Articulo> articulos;
        private int cantidad;

        private List<String> etiquetas;

        public Respuesta(List<Articulo> articulos, int cantidad, List<String> etiquetas) {
            this.articulos = articulos;
            this.cantidad = cantidad;
            this.etiquetas = etiquetas;
        }

        public List<Articulo> getArticulos() {
            return articulos;
        }

        public int getCantidad() {
            return cantidad;
        }

        public List<String> getEtiquetas(){
            return  etiquetas;
        }
    }

    static class Respuesta2 {
        private Articulo articulo;
        private List<Comentario> comentarios;

        private List<String> etiquetas;

        private String nombreAutor;

        private int cantidadComentarios;

        public Respuesta2(Articulo articulo, List<Comentario> comentarios, List<String> etiquetas, String nombreAutor, int cantidadComentarios) {
            this.articulo = articulo;
            this.comentarios = comentarios;
            this.etiquetas = etiquetas;
            this.nombreAutor = nombreAutor;
            this.cantidadComentarios = cantidadComentarios;
        }

        public Articulo getArticulo() {
            return articulo;
        }

        public List<Comentario> getComentarios() {
            return comentarios;
        }

        public List<String> getEtiquetas(){
            return  etiquetas;
        }

        public String getNombreAutor(){
            return nombreAutor;
        }

        public int getCantidadComentarios(){
            return cantidadComentarios;
        }
    }
}

