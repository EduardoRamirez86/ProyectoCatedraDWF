import { secureGetItem } from '../utils/secureStorage';

const API_URL = 'http://localhost:8080/auth/historial-puntos';
const getToken = () => secureGetItem('token');

/**
 * Obtiene el historial de puntos paginado
 * @param {number} page - Página solicitada (default: 0)
 * @param {number} size - Tamaño de la página (default: 10)
 */
const getHistorialPuntos = async (page = 0, size = 10) => {
  if (typeof page !== 'number' || typeof size !== 'number') {
    throw new Error('Los parámetros page y size deben ser números');
  }

  const token = getToken();
  if (!token) throw new Error('No autenticado');

  const url = `${API_URL}?page=${page}&size=${size}`;
  const resp = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al obtener historial de puntos');
  }

  return resp.json();
};

/**
 * Obtiene un registro de historial de puntos por ID
 * @param {number} id - ID del registro
 */
const getHistorialPuntosById = async (id) => {
  if (typeof id !== 'number') {
    throw new Error('El parámetro id debe ser un número');
  }

  const token = getToken();
  if (!token) throw new Error('No autenticado');

  const url = `${API_URL}/${id}`;
  const resp = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al obtener detalle de puntos');
  }

  return resp.json();
};

/**
 * Obtiene el historial de puntos de un usuario específico (paginado)
 * @param {number} userId - ID del usuario
 * @param {number} page - Página solicitada (default: 0)
 * @param {number} size - Tamaño de la página (default: 10)
 */
export const getHistorialPuntosByUser = async (userId, page = 0, size = 10) => {
  if (typeof userId !== 'number') throw new Error('El parámetro userId debe ser un número');
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const url = `${API_URL}/usuario/${userId}?page=${page}&size=${size}`;
  const resp = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al obtener historial de puntos');
  }
  return resp.json();
};

export default getHistorialPuntos;
export { getHistorialPuntosById };
