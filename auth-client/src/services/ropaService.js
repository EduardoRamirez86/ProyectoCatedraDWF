const API_URL = "http://localhost:8080/auth/ropa";
const getToken = () => localStorage.getItem('token');

// GET (público)
export const getAllRopa = async () => {
  const resp = await fetch(API_URL);
  if (!resp.ok) throw new Error('No se pudo obtener la lista');
  return resp.json();
};

// GET por ID (público)
export const getRopaById = async (id) => {
  if (!id) throw new Error('ID no proporcionado');
  const resp = await fetch(`${API_URL}/${id}`);
  if (!resp.ok) throw new Error(`Prenda ${id} no encontrada`);
  return resp.json();
};

// POST (ADMIN)
export const createRopa = async (ropa) => {
  const token = getToken();
  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(ropa),
  });
  if (!resp.ok) throw new Error('Error al crear prenda');
  return resp.json();
};

// PUT (ADMIN) ← aquí va el token
export const updateRopa = async (id, ropa) => {
  if (!id) throw new Error('ID no proporcionado para update');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`  // ← Asegúrate de mandarlo
    },
    body: JSON.stringify(ropa),
  });
  if (!resp.ok) throw new Error(`Error al actualizar prenda ${id}`);
  return resp.json();
};

// DELETE (ADMIN)
export const deleteRopa = async (id) => {
  if (!id) throw new Error('ID no proporcionado para delete');
  const token = getToken();
  const resp = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  if (!resp.ok) throw new Error(`Error al eliminar prenda ${id}`);
  return { success: true };
};
