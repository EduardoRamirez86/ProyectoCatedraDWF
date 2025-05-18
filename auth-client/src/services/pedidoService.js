import { secureGetItem } from '../utils/secureStorage';

const API_URL = "http://localhost:8080/auth/pedido";

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

const getToken = () => secureGetItem("token");

/**
 * Admin: todos los pedidos, paginados
 */
export const getAllPedidos = async (page = 0, size = 10) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const params = new URLSearchParams({ page, size });
  const resp = await fetch(`${API_URL}/all?${params}`, {
    headers: { "Authorization": `Bearer ${token}` },
  });
  const data = await handleResponse(resp);
  console.log("RAW PAGE MODEL (all):", data);

  // Corrige: si hay pedidos en _embedded.pedidoResponseList, úsalos SIEMPRE
  let items = [];
  if (data._embedded && Array.isArray(data._embedded.pedidoResponseList)) {
    items = data._embedded.pedidoResponseList;
  } else if (Array.isArray(data.pedidoResponseList)) {
    // fallback por si el backend cambia la estructura
    items = data.pedidoResponseList;
  }

  // Corrige la paginación: usa SIEMPRE la info real del backend
  const pageInfo = data.page || {};

  // Si no hay info de paginación pero hay items, calcula totalElements
  return {
    items,
    page: pageInfo.number ?? 0,
    size: pageInfo.size ?? items.length,
    totalPages: pageInfo.totalPages ?? 1,
    totalElements: pageInfo.totalElements ?? items.length,
  };
};

/**
 * Usuario: sus pedidos, paginados
 */
export const getPedidosByUser = async (idUser, page = 0, size = 10) => {
  if (!idUser) throw new Error("ID de usuario no proporcionado");
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const params = new URLSearchParams({ page, size });
  const resp = await fetch(`${API_URL}/user/${idUser}?${params}`, {
    headers: { "Authorization": `Bearer ${token}` },
  });
  const data = await handleResponse(resp);
  console.log("RAW PAGE MODEL (user):", data);

  const items = Array.isArray(data._embedded?.pedidoResponseList)
    ? data._embedded.pedidoResponseList
    : [];
  const pageInfo = data.page || {};

  return {
    items,
    page: pageInfo.number ?? 0,
    size: pageInfo.size ?? items.length,
    totalPages: pageInfo.totalPages ?? 1,
    totalElements: pageInfo.totalElements ?? items.length,
  };
};

/**
 * Checkout (crear pedido)
 */
export const checkoutPedido = async (payload) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  const resp = await fetch(`${API_URL}/checkout`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
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
  const resp = await fetch(`${API_URL}/${idPedido}/${action}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  return handleResponse(resp);
};
