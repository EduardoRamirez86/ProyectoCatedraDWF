const API_URL = "http://localhost:8080/auth/carrito";
const getToken = () => localStorage.getItem('token');

// Obtiene o crea el carrito del usuario
export const getOrCreateCarrito = async (idUser) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  
  const userId = idUser || parseInt(localStorage.getItem('userId'), 10); // Fallback to localStorage
  if (!userId) throw new Error('ID de usuario no proporcionado');
  
  const resp = await fetch(`${API_URL}/${userId}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener/crear carrito: ${text}`);
  return JSON.parse(text);
};

// Obtiene los ítems de un carrito
export const getCarritoItems = async (idCarrito) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  
  if (!idCarrito) throw new Error('ID de carrito no proporcionado');
  const resp = await fetch(`${API_URL}/${idCarrito}/items`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener ítems del carrito: ${text}`);
  const items = JSON.parse(text);

  // Debugging log to verify the structure of the response
  console.log('Response from API (Carrito Items):', items);

  // Ensure each item has a valid producto object
  return items.map((item) => ({
    ...item,
    producto: {
      ...item.producto,
      nombre: item.producto?.nombre || 'Producto desconocido',
      imagen: item.producto?.imagen || 'placeholder.jpg',
      precio: item.producto?.precio || 0,
    },
  }));
};