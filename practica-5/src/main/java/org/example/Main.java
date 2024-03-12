/// LA BUENA ///
package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import com.google.gson.Gson;

import java.util.*;

import org.jasypt.util.text.BasicTextEncryptor;

import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    static String laimagen = null;

    public static void main(String[] args) {

        /* SERVIDOR JAVALIN */
        // Iniciar el servidor de Javalin
        int wsPort = 3000; // Puerto en el que escuchará el servidor WebSocket

        WSServer wsServer = new WSServer(new InetSocketAddress(wsPort));
        wsServer.start();
        System.out.println("Servidor WebSocket en ejecución en ws://localhost:" + wsPort);
        var app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });
        }).start(7000);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                wsServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        // Ruta para la página principal
        app.get("/", ctx -> {
            if (ServiciosUsuario.getInstance().login("TheAdmin", "123") == null) {
                ServiciosUsuario.getInstance().SetUsuario(ServiciosUsuario.getInstance().CrearUsuario("TheAdmin", "Admin", "123", true, false, 0));
                ServiciosUsuario.getInstance().SetUsuarioAct(ServiciosUsuario.getInstance().login("TheAdmin", "123"));
                ctx.redirect("login.html");
            } else {
                ServiciosUsuario.getInstance().SetUsuario(ServiciosUsuario.getInstance().login("TheAdmin", "123"));
                ServiciosUsuario.getInstance().SetUsuarioAct(ServiciosUsuario.getInstance().login("TheAdmin", "123"));
                ctx.redirect("/login.html");
            }

        });

        H2Server.getInstancia().init();

        UserAuthenticationLogger logger = new UserAuthenticationLogger();

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("C&V");

        app.post("/cookie", ctx -> {
            String usernameCookie = ctx.cookie("usernameCookie");
            String passwordCookie = ctx.cookie("passwordCookie");
            Usuario u = ServiciosUsuario.getInstance().login(textEncryptor.decrypt(usernameCookie), textEncryptor.decrypt(passwordCookie));
            if (u != null) {

                String nuevoUsuario = u.getUsername();
                logger.logUserAuthentication(nuevoUsuario);

                logger.printAllAuthenticatedUsers();

                ctx.status(200);
            } else {
                ctx.status(404);
            }
        });


        final Usuario[] usuario = {null};


        app.post("/loginParaObtenerUsuerioArriba", ctx -> {
            String username = ctx.cookie("username"); // Obtener el valor de la cookie de nombre de usuario
            System.out.println(" !!!! loginParaObtenerUsuerioArriba !!!!");

            usuario[0] = new Usuario(username, null, null, null, null, 1);
           // usuario[0].set(ServiciosUsuario.getInstance().getUsuarioPorUsername(username));

            if (username != null) {
                System.out.println("¡Galleta recibida! Usuario: " + username);
             //   ctx.result("¡Galleta recibida! Usuario: " + username);
                ctx.redirect("/obtenerAdmin");

            } else {
                System.out.println("No se encontró la cookie de usuario.");
                ctx.result("No se encontró la cookie de usuario.");
            }


        });


        app.get("/obtenerAdmin", ctx -> {

                /* Implementacion pasada */
                /*
                String usuarioJson = convertirAJson(ServiciosUsuario.getInstance().GetUsuario());
                ServiciosUsuario.getInstance().SetUsuario(null);

                ctx.json(usuarioJson);
                */


                /** Intento de nueva implementacion */
              //  System.out.println("ESTOY EN OBTENER ADMIN, EL USUARIO ES: " + usuario[0].getUsername());

        });



        app.post("/login", ctx -> {
            String nombreUsuario = ctx.formParam("username");
            String contrasena = ctx.formParam("password");
            String rememberMeStr = ctx.formParam("rememberMe");

            Usuario user = ServiciosUsuario.getInstance().login(nombreUsuario, contrasena);

            if (user != null) {


                ServiciosUsuario.getInstance().SetUsuarioAct(user);
                ServiciosUsuario.getInstance().setUsuarioFalso(user);  /* NEW */
                ServiciosUsuario.getInstance().addUsuarioConectado(user); /* NEW */




                String nuevoUsuario = user.getUsername();
                logger.logUserAuthentication(nuevoUsuario);

                if (rememberMeStr.equals("true")) {

                    // COOKIES
                    // Cookie Username
                    String usernameEncripted = textEncryptor.encrypt(user.getUsername());
                    ctx.cookie("usernameCookie", usernameEncripted, 604800);

                    // Cookie Password
                    String passwordEncripted = textEncryptor.encrypt(user.getPassword());
                    ctx.cookie("passwordCookie", passwordEncripted, 604800);
                    System.out.println("Remenber me");
                }
                ctx.status(200);
            } else {
                ctx.status(404);
            }
        });

        app.post("/crearArticulo", ctx -> {
            String titulo = ctx.formParam("titulo");
            String contenido = ctx.formParam("contenido");
            String etiqueta = ctx.formParam("etiqueta");
            Date fecha = new Date();
            etiqueta = etiqueta + ", ";
            String[] etiquetaSplit = etiqueta.split(", ");
            Usuario user = ctx.sessionAttribute("user");

            //Articulo newArticulo = new Articulo(titulo, contenido, ServiciosUsuario.getInstance().GetUsuarioAct().getId(), fecha);
            Articulo newArticulo = new Articulo(titulo, contenido, user.getId(), fecha);
            Articulo newArticuloTuti = ServiciosArticulo.getInstance().guardarArticulo(newArticulo);
            for (String palabra : etiquetaSplit) {
                Etiqueta etiquetaNew = new Etiqueta(palabra);
                if (ServiciosEtiquetas.getInstance().etiquetaExiste(etiquetaNew.getEtiqueta()) == null) {
                    Etiqueta etiquetaNewTuti = ServiciosEtiquetas.getInstance().crearEtiqueta(palabra);
                    ArticuloEtiqueta artiEti = new ArticuloEtiqueta(newArticuloTuti.getId(), etiquetaNewTuti.getId());
                    ServiciosArticuloEtiqueta.getInstance().almacenarArticuloEtiqueta(artiEti);
                } else {
                    Etiqueta barbie = ServiciosEtiquetas.getInstance().etiquetaExiste(palabra);
                    ArticuloEtiqueta artiEti = new ArticuloEtiqueta(newArticuloTuti.getId(), barbie.getId());
                    ServiciosArticuloEtiqueta.getInstance().almacenarArticuloEtiqueta(artiEti);
                }
            }
            ServiciosArticulo.getInstance().imprimirArticulos();
            ServiciosArticuloEtiqueta.getInstance().imprimirArticulosEtiquetas();
        });



        app.get("/cargarArticulosEtiqueta/{etiqueta}", ctx -> {
            try {
                String etiqueta = ctx.pathParam("etiqueta");

                List<Articulo> lista;

                if (etiqueta != null && !etiqueta.isEmpty()) {
                    Etiqueta tutiTiqueta = ServiciosEtiquetas.getInstance().etiquetaExiste(etiqueta);
                    List<ArticuloEtiqueta> todoTuti = ServiciosArticuloEtiqueta.getInstance().obtenerTodosLosArticulosEtiquetas();
                    List<Articulo> articulotodo = ServiciosArticulo.getInstance().obtenerTodosLosArticulos();
                    List<String> etiquetas = new ArrayList<>();
                    List<Articulo> articuloClean = new ArrayList<>();

                    for (Articulo arti : articulotodo) {
                        for (ArticuloEtiqueta artitUTI : todoTuti) {
                            if (artitUTI.getIdEtiqueta() == tutiTiqueta.getId() && arti.getId() == artitUTI.getIdArticulo()) {
                                articuloClean.add(arti);
                            }
                        }
                    }

                    lista = articuloClean;
                } else {
                    lista = ServiciosArticulo.getInstance().obtenerTodosLosArticulos();
                }

                List<String> etiquetas = new ArrayList<>();
                for (Articulo arti : lista) {
                    etiquetas.add(convertirEtiquetasAJson(arti));
                }

                int cantidad = lista.size();
                Respuesta respuesta = new Respuesta(lista, cantidad, etiquetas);
                ctx.json(respuesta);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error: " + e.getMessage());
            }
        });

        app.post("/cargar-articulo", ctx -> {
            String body = ctx.body();
            JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
            long articleId = jsonObject.get("id").getAsLong();
            Articulo articulo = ServiciosArticulo.getInstance().obtenerArticuloPorId(articleId);
            ServiciosArticulo.getInstance().SetArticuloAct(articulo);
            ctx.status(200);
        });

        //funcion de cargar articulos con ajax, parte1:
        app.post("/cargarArticulos", ctx -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode bodyJson = objectMapper.readTree(ctx.body());
                int paginaActual = bodyJson.get("pagina").asInt();
                int articulosPorPagina = 5;

                // Obtén el filtro del cuerpo de la solicitud
                String filtro = bodyJson.has("filtro") ? bodyJson.get("filtro").asText() : null;

                // Utiliza el filtro al obtener la lista de artículos paginados
                List<Articulo> lista;

                if (filtro != null) {
                    // Si hay un filtro, obtén los artículos filtrados
                    lista = ServiciosArticulo.getInstance().obtenerArticulosPorEtiquetaPaginados(filtro, paginaActual, articulosPorPagina);
                } else {
                    // Si no hay filtro, obtén todos los artículos
                    lista = ServiciosArticulo.getInstance().obtenerArticulosPaginados(paginaActual, articulosPorPagina);
                }

                List<String> etiquetas = new ArrayList<>();
                int cantidadArtTotales;

                if (filtro != null) {
                    // Si hay un filtro, obtén la cantidad total de artículos filtrados
                    cantidadArtTotales = ServiciosArticulo.getInstance().obtenerCantidadArticulosPorEtiqueta(filtro);
                } else {
                    // Si no hay filtro, obtén la cantidad total de todos los artículos
                    cantidadArtTotales = ServiciosArticulo.getInstance().obtenerTodosLosArticulos().size();
                }

                for (Articulo articulo : lista) {
                    etiquetas.add(convertirEtiquetasAJson(articulo));
                }

                Respuesta respuesta = new Respuesta(lista, cantidadArtTotales, etiquetas);
                ctx.json(respuesta);
            } catch (Exception e) {
                // Log the exception details
                e.printStackTrace();

                // Return a more detailed error message to the client
                ctx.status(500).result("Internal Server Error: " + e.getMessage());
            }
        });


        app.get("/obtenerArticulo", ctx -> {
            Articulo articulo = ServiciosArticulo.getInstance().GetArticuloAct();

            List<Comentario> todoLosComentarios = ServiciosComentario.getInstance().obtenerTodosLosComentarios();

            List<Comentario> comentarios = new ArrayList<>();

            long idArticulo = ServiciosArticulo.getInstance().GetArticuloAct().getId();

            for(Comentario coment : todoLosComentarios){
                if(coment != null)
                    if(coment.getIdArticulo() == idArticulo)
                        comentarios.add(coment);
            }

            int cantidadComentarios = comentarios.size();
            List<String> etiquetas = new ArrayList<>();

            String nombreAutor = ServiciosUsuario.getInstance().buscarUsuarioPorId(ServiciosArticulo.getInstance().GetArticuloAct().getAutor()).getNombre();

            etiquetas.add(convertirEtiquetasAJson(articulo));

            Respuesta2 respuesta = new Respuesta2(articulo, comentarios, etiquetas,nombreAutor,cantidadComentarios);
            ctx.json(respuesta);
        });


        app.get("/obtener-articulo-modify", ctx -> {
            Gson gson = new Gson();
            String articuloJson = gson.toJson( ServiciosArticulo.getInstance().GetArticuloAct());
            ctx.result(articuloJson).contentType("application/json; charset=utf-8");
        });


        app.post("/modificarArticulo", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            String etiquetas = ctx.formParam("etiquetas");
            ServiciosArticuloEtiqueta.getInstance().borrarArticuloEtiquetaPorIdArticulo(ServiciosArticulo.getInstance().GetArticuloAct().getId());
            long articuloId = ServiciosArticulo.getInstance().GetArticuloAct().getId();
            ServiciosArticulo.getInstance().modificarArticuloPorId(articuloId, titulo, cuerpo);
            String[] etiquetaSplit = etiquetas.split(",");
            for (String etiqueta : etiquetaSplit) {
                etiqueta = etiqueta.trim();
                Etiqueta existente = ServiciosEtiquetas.getInstance().etiquetaExiste(etiqueta);
                if (existente == null) {
                    Etiqueta nuevaEtiqueta = ServiciosEtiquetas.getInstance().crearEtiqueta(etiqueta);
                    ServiciosArticuloEtiqueta.getInstance().almacenarArticuloEtiqueta(new ArticuloEtiqueta(articuloId, nuevaEtiqueta.getId()));
                } else {
                    ServiciosArticuloEtiqueta.getInstance().almacenarArticuloEtiqueta(new ArticuloEtiqueta(articuloId, existente.getId()));
                }
            }

            ctx.status(200);
        });

        app.post("/eliminarArticulo", ctx -> {
            Articulo articulo = ServiciosArticulo.getInstance().GetArticuloAct();
            ServiciosArticulo.getInstance().eliminarArticuloPorId(articulo.getId());
            ServiciosArticulo.getInstance().SetArticuloAct(null);

        });

        app.get("/cargarUsuario", ctx -> {
           /* Usuario user = ServiciosUsuario.getInstance().GetUsuarioAct();*/
            Usuario user = ctx.sessionAttribute("user");


            ctx.json(user);
        });


        app.get("/obtenerUsuario", ctx -> {
            /*
            String usuarioJson = convertirAJson(ServiciosUsuario.getInstance().GetUsuarioAct());

            Usuario user = ServiciosUsuario.getInstance().GetUsuarioAct();
            if(user != null)
                ctx.json(ServiciosUsuario.getInstance().GetUsuarioAct());*/


            System.out.println("ENTRE A OBTENER USUARIO");
            Usuario user = ctx.sessionAttribute("user");

            if(user != null)
                ctx.json(user);
            else
                System.out.println("El usuario[0] es nulo");
        });

        app.post("/logout", ctx -> {
               ctx.removeCookie("usernameCookie");
               ctx.removeCookie("passwordCookie");

            String username = ctx.formParam("username");
           // Usuario nadie = new Usuario(null,null,null,false,false, 0);
           // ServiciosUsuario.getInstance().SetUsuarioAct(nadie);
            ServiciosUsuario.getInstance().SetUsuarioAct(null);

            ServiciosUsuario.getInstance().eliminarUsuarioConectado(ServiciosUsuario.getInstance().getUsuarioFalso().getUsername());



        });

        app.post("/crearComentario", ctx -> {
            String comentarioTexto = ctx.formParam("Comentario");
            Usuario autor = ServiciosUsuario.getInstance().buscarUsuarioPorId(ServiciosArticulo.getInstance().GetArticuloAct().getAutor());
            Comentario comentario = ServiciosComentario.getInstance().crearComentario(comentarioTexto, ServiciosUsuario.getInstance().GetUsuarioAct().getId(), ServiciosUsuario.getInstance().GetUsuarioAct().getNombre(), ServiciosArticulo.getInstance().GetArticuloAct().getId());
            ctx.json(comentario);
        });

        app.post("/registrar-usuario", ctx -> {
            String nombreUsuario = ctx.formParam("username");
            String nombre = ctx.formParam("nombre");
            String contrasena = ctx.formParam("contrasena");
            boolean esAdministrador = Boolean.parseBoolean(ctx.formParam("esAdministrador"));
            boolean esAutor = Boolean.parseBoolean(ctx.formParam("esAutor"));
           // Usuario newUser = new Usuario(nombreUsuario, nombre, contrasena, esAdministrador, esAutor, 0);
            Usuario newUser =   ServiciosUsuario.getInstance().CrearUsuario(nombreUsuario, nombre, contrasena, esAdministrador, esAutor, 0);




            System.out.println("EL USERNAME : " + newUser.getUsername());
        });





        app.get("/activeConnections", ctx -> {
            List<WebSocket> activeConnections = wsServer.getActiveConnections();
            List<String> connectionAddresses = new ArrayList<>();
            for (WebSocket connection : activeConnections) {
                connectionAddresses.add(connection.getRemoteSocketAddress().toString());
            }
            ctx.json(connectionAddresses);
        });



        app.get("/usuariosConectados", ctx -> {

            System.out.println("ESTOY EN USUARIOS CONECTADOS");

            //Usuario tutiuser = new Usuario("tutilandia", null, null, null, null, 0);
          //  ServiciosUsuario.getInstance().addUsuarioConectado(tutiuser);


             List<Usuario> usuariosConectados = ServiciosUsuario.getInstance().getUsuariosConectados();
            // List<Usuario> usuariosConectados = ServiciosUsuario.getInstance().getUsuarios();

        //   List<Usuario> usuariosConectadosReales = new ArrayList<>();
















            //    System.out.println("INTENTANDO ELIMINAR REPETIDOS O INACTIVOS");
            /*
            List<WebSocket> coneccionesAcivasReales = wsServer.getActiveConnections();
            for(Usuario user : usuariosConectados){



                System.out.println("tuti" + user.hashCode());


                if(wsServer.isConnectionActive(user.hashCode()))
                {
                    System.out.println("Esta parte funciona");

                    usuariosConectadosReales.add(user);
                }





            }

*/

          /*
            // Ahora puedes acceder a las conexiones activas desde aquí
            List<WebSocket> activeConnections = wsServer.getActiveConnections();
            // Haz lo que necesites con las conexiones activas
            for (WebSocket conn : activeConnections) {
                System.out.println("Conexión activa: " + conn.getRemoteSocketAddress());
            }
*/
            ctx.json(usuariosConectados);
          //  ctx.json(usuariosConectadosReales);

            //List<WebSocket> coneccionesAcivasReales = wsServer.getActiveConnections();
            //ELIMINAR LOS USUARIOS QUE NO TENGAN UNA VERDADERA CONEXION ACTIVA






        });

