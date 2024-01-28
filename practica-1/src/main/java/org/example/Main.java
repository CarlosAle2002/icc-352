package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserte la URL a analizar: ");
        String url = scanner.nextLine();

        try {
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            System.out.println("Tipo de recurso de la URL: " + respuesta.headers().firstValue("Content-Type").orElse("Desconocido"));

            // Aqui revisamos de que sea de tipo HTML y si es el caso hacemos lo que se pide en la consigna
            if (respuesta.headers().firstValue("Content-Type").orElse("").contains("text/html")) {
                // Revisamos la cantidad de lineas del recurso HTML, siendo la parte 1 de la tarea
                int lineas = contarLineas(respuesta.body());
                System.out.println("Cantidad de líneas del recurso HTML: " + lineas);

                // Revisamos la cantidad de parrafos que tiene el HTML, siendo la parte 2 de la tarea
                int parafos = contarParrafos(respuesta.body());
                System.out.println("Cantidad de párrafos del recurso HTML: " + parafos);

                // Revisamos la cantidad de imagenes dentro de parrafos que tiene el HTML, siendo la parte 3 de la tarea
                int imagenesEnParrafos = contarImagenesEnParrafos(respuesta.body());
                System.out.println("Cantidad de imágenes dentro de los párrafos: " + imagenesEnParrafos);

                // Revisamos la cantidad de formularios y categorizar por método (POST o GET)
                contarFormularios(respuesta.body(),url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // Esta funcion sirve para contar las lineas en el string recibido
    private static int contarLineas(String htmlRecibido) {
        String[] lineasTotales = htmlRecibido.split("\r\n|\r|\n");
        return lineasTotales.length;
    }

    // Esta funcion sirve para contar la cantidad de elementos <p> en el string recibido
    private static int contarParrafos(String htmlRecibido) {
        Document documento = Jsoup.parse(htmlRecibido);
        Elements parrafosTotales = documento.select("p");
        return parrafosTotales.size();
    }

    // Esta funcion sirve para contar la cantidad de imagenes que se encuentran dentro de los parrafos en el string recibido
    private static int contarImagenesEnParrafos(String htmlRecibido) {
        Document documento = Jsoup.parse(htmlRecibido);
        Elements paragrafos = documento.select("p");
        int imagenesContadas = 0;

        for (Element paragrafo : paragrafos) {
            Elements imagenes = paragrafo.select("img");
            imagenesContadas += imagenes.size();
        }

        return imagenesContadas;
    }

    /* Esta funcion sirve para contar la cantidad de elementos (form) en el string recibido, si se encuentra un formulario pero no es ni post ni get, solo se tendra en cuenta para el total de formularios
       Ademas de llamar a otras funciones para cumplir con otras partes de la consigna
    */
    private static void contarFormularios(String htmlRecibido, String url) {
        Document documento = Jsoup.parse(htmlRecibido);
        Elements formularios = documento.select("form");

        int postTotales = 0;
        int getTotales = 0;

        for (Element formulario : formularios) {
            String metodo = formulario.attr("method").toUpperCase();

            if ("POST".equals(metodo)) {
                postTotales++;
                mostrarCamposInput(formulario);
                try {
                    enviarPeticionPost(formulario, new URI(url));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if ("GET".equals(metodo)) {
                getTotales++;
            }

            //mostrarCamposInput(formulario);
        }

        System.out.println("Cantidad total de formularios: " + formularios.size());
        System.out.println("Cantidad de formularios con método POST: " + postTotales);
        System.out.println("Cantidad de formularios con método GET: " + getTotales);
    }

    // Esta función muestra los campos de tipo "input" y sus tipos dentro de un formulario
    private static void mostrarCamposInput(Element formulario) {
        Elements camposInput = formulario.select("input");

        System.out.println("Campos de tipo 'input' en el formulario:");
        for (Element input : camposInput) {
            String tipo = input.attr("type");
            System.out.println("    - Tipo: " + tipo);
        }
        System.out.println();
    }
    // Esta funcion sirve para enviar una peticion a la url con la informacion que se pide en la consigna (asignatura y matricula-Id)
    private static void enviarPeticionPost(Element formulario, URI baseUri) {
        String asignatura = "practica1";
        String matriculaId = "10144554";

        String parametros = "asignatura=" + asignatura;

        String action = formulario.attr("action");
        URI uri;
        try {
            uri = baseUri.resolve(new URI(action));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("matricula-id", matriculaId)
                .POST(HttpRequest.BodyPublishers.ofString(parametros))
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta del servidor:");
            HttpHeaders headers = response.headers();
            headers.map().forEach((key,value)->System.out.println(key+":"+value));
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
