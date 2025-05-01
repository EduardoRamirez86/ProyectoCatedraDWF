const API_URL = "http://localhost:8080/auth/tipoproducto";
const getToken = () => localStorage.getItem('token');

// GET (público)
export const getAllTiposProductos = async () => {
    const resp = await fetch(API_URL);
    if (!resp.ok) {
      const text = await resp.text();
      throw new Error(`No se pudieron obtener tipos: ${text}`);
    }
    return resp.json();
  };
  

// GET por ID (público)
export const getTipoProductoById = async (id) => {
  if (!id) throw new Error('ID no proporcionado');
  const resp = await fetch(`${API_URL}/${id}`);
  if (!resp.ok) throw new Error(`TipoProducto ${id} no encontrado`);
  return resp.json();
};

// POST (ADMIN)
export const createTipoProducto = async (tipoProducto) => {
  const token = getToken();
  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(tipoProducto),
  });

  const responseText = await resp.text();
  if (!resp.ok) throw new Error('Error al crear tipo de producto: ' + responseText);
  return JSON.parse(responseText);
};

// PUT (ADMIN)
export const updateTipoProducto = async (id, tipoProducto) => {
  if (!id) throw new Error('ID no proporcionado para update');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(tipoProducto),
  });

  const responseText = await resp.text();
  if (!resp.ok) throw new Error('Error al actualizar tipo de producto: ' + responseText);
  return JSON.parse(responseText);
};

// DELETE (ADMIN)
export const deleteTipoProducto = async (id) => {
  if (!id) throw new Error('ID no proporcionado para delete');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });

  if (!resp.ok) throw new Error(`Error al eliminar tipo de producto ${id}`);
  return { success: true };
};
