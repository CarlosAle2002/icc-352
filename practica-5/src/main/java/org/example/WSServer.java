package org.example;

import java.security.SecureRandom;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WSServer extends WebSocketServer {

    // Lista para almacenar las conexiones activas
    private List<WebSocket> connections;
    private WebSocket actWebSocket;
    // Constructor de la clase que recibe una dirección IP y un puerto
    public WSServer(InetSocketAddress address) {
        super(address);
        connections = new ArrayList<>();
    }

    // Método invocado cuando se establece una nueva conexión WebSocket
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Agregar la nueva conexión a la lista
        connections.add(conn);
        System.out.println("Nueva conexión: " + conn.getRemoteSocketAddress() + " (Código de sesión: " + conn.hashCode() + ")");
        // ServiciosConversacion.getInstance().
        // Mostrar todas las conexiones activas
        printActiveConnections();


        int id = conn.hashCode();
        Usuario userAct = ServiciosUsuario.getInstance().GetUsuarioAct();
        if(userAct != null)
        {
            ServiciosUsuario.getInstance().GetUsuarioAct().setSesionId(id);

            ServiciosUsuario.getInstance().getUsuarioFalso().setSesionId(id); /* NEW */

            ServiciosUsuario.getInstance().addUsuarioConectado(userAct);
            System.out.println(userAct.getSesionId());

            // Imprimir todos los mensajes del usuario
            // imprimirMensajesPorUsuario(ServiciosUsuario.getInstance().GetUsuarioAct().getId());
        }




        // VER SI HAY UN USUARIO
        Usuario usuarioTest = ServiciosUsuario.getInstance().GetUsuarioAct();
        if(usuarioTest == null)
        {
            System.out.println("Usuario es nulo : CREAR USUARIO TEMPORAL");

            // CREAR UN USUARIO PARA LAS CONEXIONES SOLAMENTE
            String name = RandomNameGenerator.generateRandomName(10);
            Usuario usi = ServiciosUsuario.getInstance().CrearUsuario(name, "none", "ASAasSDF34TRY", false, false, conn.hashCode());


            ServiciosUsuario.getInstance().setUsuarioFalso(usi);
            ServiciosUsuario.getInstance().getUsuarioFalso().setSesionId(id); /* NEW */

            ServiciosUsuario.getInstance().addUsuarioConectado(usi);

        }


    }



    // Método invocado cuando se cierra una conexión WebSocket
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {


        // Eliminar la conexión cerrada de la lista
        connections.remove(conn);
        System.out.println("Conexión cerrada: " + conn.getRemoteSocketAddress());

        //todo: revisar esto, puede que eleiminar el usuario no sea lo mejor
        //ServiciosUsuario.getInstance().eliminarUsuarioConectado(ServiciosUsuario.getInstance().GetUsuarioAct().getUsername());


       ServiciosUsuario.getInstance().eliminarUsuarioConectado(ServiciosUsuario.getInstance().getUsuarioFalso().getUsername());
      // ServiciosUsuario.getInstance().setUsuarioFalso(null);  /* NEW */

        // Mostrar todas las conexiones activas
        printActiveConnections();


    }

    /* USUARIO ENVIA MENSAJE */
    // Método invocado cuando se recibe un mensaje WebSocket
    @Override
    public void onMessage(WebSocket conn, String message) {
        //sendMessageToClient(conn, "tuti");
        // Se muestra en consola el mensaje recibido junto con el nombre de usuario del usuario actual
       // System.out.println("Mensaje recibido de " + ServiciosUsuario.getInstance().GetUsuarioAct().getUsername() + ": " + message);
        System.out.println("Mensaje recibido de " + ServiciosUsuario.getInstance().getUsuarioFalso().getUsername() + ": " + message);
        // Aquí puedes procesar el mensaje y enviar respuestas si es necesario

        connections.add(conn);
        printActiveConnections();

        // Agregar un mensaje para probar
        // Mensaje newMensaje = ServiciosMensaje.getInstance().enviarMensaje( ServiciosUsuario.getInstance().GetUsuarioAct().getId(),"Tururlu", false);
       // ServiciosMensaje.getInstance().enviarMensaje( ServiciosUsuario.getInstance().GetUsuarioAct().getId(),message, false);
        ServiciosMensaje.getInstance().enviarMensaje( ServiciosUsuario.getInstance().getUsuarioFalso().getId(),message, false);

        // Imprimir todos los mensajes del usuario
      //  imprimirMensajesPorUsuario(ServiciosUsuario.getInstance().GetUsuarioAct().getId());
        imprimirMensajesPorUsuario(ServiciosUsuario.getInstance().getUsuarioFalso().getId());
    }

    // Método invocado en caso de error en la conexión WebSocket
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error en la conexión de " + conn.getRemoteSocketAddress() + ": " + ex);
    }

    // Método invocado cuando el servidor WebSocket se inicia con éxito
    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado con éxito!");
    }

    // Método para imprimir todas las conexiones activas
    private void printActiveConnections() {
        System.out.println("Conexiones activas:");
        for (WebSocket conn : connections) {
            //System.out.println("- " + conn.getRemoteSocketAddress());
            System.out.println("- " + conn.hashCode());
        }
    }


    /* SERVIDOR ENVIA MENSAJE */
    // Método para enviar un mensaje a un cliente específico
    public void sendMessageToClient(WebSocket client, String message) {


        //ServiciosMensaje.getInstance().enviarMensaje( ServiciosUsuario.getInstance().GetUsuarioAct().getId(),message, true);
        //ServiciosMensaje.getInstance().enviarMensaje( ServiciosUsuario.getInstance().getUsuarioFalso().getId(),message, true);



        /*
        if(client != null)
            client.send(message);
        assert client != null;
        System.out.println("Mensaje enviado al cliente " + client.getRemoteSocketAddress() + ": " + message);
   */
    }

    // Método para obtener las conexiones activas
    public List<WebSocket> getActiveConnections() {
        return connections;
    }

    // Método principal que se ejecuta al iniciar la aplicación
    public static void main(String[] args) {
        int wsPort = 3000; // Puerto en el que escuchará el servidor WebSocket
        // Se crea una instancia del servidor WebSocket en el puerto especificado
        WSServer wsServer = new WSServer(new InetSocketAddress(wsPort));
        // Se inicia el servidor WebSocket
        wsServer.start();





    }

    public void imprimirMensajesPorUsuario(Long idUsuario) {
        List<Mensaje> mensajes = ServiciosMensaje.getInstance().obtenerMensajesPorUsuario(idUsuario);
        if (mensajes != null) {
            System.out.println("Mensajes del usuario con ID " + idUsuario + ":");
            for (Mensaje mensaje : mensajes) {
                System.out.println("ID: " + mensaje.getIdConeccion() + ", Contenido: " + mensaje.getContenido());
            }
        } else {
            System.out.println("No se encontraron mensajes para el usuario con ID " + idUsuario);
        }
    }


    public WebSocket getClientByHashCode(int hashCode) {
        System.out.println("Estoy en getClientByHashCode!!!!!!!!!!!!");

        for (WebSocket conn : connections) {
            System.out.println("HASH CODE: " + conn.hashCode());
            if (conn.hashCode() == hashCode) {
                return conn;
            }
        }
        return null; // Retorna null si no se encuentra el cliente con el hashCode especificado
    }



    public static class RandomNameGenerator {
        private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
        private static final SecureRandom random = new SecureRandom();

        public static String generateRandomName(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                sb.append(randomChar);
            }
            return sb.toString();
        }

        public static void main(String[] args) {
            // Ejemplo de uso
            String randomName = generateRandomName(10); // Generar un nombre de 10 caracteres
            System.out.println("Nombre aleatorio: " + randomName);
        }
    }
    public boolean isConnectionActive(int hashCode) {
        for (WebSocket conn : connections) {
            System.out.println("HASHI : " + conn.hashCode());
            if (conn.hashCode() == hashCode) {
                return true;
            }
        }
        return false;
    }

}
