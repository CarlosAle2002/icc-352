package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.example.controladores.CrudTradicionalControlador;
import org.example.controladores.PlantillasControlador;
import org.example.servicios.BootStrapServices;
import org.example.servicios.DataBaseServices;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        //Iniciando la base de datos con bootstrap
        BootStrapServices.startDb();

        //Creando la instancia de la base de datos y testeando la coneccion
        DataBaseServices.getInstancia().testConexion();

        //Creando las tablas
        BootStrapServices.crearTablas();

        //Creando la instancia del servidor y configurando.
        Javalin app = Javalin.create((config) -> {
            //configurando los documentos estaticos.
            config.staticFiles.add((staticFileConfig) -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });
        }).start(7000);;

        //Seteando las rutas que se usaran en la aplicacion
        (new CrudTradicionalControlador(app)).aplicarRutas();
        (new PlantillasControlador(app)).aplicarRutas();

        //redirigiendo al usuario a la pantalla inicial, listar estudiantes
        app.get("/", (ctx) -> {
            ctx.redirect("/crud-simple");
        });
    }
}