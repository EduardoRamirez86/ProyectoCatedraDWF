const API_URL = "http://localhost:8080/auth/carrito";
const getToken = () => localStorage.getItem('token');

// Obtiene o crea el carrito del usuario
export const getOrCreateCarrito = async (idUser) => {
  const userId = idUser || parseInt(localStorage.getItem('userId'), 10); // Fallback to localStorage
  if (!userId) throw new Error('ID de usuario no proporcionado');
  const resp = await fetch(`${API_URL}/${userId}`, {
    headers: { 'Authorization': `Bearer ${getToken()}` }
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener/crear carrito: ${text}`);
  return JSON.parse(text);
};

// Obtiene los ítems de un carrito
export const getCarritoItems = async (idCarrito) => {
  if (!idCarrito) throw new Error('ID de carrito no proporcionado');
  const resp = await fetch(`${API_URL}/${idCarrito}/items`, {
    headers: { 'Authorization': `Bearer ${getToken()}` }
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener ítems del carrito: ${text}`);
  return JSON.parse(text);
};
