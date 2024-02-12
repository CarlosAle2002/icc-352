package Logico;

import java.util.*;

public class Controladora {

    private static Controladora controladora;
    private static Usuario userLogeado;
    private static List<Usuario> listaUsuarios;

    private Controladora(){
        listaUsuarios = new ArrayList<Usuario>();
    }
    public static Controladora getInstance() {
        if (controladora == null) {
            controladora = new Controladora();
            listaUsuarios = new ArrayList<>();
            userLogeado = new Usuario(null,null);
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

    public static boolean validarUsuario(String username, String password, List<Usuario> usuarios) {
        for(Usuario user: usuarios) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
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
    public void deslogearUsuario(){
        userLogeado = new Usuario(null,null);
    }
}

