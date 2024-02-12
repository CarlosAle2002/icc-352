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
        app.get("/", ctx -> ctx.redirect("/login.html"));

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            boolean resp = Controladora.validarUsuario(username,password,Controladora.getInstance().getListaUsuarios());

            if (resp) {
                ctx.sessionAttribute("username", username);
                ctx.sessionAttribute("password", password);
            }
            Controladora.getInstance().setUserLogeado(Controladora.buscarUsuario(username,password,Controladora.getInstance().getListaUsuarios()));

            ctx.json(resp);

        });

        app.post("/registrarUsuario", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            Usuario user = new Usuario(username, password);
            Controladora.getInstance().agregarUsuario(user);
            Controladora.getInstance().setUserLogeado(user);
        });

        app.post("/user", ctx -> {
            String nombreUsuario = ctx.formParam("nombreUsuario");
            Usuario user = Controladora.getInstance().getUserLogueado();
            ctx.json(user.getUsername());
        });

        app.get("/obtenerUsuario", ctx -> {
            String usuarioJson = convertirUsuarioAJson(Controladora.getInstance().getUserLogueado());
            ctx.json(usuarioJson);
        });

        app.post("/logout", ctx -> {
            String username = ctx.formParam("username");
            Controladora.getInstance().deslogearUsuario();
        });
    }

    private static String convertirUsuarioAJson(Usuario usuario) {
        return "{ \"username\": \"" + usuario.getUsername() + "\", \"contrasena\": \"" + usuario.getPassword() + "\" }";
    }
}

