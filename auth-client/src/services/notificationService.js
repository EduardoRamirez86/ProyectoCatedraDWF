import { secureGetItem } from '../utils/secureStorage';

const API_URL = "http://localhost:8080/auth/notificacion";
const getToken = () => secureGetItem("token"); // Usar secure-ls

/**
 * Obtiene todas las notificaciones de un usuario.
 * @param {number} userId 
 * @returns {Promise<Array<NotificacionResponse>>}
 */
export const getUserNotifications = async (userId) => {
  if (!userId) throw new Error("ID de usuario no proporcionado");
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}/usuario/${userId}`, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
  });
  if (!resp.ok) {
    const txt = await resp.text();
    throw new Error(`Error al obtener notificaciones: ${txt}`);
  }
  return resp.json();
};

/**
 * Marca una notificación como leída.
 * @param {number} notificationId 
 * @returns {Promise<void>}
 */
export const markNotificationRead = async (notificationId) => {
  if (!notificationId) throw new Error("ID de notificación no proporcionado");
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}/leer/${notificationId}`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${token}`,
    },
  });
  if (!resp.ok) {
    const txt = await resp.text();
    throw new Error(`Error al marcar notificación leída: ${txt}`);
  }
};