/*
        app.get("/usuariosConectados", ctx -> {
            List<Usuario> usuariosConectados = ServiciosUsuario.getInstance().getUsuariosConectados();

            // Crear un conjunto para eliminar duplicados
            Set<Usuario> conjuntoUsuarios = new HashSet<>(usuariosConectados);

            // Limpiar la lista y agregar los elementos únicos
            usuariosConectados.clear();
            usuariosConectados.addAll(conjuntoUsuarios);

            ctx.json(usuariosConectados);
        });
*/
        // Configurar la ruta para enviar mensajes
        app.get("/enviar-mensajes", ctx -> {
            // Aquí recuperas tus mensajes de alguna fuente, como una base de datos
            ///////Usuario user = ServiciosUsuario.getInstance().GetUsuarioAct();
            /* Usuario user = ServiciosUsuario.getInstance().getUsuarioFalso(); */
            Usuario user = ctx.sessionAttribute("user");

            List<Mensaje> mensajes = new ArrayList<>();
            if(user != null)
                mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(user.getId());


            // Convertir la lista de mensajes a JSON
            Gson gson = new Gson();
            String mensajesJson = gson.toJson(mensajes);

            // Enviar la lista de mensajes como respuesta al cliente
            ctx.result(mensajesJson).contentType("application/json");
        });



        // Configurar la ruta para enviar mensajes
        app.get("/enviar-mensajes-admin", ctx -> {
            System.out.println("ENTRE A ENVIAR-MENSAJES-ADMIN");
            // Aquí recuperas tus mensajes de alguna fuente, como una base de datos
            // List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(ServiciosUsuario.getInstance().getUsuarioParaAdmin().getId());
            // List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(ServiciosUsuario.getInstance().getUsuarioParaAdmin().getId());

            /* Usuario user = ServiciosUsuario.getInstance().getUsuarioFalso();*/
           // Usuario userForChat = ServiciosUsuario.getInstance().getUsuarioPorUsername(cookieValue);
           //// ctx.sessionAttribute("userForChat", userForChat);

            Usuario userForChat = ctx.sessionAttribute("userForChat");

            List<Mensaje> mensajes = new ArrayList<>();
            if(userForChat != null)
               mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(userForChat.getId());

            //




            // Convertir la lista de mensajes a JSON
            Gson gson = new Gson();
            String mensajesJson = gson.toJson(mensajes);

            // Enviar la lista de mensajes como respuesta al cliente
            ctx.result(mensajesJson).contentType("application/json");
        });





        // Configurar la ruta para enviar mensajes
        app.get("/enviar-mensajes-index", ctx -> {
            System.out.println("ENTRE A ENVIAR-MENSAJES-ADMIN");
            // Aquí recuperas tus mensajes de alguna fuente, como una base de datos
            // List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(ServiciosUsuario.getInstance().getUsuarioParaAdmin().getId());
            // List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(ServiciosUsuario.getInstance().getUsuarioParaAdmin().getId());

            /* Usuario user = ServiciosUsuario.getInstance().getUsuarioFalso();*/
            // Usuario userForChat = ServiciosUsuario.getInstance().getUsuarioPorUsername(cookieValue);
            //// ctx.sessionAttribute("userForChat", userForChat);

            Usuario user = ctx.sessionAttribute("user");

            List<Mensaje> mensajes = new ArrayList<>();
            if(user != null)
                mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(user.getId());

            //




            // Convertir la lista de mensajes a JSON
            Gson gson = new Gson();
            String mensajesJson = gson.toJson(mensajes);

            // Enviar la lista de mensajes como respuesta al cliente
            ctx.result(mensajesJson).contentType("application/json");
        });

        // Mensajes de un usuario
        app.get("/enviar-mensajes-tuti", ctx -> {
           /// // Aquí recuperas tus mensajes de alguna fuente, como una base de datos
            //List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(ServiciosUsuario.getInstance().GetUsuarioAct().getId());
            Usuario user = ServiciosUsuario.getInstance().getUsuarioFalso();
            List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(user.getId());


            // Convertir la lista de mensajes a JSON
            Gson gson = new Gson();
            String mensajesJson = gson.toJson(mensajes);

            // Enviar la lista de mensajes como respuesta al cliente
            ctx.result(mensajesJson).contentType("application/json");
        });


        // Agregar una nueva ruta para manejar la solicitud POST que recibe el nombre de usuario
        ObjectMapper objectMapper = new ObjectMapper();

        app.post("/enviar-username", ctx -> {
            /*
            // Recuperar el cuerpo de la solicitud como JSON
            JsonNode jsonNode = objectMapper.readTree(ctx.body());

            // Extraer el nombre de usuario del JSON
            String username = jsonNode.get("username").asText();

            // Aquí puedes realizar cualquier acción necesaria con el nombre de usuario
            System.out.println("Nombre de usuario recibido: " + username);

            // Actualizar usuario para admin
            //Usuario user = ServiciosUsuario.getInstance().getUsuarioPorUsername(username);
            Usuario user = ServiciosUsuario.getInstance().getUsuarioConectadoPorUsername(username);
            System.out.println(user.getSesionId());


            int idSesion = user.getSesionId();*/

          //  WebSocket client =  wsServer.getClientByHashCode(idSesion); /* ojo */
            // TEST
            // Eliminar de los usuarios conectados todos aquellos que no tengan un cliente



/*
            if(client != null)
                 wsServer.sendMessageToClient(client, "tuti");
            else
                System.out.println("Cliente es nulo");

*/


            // USUARIO PARA ADMIN
          //  ServiciosUsuario.getInstance().setUsuarioParaAdmin(user);














            String cookieValue = ctx.cookie("cookieforchat");

            // Acciones con el valor de la cookie
            System.out.println("Valor de la cookie recibida: " + cookieValue);

            // Aquí puedes realizar las acciones necesarias con el valor de la cookie
            // Por ejemplo, guardar el nombre de usuario en una base de datos, etc.

            // Envía una respuesta al cliente
            ctx.status(200).result("Cookie recibida correctamente");




            //Obtener cliente




            //  WebSocket client =  wsServer.getClientByHashCode(idSesion); /* ojo */
            // TEST
            // Eliminar de los usuarios conectados todos aquellos que no tengan un cliente


            Usuario userForChat = ServiciosUsuario.getInstance().getUsuarioPorUsername(cookieValue);
            ctx.sessionAttribute("userForChat", userForChat);
            int idSesion = userForChat.getSesionId();
            WebSocket client =  wsServer.getClientByHashCode(idSesion);

            if(client != null)
                 wsServer.sendMessageToClient(client, "tuti");
            else
                System.out.println("Cliente es nulo");

            // Enviar una respuesta al cliente
            ctx.status(200).result("tititit Nombre de usuario recibido correctamente: " + cookieValue);
        });



        app.post("/enviar-mensaje-cliente", ctx -> {
            // Obtener el texto del mensaje del cuerpo de la solicitud
            String mensaje = ctx.formParam("mensaje");

            // Verdaderamente mandar el mensaje
            // Conseguir la sesion del usuario

          //  int idSesion = ServiciosUsuario.getInstance().getUsuarioParaAdmin().getSesionId();

           /* int idSesion = ServiciosUsuario.getInstance().getUsuarioFalso().getSesionId(); */

            /*** tuti ***/
            //Usuario user = ctx.sessionAttribute("userForChat");
            Usuario user = ctx.sessionAttribute("user");
            int idSesion = user.getSesionId();
            wsServer.sendMessageToClient(wsServer.getClientByHashCode(idSesion), mensaje);



            //falta lo de base de datos

            // Aquí puedes realizar cualquier acción necesaria con el mensaje recibido
            System.out.println("Mensaje recibido: " + mensaje);

            // Enviar una respuesta al cliente
            ctx.status(200).result("Mensaje recibido correctamente: " + mensaje);
        });





        app.post("/guardarFoto", ctx -> {
            System.out.println("GUARDAR FOTO");
            // Obtiene los datos JSON enviados desde el cliente
            String username = ctx.formParam("username");
            String profilePicture = ctx.formParam("profilePicture");

            // Aquí puedes procesar los datos recibidos, por ejemplo, guardar la imagen en el servidor

            // Envía una respuesta al cliente
            ctx.result("Imagen recibida y procesada correctamente");
        });




        app.post("/upload-image", ctx -> {
            // Obtener los datos de la imagen en base64 desde el cuerpo de la solicitud
            String imageData = ctx.body();



            //TODO: GUARDAR ESA IMAGEN CORRECTAMENTE
            //INTENTO DE GUARDAR LA FOTO CORRECTAMENTE
          /* Usuario user = ServiciosUsuario.getInstance().GetUsuarioAct();*/
            Usuario user = ctx.sessionAttribute("user");

            ServiciosFoto.getInstance().crearFoto(imageData, user.getId());




            System.out.println("@------------FOTO :    " + imageData);

            laimagen = imageData;
            // Guardar la imagen en el servidor
            // Aquí deberías implementar la lógica para guardar la imagen en el servidor.

            // Responder al cliente
            ctx.result("Imagen recibida correctamente");
        });








        app.get("/obtenerImagen", ctx -> {
            // Aquí deberías escribir el código para obtener la foto del usuario
            // y devolverla al cliente en formato base64 o en el formato adecuado
            // Supongamos que tienes una función que devuelve la foto del usuario en base64

           // String fotoBase64 = "base64_encoded_image_data"; // Debes implementar esta función
            String fotoBase64 = "base64_encoded_image_data"; // Debes implementar esta función


            //TODO: PARA LOD E BASE DE DATOS
            // EN VES DE ENVIAR LAIMAGEN SE DEBE ENIAR LA IMAGEN CORRECTA SEGUN EL USUARIO ACTUAL
            /*Usuario user = ServiciosUsuario.getInstance().GetUsuarioAct(); */
            Usuario user = ctx.sessionAttribute("user");

            Foto futiruti = ServiciosFoto.getInstance().obtenerFotoPorIdUsuario(user.getId());
            String TutiString = futiruti.getBase64Foto();


            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                ctx.result(TutiString); // Devolver la foto como respuesta al cliente
            } else {
                ctx.status(404); // Indicar que la foto no fue encontrada (código de error HTTP 404)
            }



            //TODO : HACER QUE A CADA USUARIO SE LE TENGA SU FOTO SI YA EL LA HA DEFINIDO
            //DIGASE, IMPLEMENTAR BASE DE DATOS PARA LAS FOTOS
        });






        /**  LA FELICIDAD NUNCA ES COMPELTA **/
        // Intentar recibir cookie para solucionar mi vida
        app.post("/logintuti", ctx -> {
            String username = ctx.cookie("username"); // Obtener el valor de la cookie de nombre de usuario

           // Usuario user = new Usuario(username, null, null, null, null, 0);
            Usuario user = ServiciosUsuario.getInstance().getUsuarioPorUsername(username);
            ctx.sessionAttribute("user", user);

            if (username != null) {
                System.out.println("¡Galleta recibida! Usuario: " + username);
                ServiciosUsuario.getInstance().imprimirUsuarios();
                ServiciosUsuario.getInstance().imprimirUsuarios();
                ctx.result("¡Galleta recibida! Usuario: " + user.getUsername());
            } else {
                System.out.println("No se encontró la cookie de usuario.");
                ctx.result("No se encontró la cookie de usuario.");
            }
        });

        app.post("/sandia", ctx -> {
            //Obtener el mensaje del cuerpo de la solicitud
            String mensaje = ctx.formParam("mensaje");

            Usuario userForChat = ctx.sessionAttribute("userForChat");
            Mensaje newMensaje = ServiciosMensaje.getInstance().enviarMensaje( userForChat.getId(),mensaje, true);
            ServiciosMensaje.getInstance().imprimirMensajesPorUsuario(userForChat.getId());

          //  ServiciosMensaje.getInstance().

            // Realizar cualquier procesamiento necesario con el mensaje
            // En este ejemplo, simplemente lo imprimimos en la consola del servidor
            System.out.println("Mensaje recibido del cliente: " + mensaje);
            System.out.println("AHHHHHHHHHHHHHHHHHH EL USERNAME : " + userForChat.getUsername());

            // Enviar una respuesta al cliente para confirmar que el mensaje fue recibido
            ctx.result("Mensaje recibido correctamente por el servidor.");



        });
    }






















    // FUNCIONES DE UTILIDAD
    private static String convertirAJson(Usuario usuario) {
        Gson gson = new Gson();
        return gson.toJson(usuario);
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

        public List<String> getEtiquetas() {
            return etiquetas;
        }
    }

    private static String convertirEtiquetasAJson(Articulo articulo) {
        StringBuilder etiquetas = new StringBuilder();

        List<ArticuloEtiqueta> articulosEtiquetas = ServiciosArticuloEtiqueta.getInstance().obtenerTodosLosArticulosEtiquetas();
        for (ArticuloEtiqueta artieti : articulosEtiquetas) {
            if (artieti.getIdArticulo() == articulo.getId()) {
                Etiqueta tutiTiqueta = ServiciosEtiquetas.getInstance().getEtiquetaPorId(artieti.getIdEtiqueta());
                if (tutiTiqueta != null) {
                    etiquetas.append(tutiTiqueta.getEtiqueta()).append(", ");
                }
            }
        }
        if (!etiquetas.isEmpty()) {
            etiquetas.delete(etiquetas.length() - 2, etiquetas.length());
        }
        return etiquetas.toString();
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