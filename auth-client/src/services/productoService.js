import { secureGetItem } from '../utils/secureStorage';

const API_URL  = "http://localhost:8080/auth/producto";
const TIPO_URL = "http://localhost:8080/auth/tipoproducto";
const getToken = () => secureGetItem('token');

/**
 * Admin / público: obtiene productos paginados
 * @param {number} page Índice de página (0-based)
 * @param {number} size Tamaño de página
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
  const items    = data._embedded?.productoResponseList || [];
  const pageInfo = data.page || {};

  return {
    items,
    page:          pageInfo.number     ?? 0,
    size:          pageInfo.size       ?? size,
    totalPages:    pageInfo.totalPages ?? 1,
    totalElements: pageInfo.totalElements ?? items.length,
  };
};

/** GET todos los productos (sin paginar) *//** GET todos los productos (sin paginar) */
export const getAllProductos = async () => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}/all`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });

  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudo obtener la lista de productos: ${text}`);
  }

  const data = await resp.json();
  const items = data._embedded?.productoResponseList || [];

  return items; // <- devuelve el array de productos
};



/** GET recomendados por usuario */
export const getRecommendedProductos = async (idUser) => {
  if (!idUser) return [];

  const resp = await fetch(`http://localhost:8080/auth/producto/recomendados/${idUser}`);
  
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
      'Content-Type':  'application/json',
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
      'Content-Type':  'application/json',
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
