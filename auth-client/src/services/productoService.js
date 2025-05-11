// src/services/productoService.js
import { secureGetItem } from '../utils/secureStorage';

const API_URL = "http://localhost:8080/auth/producto";
const RECOMMENDED_URL = "http://localhost:8080/auth/producto/recomendados";
const TIPO_URL = "http://localhost:8080/auth/tipoproducto";
const getToken = () => secureGetItem('token');

// GET (público)
export const getAllProductos = async () => {
  const resp = await fetch(API_URL);
  if (!resp.ok) throw new Error('No se pudo obtener la lista de productos');
  return resp.json();
};

// GET recomendados por usuario
export const getRecommendedProductos = async (idUser) => {
  if (!idUser) return [];
  const resp = await fetch(`${RECOMMENDED_URL}/${idUser}`);
  if (resp.status === 204) return [];
  if (!resp.ok) throw new Error('No se pudieron obtener recomendaciones');
  return resp.json();
};

// GET por ID (público)
export const getProductoById = async (id) => {
  if (!id) throw new Error('ID no proporcionado');
  const resp = await fetch(`${API_URL}/${id}`);
  if (!resp.ok) throw new Error(`Producto ${id} no encontrado`);
  return resp.json();
};

// GET todos los Tipos de Producto
export const getAllTiposProductos = async () => {
  const resp = await fetch(TIPO_URL);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudieron obtener los tipos de producto: ${text}`);
  }
  return resp.json();
};

// POST (ADMIN)
export const createProducto = async (producto) => {
  const token = getToken();
  const payload = {
    ...producto,
    tipoProducto: { id: parseInt(producto.idTipoProducto, 10) }
  };
  if (!payload.tipoProducto.id) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }
  const headers = new Headers({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  });
  const resp = await fetch(API_URL, {
    method: 'POST', headers, body: JSON.stringify(payload)
  });
  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al crear producto: ${text}`);
  }
  return JSON.parse(text);
};

// PUT (ADMIN)
export const updateProducto = async (id, producto) => {
  if (!id) throw new Error('ID no proporcionado para update');
  const token = getToken();
  const payload = {
    ...producto,
    tipoProducto: { id: parseInt(producto.idTipoProducto, 10) }
  };
  if (!payload.tipoProducto.id) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }
  const headers = new Headers({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  });
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT', headers, body: JSON.stringify(payload)
  });
  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al actualizar producto: ${text}`);
  }
  return JSON.parse(text);
};

// DELETE (ADMIN)
export const deleteProducto = async (id) => {
  if (!id) throw new Error('ID no proporcionado para delete');
  const token = getToken();
  const headers = new Headers({ 'Authorization': `Bearer ${token}` });
  const resp = await fetch(`${API_URL}/${id}`, { method: 'DELETE', headers });
  if (!resp.ok) throw new Error(`Error al eliminar producto ${id}`);
  return { success: true };
};