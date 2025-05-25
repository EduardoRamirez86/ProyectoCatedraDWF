import { secureGetItem } from '../utils/secureStorage';

const API_URL = 'http://localhost:8080/auth/historial-pedidos';
const getToken = () => secureGetItem('token');

/**
 * Obtiene el historial de pedidos paginado
 * @param {number} page - Página solicitada (default: 0)
 * @param {number} size - Tamaño de la página (default: 10)
 */
export const getHistorialPedidos = async (page = 0, size = 10) => {
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
    throw new Error(text || 'Error al obtener historial de pedidos');
  }

  return resp.json();
};

/**
 * Obtiene un registro de historial de pedido por ID
 * @param {number} id - ID del registro
 */
export const getHistorialPedidoById = async (id) => {
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
    throw new Error(text || 'Error al obtener detalle de pedido');
  }

  return resp.json();
};
