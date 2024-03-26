// Importar LocalForage
//import localforage from 'localforage';
import localforage from 'localforage';

// Obtener datos del formulario
/*
const formData = {
    id: document.getElementById('id').value,
    nombre: document.getElementById('nombre').value,
    Sector: document.getElementById('Sector').value,
    NivelEscolar: document.getElementById('NivelEscolar').value,
    IdUsuarioRegistrador: document.getElementById('IdUsuarioRegistrador').value,
    latitud: document.getElementById('latitud').value,
    longitud: document.getElementById('longitud').value,
};
*/

// Almacenar datos en LocalForage
/*
localforage.setItem('registro-' + formData.id, formData).then(() => {
    console.log('Registro almacenado correctamente.');
}).catch((error) => {
    console.error('Error al almacenar el registro:', error);
});
*/

// Recuperar un registro por su ID
localforage.getItem('registro-1').then((registro) => {
    if (registro !== null) {
        console.log('Registro encontrado:', registro);
        // Mostrar el registro en la interfaz de usuario
    } else {
        console.log('Registro no encontrado.');
    }
}).catch((error) => {
    console.error('Error al recuperar el registro:', error);
});

// Iterar sobre todos los registros almacenados
localforage.iterate((value, key) => {
    console.log('Key:', key, 'Value:', value);
    // Mostrar cada registro en la interfaz de usuario
}).catch((error) => {
    console.error('Error al iterar sobre los registros:', error);
});


// Obtener todos los registros almacenados
localforage.iterate((value, key) => {
    // Enviar cada registro al servidor de Javalin
    fetch('http://tu-servidor-javalin.com/api/registros', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(value),
    })
        .then(response => {
            if (response.ok) {
                console.log('Registro enviado al servidor:', value);
                // Aquí puedes realizar alguna acción después de enviar el registro
            } else {
                console.error('Error al enviar el registro al servidor:', response.statusText);
                // Aquí puedes manejar el error de envío al servidor
            }
        })
        .catch(error => {
            console.error('Error al enviar el registro al servidor:', error);
            // Aquí puedes manejar el error de conexión o envío
        });
})
    .then(() => {
        console.log('Todos los registros enviados al servidor.');
    })
    .catch(error => {
        console.error('Error al obtener los registros:', error);
        // Aquí puedes manejar el error de obtención de registros
    });


/** Remover un registro **/
localforage.removeItem(idAEliminar).then(() => {
    console.log('Registro eliminado correctamente.');
}).catch((error) => {
    console.error('Error al eliminar el registro:', error);
});


/*** WED STORAGE - LOG DE LOGINS EXITOSOS ***/
// Función para almacenar el registro de inicio de sesión
/*
function almacenarInicioSesion(username) {
    // Función para obtener la fecha actual en formato ISO
    const getCurrentDate = () => new Date().toISOString();

    const registroInicioSesion = {
        username,
        fechaInicio: getCurrentDate(),
    };

    // Almacenar datos en LocalForage
    localforage.setItem('inicioSesion-' + username, registroInicioSesion)
        .then(() => {
            console.log('Registro de inicio de sesión almacenado correctamente.');
        })
        .catch((error) => {
            console.error('Error al almacenar el registro de inicio de sesión:', error);
        });
}
*/








