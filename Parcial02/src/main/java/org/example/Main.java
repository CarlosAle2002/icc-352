package org.example;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import com.google.gson.Gson;
public class Main {
    public static void main(String[] args) {

        // todo: REMOVE
        Usuario userTest = new Usuario("TheAdmin", "Admin", "123", "AdminRol");
      //  ServiciosUsuario.getInstance().guardarUsuario(userTest);




        /*** SERVIDOR JAVALIN ***/
        // Iniciar el servidor de Javalin
        var app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });
        }).start(80);

        // Ruta para la página principal
        app.get("/", ctx -> {
            ctx.redirect("login.html");
        });


        /*** SERVIDOR H2 ***/
        H2Server.getInstancia().init();



        // USUARIO ADMIN POR DEFECTO
        if(!ServiciosUsuario.getInstance().existeUsuario("TheAdmin", "123"))
        {
            Usuario Test = new Usuario("TheAdmin", "123", "Admin", "AdminRol");
            ServiciosUsuario.getInstance().guardarUsuario(Test);
        }

        // TEST

        // System.out.println(ServiciosUsuario.getInstance().existeUsuario("TheAdmin", "123"));
        // System.out.println("\n");

        //System.out.println(   ServiciosUsuario.getInstance().existeUsuario("TheAdmin", "123"));

        // ServiciosUsuario.getInstance().imprimirTodosLosUsuarios();






        /* LOGIN */
        app.post("/login", ctx -> {
            String nombreUsuario = ctx.formParam("username");
            String contrasena = ctx.formParam("password");

            System.out.println("\n");
            System.out.println("ESTOY EN LOGIN");
            System.out.println("nombreUsuario : " + nombreUsuario);
            System.out.println("contrasena : " + contrasena);
            System.out.println("\n");

            if(ServiciosUsuario.getInstance().existeUsuario(nombreUsuario, contrasena))
            {
                System.out.println("Existe usuario con dichas credenciales \n");
                ctx.status(200);
            }else
            {
                System.out.println("No existe usuario con dichas credenciales \n");
                ctx.status(404);
            }
        });



        /* CARGAR REGISTROS AL SERVIDOR */
        app.post("/cargarRegistroServidor", ctx -> {
            // Obtener los datos del registro del cuerpo de la solicitud
            String nombre = ctx.formParam("nombre");
            String sector = ctx.formParam("sector");
            String nivelEscolar = ctx.formParam("nivelEscolar");
            String  UsuarioRegistrador =ctx.formParam("UsuarioRegistrador");
            float latitud = Float.parseFloat(Objects.requireNonNull(ctx.formParam("latitud")));
            float longitud = Float.parseFloat(Objects.requireNonNull(ctx.formParam("longitud")));

            Registro registro = new Registro(nombre, sector, nivelEscolar, UsuarioRegistrador, latitud, longitud);
            ServiciosRegistro.getInstance().guardarRegistro(registro);
            ServiciosRegistro.getInstance().imprimirTodosLosRegistros(); // Confirmar


            // Enviar una respuesta al cliente
            ctx.status(200);

        });


        /* REGISTRAR USUARIO */
        app.post("/registrar-usuario", ctx -> {
            // Obtener los datos del registro del cuerpo de la solicitud
            String username = ctx.formParam("username");
            String nombre = ctx.formParam("nombre");
            String contrasena = ctx.formParam("contrasena");
            String rol = ctx.formParam("rol");


            // CREAR USUARIO
            Usuario user = new Usuario(username, contrasena, nombre, rol); // todo
            ServiciosUsuario.getInstance().guardarUsuario(user);
            ServiciosUsuario.getInstance().imprimirTodosLosUsuarios(); // Confirmar



            // Enviar una respuesta al cliente
            ctx.status(200);
        });




        /* VER REGISTROS */
        app.get("/registros", ctx -> {
            // Obtener todos los registros
            List<Registro> registros = ServiciosRegistro.getInstance().obtenerTodosLosRegistros();

            // Convertir registros a JSON
            Gson gson = new Gson();
            String registrosJson = gson.toJson(registros);

            // Enviar respuesta al cliente
            ctx.result(registrosJson);
        });


























    }


















































    /***      FUNCIONES      ****/
    /* checkInternetConnection */
    public static boolean checkInternetConnection() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /*  */


    

}
