const API_URL = "http://localhost:8080/auth/pedido";
const getToken = () => localStorage.getItem('token');

// Realiza el checkout del carrito
export const checkoutPedido = async ({ idCarrito, idFormaPago }) => {
  if (!idCarrito || !idFormaPago) {
    throw new Error('idCarrito e idFormaPago son obligatorios');
  }
  const resp = await fetch(`${API_URL}/checkout`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`,
    },
    body: JSON.stringify({ idCarrito, idFormaPago }),
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error en checkout: ${text}`);
  return JSON.parse(text);
};

// Obtiene el historial de pedidos de un usuario
export const getPedidosByUser = async (idUser) => {
  if (!idUser) throw new Error('ID de usuario no proporcionado');
  const resp = await fetch(`${API_URL}/user/${idUser}`, {
    headers: { 'Authorization': `Bearer ${getToken()}` }
  });
  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener pedidos: ${text}`);
  return JSON.parse(text);
};
