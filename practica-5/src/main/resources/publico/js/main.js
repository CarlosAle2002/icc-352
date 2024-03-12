// Crear la conexión WebSocket
const ws = new WebSocket('ws://localhost:3000'); // Reemplaza con la URL y puerto correctos

// Evento que se ejecuta cuando la conexión se establece
ws.addEventListener('open', (event) => {
    console.log('Conexión WebSocket establecida');

    // Ahora que la conexión está abierta, puedes enviar mensajes
   // sendMessage("Hola, servidor!");
});

// Evento que se ejecuta cuando se recibe un mensaje
ws.addEventListener('message', (event) => {
    console.log('Mensaje recibido:', event.data);

// Crear un nuevo elemento para el mensaje recibido
    var messageElement = document.createElement('div');
    messageElement.textContent = event.data;



  //  messageElement.className = 'message server-message'; // Estilo para mensajes del servidor
    messageElement.className = 'message server-message'; // Estilo para mensajes del servidor
    messageElement.style.textAlign = 'left'; // Alinear mensajes del servidor a la izquierda
    messageElement.style.backgroundColor = '#d3d3d3'; // Color de fondo para mensajes del servidor

    // Agregar el mensaje al contenedor del chat
    document.getElementById('messages').appendChild(messageElement);
});

// Evento que se ejecuta en caso de error
ws.addEventListener('error', (event) => {
    console.error('Error en la conexión WebSocket:', event);
});

// Evento que se ejecuta cuando la conexión se cierra
ws.addEventListener('close', (event) => {
    console.log('Conexión WebSocket cerrada');
});





// Función para enviar mensajes a través del WebSocket
function sendMessage(message) {
    if (ws.readyState === WebSocket.OPEN) {
        console.log("Enviando mensaje: " + message);
        ws.send(message);

        // Crear un nuevo elemento para el mensaje
        var messageElement = document.createElement('div');
        messageElement.textContent = message;
        messageElement.className = 'message user-message'; // Agregar la clase user-message

        // Agregar el mensaje al contenedor del chat
        document.getElementById('messages').appendChild(messageElement);

        // Limpiar el input después de enviar el mensaje
        document.getElementById('messageInput').value = '';

        // Enviar el mensaje al servidor WebSocket
        websocket.send(message); // Aquí, 'websocket' es tu objeto WebSocket

    } else {
        console.error("WebSocket aún no está en estado OPEN. No se puede enviar el mensaje.");
    }




}
