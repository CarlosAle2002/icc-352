package org.example.controladores;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import org.example.encapsulaciones.Estudiante;
import org.example.util.BaseControlador;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantillasControlador extends BaseControlador {
    public PlantillasControlador(Javalin app) {
        super(app);
        this.registrandoPlantillas();
    }

    private void registrandoPlantillas() {
        JavalinRenderer.register(new JavalinThymeleaf());
    }

    public void aplicarRutas() {
        app.routes(() -> {
            /**
             * Cada sistema de plantilla incluye etiquetas y tiene su forma de trabajo:
             * ir a https://freemarker.apache.org/docs/dgui.html
             */
            ApiBuilder.path("/freemarker", () -> {
                /**
                 * Validando el sistema de plantilla
                 * Ir a: http://localhost:7000/freemarker/datosEstudiante/20011136
                 */
                ApiBuilder.get("/datosEstudiante/{matricula}", (ctx) -> {
                    int matricula = (Integer)ctx.pathParamAsClass("matricula", Integer.class).get();
                    Estudiante estudiante = new Estudiante(matricula, "Estudiante matricula: " + matricula, "ISC");
                    //
                    Map<String, Object> modelo = new HashMap();
                    modelo.put("estudiante", estudiante);
                    //enviando al sistema de plantilla.
                    ctx.render("/templates/freemarker/datosEstudiante.ftl", modelo);
                });
            });

            /**
             * Cada sistema de plantilla incluye etiquetas y tiene su forma de trabajo:
             * ir a https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
             */
            ApiBuilder.path("/thymeleaf", () -> {

                /**
                 * http://localhost:7000/thymeleaf/
                 */
                ApiBuilder.get("/", (ctx) -> {
                    List<Estudiante> listaEstudiante = this.getEstudiantes();
                    Map<String, Object> modelo = new HashMap();
                    modelo.put("titulo", "Ejemplo de funcionalidad Thymeleaf");
                    modelo.put("listaEstudiante", listaEstudiante);

                    //
                    ctx.render("/templates/thymeleaf/funcionalidad.html", modelo);
                });
            });

            /**
             * Cada sistema de plantilla incluye etiquetas y tiene su forma de trabajo:
             * ir a https://velocity.apache.org/engine/2.2/user-guide.html
             */
            ApiBuilder.path("/velocity", () -> {

                /**
                 * http://localhost:7000/velocity/
                 */
                ApiBuilder.get("/", (ctx) -> {
                    //listando los estudiantes..
                    List<Estudiante> listaEstudiante = this.getEstudiantes();

                    Map<String, Object> modelo = new HashMap();
                    modelo.put("titulo", "Ejemplo de funcionalidad Velocity");
                    modelo.put("listaEstudiante", listaEstudiante);

                    //
                    ctx.render("/templates/velocity/funcionalidad.vm", modelo);
                });
            });
        });
    }

    private @NotNull List<Estudiante> getEstudiantes() {
        //listando los estudiantes..
        List<Estudiante> listaEstudiante = new ArrayList();
        listaEstudiante.add(new Estudiante(20011136, "Carlos Camacho", "ITT"));
        listaEstudiante.add(new Estudiante(20011137, "Otro Estudiante", "ISC"));
        listaEstudiante.add(new Estudiante(20011138, "Otro otro", "ISC"));
        return listaEstudiante;
    }
}
