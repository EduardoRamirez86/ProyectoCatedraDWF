import { secureGetItem } from '../utils/secureStorage';
import { BASE_API_URL } from '../config/apiConfig'; // <-- IMPORTAMOS LA URL BASE

// Paso 1: Definir las URLs base a partir de BASE_API_URL.
// La mayoría de tus URLs empiezan con /auth/producto o /auth/tipoproducto.

// Eliminamos '/auth' de BASE_API_URL para obtener la base: https://figurately-sinuous-isla.ngrok-free.dev
const BASE_URL_ROOT = BASE_API_URL.replace('/auth', ''); 

// Construcción de los endpoints:
const API_URL  = `${BASE_URL_ROOT}/producto`;     // Queda: https://...ngrok.dev/producto
const TIPO_URL = `${BASE_URL_ROOT}/tipoproducto`;

const getToken = () => secureGetItem('token');

/**
 * Admin / público: obtiene productos paginados (ESTE ES EL QUE DEBES USAR EN ProductoCrud)
 * @param {number} page Índice de página (0-based)
 * @param {number} size Tamaño de página
 * @returns { items, page, size, totalPages, totalElements }
 */
export const getAllProductosPaged = async (page = 0, size = 10) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const params = new URLSearchParams({ page, size });
  const resp = await fetch(`${API_URL}/all?${params}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });

  if (!resp.ok) {
    const msg = await resp.text();
    throw new Error(`Error al cargar productos: ${msg}`);
  }

  const data = await resp.json();
  const items    = data._embedded?.productoResponseList || [];
  const pageInfo = data.page || {};

  return {
    items,
    page:          pageInfo.number     ?? 0,
    size:          pageInfo.size       ?? size,
    totalPages:    pageInfo.totalPages ?? 1,
    totalElements: pageInfo.totalElements ?? items.length,
  };
};

/** GET todos los productos (sin paginar) */
export const getAllProductos = async () => {
  // No requiere token ni header especial
  const resp = await fetch(`${API_URL}/all`);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudo obtener la lista de productos: ${text}`);
  }
  const data = await resp.json();
  // Devuelve solo el array de productos
  return data._embedded?.productoResponseList || [];
};



/** GET recomendados por usuario */
export const getRecommendedProductos = async (idUser) => {
  if (!idUser) return [];

  // 🔴 CORRECCIÓN: Usar API_URL centralizada en lugar de localhost:8080
  const resp = await fetch(`${API_URL}/recomendados/${idUser}`);
  
  if (resp.status === 204 || resp.status === 404) return [];

  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudieron obtener recomendaciones: ${text}`);
  }

  const data = await resp.json();
  const items = data._embedded?.productoResponseList || [];

  return items;
};

/** GET por ID */
export const getProductoById = async (id) => {
  if (!id) throw new Error('ID no proporcionado');
  const resp = await fetch(`${API_URL}/${id}`);
  if (!resp.ok) throw new Error(`Producto ${id} no encontrado`);
  return resp.json();
};

/** GET todos los Tipos de Producto */
export const getAllTiposProductos = async () => {
  const resp = await fetch(TIPO_URL);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudieron obtener los tipos de producto: ${text}`);
  }
  return resp.json();
};

/** POST (ADMIN): crea un producto */
export const createProducto = async (producto) => {
  const token = getToken();
  const payload = {
    ...producto,
    idTipoProducto: parseInt(producto.idTipoProducto, 10)
  };

  if (!payload.idTipoProducto) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }

  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type':  'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(payload)
  });

  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al crear producto: ${text}`);
  }
  return JSON.parse(text);
};

/** PUT (ADMIN): actualiza un producto */
export const updateProducto = async (id, producto) => {
  if (!id) throw new Error('ID no proporcionado para update');
  const token = getToken();
  const payload = {
    ...producto,
    idTipoProducto: parseInt(producto.idTipoProducto, 10)
  };

  if (!payload.idTipoProducto) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }

  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type':  'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(payload)
  });

  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al actualizar producto: ${text}`);
  }
  return JSON.parse(text);
};

/** DELETE (ADMIN): elimina un producto */
export const deleteProducto = async (id) => {
  if (!id) throw new Error('ID no proporcionado para delete');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!resp.ok) throw new Error(`Error al eliminar producto ${id}`);
  return { success: true };
};