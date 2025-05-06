import { secureGetItem } from '../utils/secureStorage';

/**
 * Función auxiliar para manejar respuestas
 */
const handleResponse = async (resp) => {
  const contentType = resp.headers.get("content-type");
  const isJson = contentType && contentType.includes("application/json");
  const data = isJson ? await resp.json() : await resp.text();

  if (!resp.ok) {
    const message = typeof data === "string" ? data : JSON.stringify(data);
    throw new Error(`Error: ${message}`);
  }

  return data;
};

const API_URL = "http://localhost:8080/auth/pedido";
const getToken = () => secureGetItem("token");

/**
 * Realiza el checkout del carrito
 */
export const checkoutPedido = async ({ idCarrito, tipoPago, cuponCodigo }) => {
  if (!idCarrito || !tipoPago) {
    throw new Error("idCarrito y tipoPago son obligatorios");
  }

  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const headers = new Headers({
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`,
  });

  const resp = await fetch(`${API_URL}/checkout`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({ idCarrito, tipoPago, cuponCodigo }),
  });

  return handleResponse(resp);
};

/**
 * Obtiene el historial de pedidos de un usuario
 */
export const getPedidosByUser = async (idUser) => {
  if (!idUser) throw new Error("ID de usuario no proporcionado");

  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const headers = new Headers({
    "Authorization": `Bearer ${token}`,
  });

  const resp = await fetch(`${API_URL}/user/${idUser}`, {
    headers: headers,
  });

  return handleResponse(resp);
};

/**
 * Obtiene todos los pedidos del sistema (ADMIN)
 */
export const getAllPedidos = async () => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const headers = new Headers({
    "Authorization": `Bearer ${token}`,
  });

  const resp = await fetch(`${API_URL}/all`, {
    headers: headers,
  });

  return handleResponse(resp);
};

/**
 * Cambia el estado de un pedido (ADMIN)
 */
export const updatePedidoEstado = async (idPedido, newEstado) => {
  const endpointMap = {
    PENDIENTE: "confirmar",
    PAGADO: "pagar",
    EN_PROCESO: "envio",
    ENTREGADO: "entregar",
    CANCELADO: "cancelar?motivo=admin"
  };

  const action = endpointMap[newEstado];
  if (!action) throw new Error("Estado inválido");

  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const headers = new Headers({
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`,
  });

  const resp = await fetch(`${API_URL}/${idPedido}/${action}`, {
    method: "POST",
    headers: headers,
  });

  return handleResponse(resp);
};