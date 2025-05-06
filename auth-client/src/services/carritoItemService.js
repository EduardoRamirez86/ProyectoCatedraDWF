const API_URL = "http://localhost:8080/auth/carrito-item";
const getToken = () => localStorage.getItem('token');

// Agrega un ítem al carrito
export const addCarritoItem = async (item) => {
  const { idCarrito, idProducto, cantidad } = item;
  if (!idCarrito || !idProducto || !cantidad) {
    console.error('Error: Faltan campos en el ítem:', { idCarrito, idProducto, cantidad });
    throw new Error('Faltan campos en el ítem (idCarrito, idProducto, cantidad)');
  }
  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`,
    },
    body: JSON.stringify({ idCarrito, idProducto, cantidad }),
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al agregar ítem: ${text}`);
  return JSON.parse(text);
};

// Elimina un ítem del carrito
export const removeCarritoItem = async (idCarritoItem) => {
  if (!idCarritoItem) throw new Error('ID de CarritoItem no proporcionado');
  const resp = await fetch(`${API_URL}/${idCarritoItem}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${getToken()}` }
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(`Error al eliminar ítem: ${text}`);
  }
  return { success: true };
};

// Actualiza la cantidad de un ítem del carrito
export const updateCarritoItem = async ({ idCarritoItem, idCarrito, idProducto, cantidad }) => {
  if (!idCarritoItem || !idCarrito || !idProducto || cantidad == null) {
    throw new Error('Faltan datos para actualizar el ítem');
  }

  const resp = await fetch(`${API_URL}/${idCarritoItem}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    },
    body: JSON.stringify({
      idCarrito,
      idProducto,
      cantidad
    })
  });

  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al actualizar ítem: ${text}`);
  return JSON.parse(text);
};
