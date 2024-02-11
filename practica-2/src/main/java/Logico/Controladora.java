package Logico;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.text.SimpleDateFormat;

public class Controladora {

    private static Controladora controladora;
    private static Usuario userLogeado;

    private static Articulo articuloSeleccionado;

    private static List<Usuario> listaUsuarios;

    private static List<Comentario> listaComentarios;

    private static List<Articulo> listaArticulos;

    private static List<Etiqueta> listaEtiquetas;


    private Controladora(){
        listaUsuarios = new ArrayList<Usuario>();
    }
    public static Controladora getInstance() {
        if (controladora == null) {
            controladora = new Controladora();
            listaUsuarios = new ArrayList<>();
            listaComentarios = new ArrayList<>();
            listaEtiquetas = new ArrayList<>();
            listaArticulos = new ArrayList<>();
            userLogeado = new Usuario(null,null,null,false,false);
        }
        return controladora;
    }

    public void agregarUsuario(Usuario user){
        listaUsuarios.add(user);
    }

    public List<Usuario> getListaUsuarios(){
        return listaUsuarios;
    }

    public static Usuario buscarUsuario(String username, String password, List<Usuario> usuarios) {
        for(Usuario user: usuarios){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }


    /*Si el usuario no existe devuelve 0
      Si el usuario existe, es admin y autor devuelve 1
      Si el usuario existe, es admin, pero no autor devuelve 2
      Si el usuario existe, no es admin, pero si autor devuelve 3
      Si el usuario existe, no es admin, ni autor, devuelve 4
    */
    public static int validarUsuario(String username, String password, List<Usuario> usuarios) {
        for(Usuario user: usuarios){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if(user.isAdministrador()){
                    if(user.isAutor()){
                        return 1;
                    }
                    return 2;
                } else if (user.isAutor()) {
                    return 3;
                }
                return 4;
            }
        }
        return 0;
    }

    public static Usuario buscarUsuarioByUsername(String username, List<Usuario> usuarios){
        for(Usuario user: usuarios){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    public void setUserLogeado(Usuario user){
        userLogeado = user;
    }

    public Usuario getUserLogueado(){
        return userLogeado;
    }

    public void setArticuloSeleccionado(Articulo articulo){
        articuloSeleccionado = articulo;
    }

    public Articulo getArticuloSeleccionado(){
        return articuloSeleccionado;
    }

    public void deslogearUsuario(){
        userLogeado = new Usuario(null,null,null,false,false);
    }

    public Articulo crearArticulo(String titulo, String contenido, String stringTags, Usuario autor){
        Date date = new Date();

        String[] tagsSeparados = stringTags.split(", ");

        List<Etiqueta> tags = new ArrayList<>();
        for(String tag: tagsSeparados){
            boolean resp = validarEtiquetaPorContenido(tag);
            if(resp == true){
                Etiqueta tagActual = buscarEtiquetaPorContenido(tag);
                listaEtiquetas.add(tagActual);
                tags.add(tagActual);
            } else {
                long idTag = crearTagId();
                Etiqueta tagActual = new Etiqueta(idTag,tag);
                listaEtiquetas.add(tagActual);
                tags.add(tagActual);
            }
        }

        long id = crearArticuloId();

        List<Comentario> articuloComentarios = new ArrayList<>();

        Articulo articulo = new Articulo(id,titulo,contenido,autor,date,articuloComentarios,tags);
        if(validarArticulo(articulo)){
            listaArticulos.add(articulo);
            return articulo;
        }
        return null;
    }

    public void modificarArticulo(String titulo, String contenido, String stringTags){

        String[] tagsSeparados = stringTags.split(", ");

        List<Etiqueta> tags = new ArrayList<>();
        for(String tag: tagsSeparados){
            boolean resp = validarEtiquetaPorContenido(tag);
            if(resp == true){
                Etiqueta tagActual = buscarEtiquetaPorContenido(tag);
                listaEtiquetas.add(tagActual);
                tags.add(tagActual);
            } else {
                long idTag = crearTagId();
                Etiqueta tagActual = new Etiqueta(idTag,tag);
                listaEtiquetas.add(tagActual);
                tags.add(tagActual);
            }
        }

        articuloSeleccionado.setTitulo(titulo);
        articuloSeleccionado.setCuerpo(contenido);
        articuloSeleccionado.setListaEtiqueta(tags);
    }

    private boolean validarArticulo(Articulo articulo) {
        for(Articulo arti: listaArticulos){
            if(arti.getTitulo().equals(articulo.getTitulo()) && arti.getCuerpo().equals(articulo.getCuerpo())){
                return false;
            }
        }
        return true;
    }

    private Etiqueta buscarEtiquetaPorContenido(String tag) {
        for(Etiqueta etiqueta: listaEtiquetas){
            if(etiqueta.getEtiqueta().equals(tag)){
                return etiqueta;
            }
        }
        return null;
    }

    private Articulo buscarArticuloPorTitulo(String titulo) {
        for(Articulo articulo: listaArticulos){
            if(articulo.getTitulo().equals(titulo)){
                return articulo;
            }
        }
        return null;
    }

    Articulo buscarArticuloPorId(long id) {
        for(Articulo articulo: listaArticulos){
            if(articulo.getId() == id){
                return articulo;
            }
        }
        return null;
    }

    private boolean validarEtiquetaPorContenido(String tag) {
        for(Etiqueta etiqueta: listaEtiquetas){
            if(etiqueta.getEtiqueta().equals(tag)){
                return true;
            }
        }
        return false;
    }

    public long crearTagId(){
        return listaEtiquetas.size();
    }

    public long crearArticuloId(){
        return listaArticulos.size();
    }

    public long crearComentarioId(){
        return listaEtiquetas.size();
    }

    public int getCantidadArticulos() {
        return listaArticulos.size();
    }

    public List<Articulo> getListaArticulo() {
        return listaArticulos;
    }

    public Articulo buscarArticuloPorTituloYCuerpo(String titulo, String cuerpo) {
        for(Articulo arti: listaArticulos){
            if(arti.getTitulo().equals(titulo) && arti.getCuerpo().equals(cuerpo)){
                return arti;
            }
        }
        return null;
    }
}

