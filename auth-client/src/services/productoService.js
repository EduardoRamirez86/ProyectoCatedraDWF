// src/services/productoService.js

const API_URL  = "http://localhost:8080/auth/producto";
const TIPO_URL = "http://localhost:8080/auth/tipoproducto";
const getToken = () => localStorage.getItem('token');

// GET (público) — Todos los productos
export const getAllProductos = async () => {
  const resp = await fetch(API_URL);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudo obtener la lista de productos: ${text}`);
  }
  return resp.json();
};

// GET por ID (público)
export const getProductoById = async (id) => {
  if (!id) throw new Error('ID no proporcionado');
  const resp = await fetch(`${API_URL}/${id}`);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`Producto ${id} no encontrado: ${text}`);
  }
  return resp.json();
};

// GET (público) — Todos los tipos de producto
export const getAllTiposProductos = async () => {
  const resp = await fetch(TIPO_URL);
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`No se pudieron obtener los tipos de producto: ${text}`);
  }
  return resp.json();
};

// POST (ADMIN) — Crear producto
export const createProducto = async (producto) => {
  const token = getToken();
  // Armamos el payload con idTipoProducto directamente
  const payload = {
    nombre:          producto.nombre,
    descripcion:     producto.descripcion,
    precio:          parseFloat(producto.precio),
    costo:           parseFloat(producto.costo),
    cantidad:        parseInt(producto.cantidad, 10),
    cantidadPuntos:  parseInt(producto.cantidadPuntos, 10),
    imagen:          producto.imagen,
    idTipoProducto:  parseInt(producto.idTipoProducto, 10),
  };

  if (isNaN(payload.idTipoProducto)) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }

  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type':  'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al crear producto: ${text}`);
  }
  return JSON.parse(text);
};

// PUT (ADMIN) — Actualizar producto
export const updateProducto = async (id, producto) => {
  if (!id) throw new Error('ID no proporcionado para update');
  const token = getToken();

  const payload = {
    nombre:          producto.nombre,
    descripcion:     producto.descripcion,
    precio:          parseFloat(producto.precio),
    costo:           parseFloat(producto.costo),
    cantidad:        parseInt(producto.cantidad, 10),
    cantidadPuntos:  parseInt(producto.cantidadPuntos, 10),
    imagen:          producto.imagen,
    idTipoProducto:  parseInt(producto.idTipoProducto, 10),
  };

  if (isNaN(payload.idTipoProducto)) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }

  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type':  'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  const text = await resp.text();
  if (!resp.ok) {
    throw new Error(`Error al actualizar producto: ${text}`);
  }
  return JSON.parse(text);
};

// DELETE (ADMIN) — Borrar producto
export const deleteProducto = async (id) => {
  if (!id) throw new Error('ID no proporcionado para delete');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`Error al eliminar producto ${id}: ${text}`);
  }
  return { success: true };
};
