const API_URL  = "http://localhost:8080/auth/producto";
const TIPO_URL = "http://localhost:8080/auth/tipoproducto";
const getToken = () => localStorage.getItem('token');

// GET (público)
export const getAllProductos = async () => {
  const resp = await fetch(API_URL);
  if (!resp.ok) throw new Error('No se pudo obtener la lista de productos');
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
  // El backend espera recibir { ..., tipoProducto: { id: X } }
  const payload = {
    ...producto,
    tipoProducto: { id: parseInt(producto.idTipoProducto, 10) }
  };

  if (!payload.tipoProducto.id) {
    throw new Error('El campo "ID Tipo Producto" es obligatorio y debe ser un número válido.');
  }

  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
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

  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
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

// DELETE (ADMIN)
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
