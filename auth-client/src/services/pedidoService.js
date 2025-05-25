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
  const token = getToken(); // Suponiendo que tienes una función para obtener el token
  if (!token) throw new Error('No se encontró el token de autenticación');

  const params = new URLSearchParams({ page, size });
  try {
    const resp = await fetch(`${API_URL}/all?${params}`, {
      headers: { "Authorization": `Bearer ${token}` },
    });

    if (!resp.ok) {
      const message = await resp.text();
      throw new Error(`Error en la solicitud: ${message}`);
    }

    const data = await resp.json();
    console.log("Respuesta cruda del backend:", data);

    // Extraer la lista de pedidos de _embedded.pedidoResponseList
    const items = data._embedded?.pedidoResponseList || [];
    console.log("Pedidos extraídos:", items);

    // Extraer información de paginación
    const pageInfo = data.page || {};

    return {
      items, // Lista de pedidos
      page: pageInfo.number || 0, // Número de página actual
      size: pageInfo.size || size, // Tamaño de la página
      totalPages: pageInfo.totalPages || 1, // Total de páginas
      totalElements: pageInfo.totalElements || items.length, // Total de elementos
    };
  } catch (error) {
    console.error("Error al obtener los pedidos:", error);
    throw error;
  }
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

/**
 * Dashboard: ganancias totales
 */
export const getGananciasTotales = async () => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  const resp = await fetch(`${API_URL}/dashboard/ganancias/totales`, {
    headers: { "Authorization": `Bearer ${token}` },
  });
  if (!resp.ok) throw new Error(await resp.text());
  return await resp.json();
};

/**
 * Dashboard: ganancias por periodo
 */
export const getGananciasPorPeriodo = async (fechaInicio, fechaFin) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  const params = new URLSearchParams({ fechaInicio, fechaFin });
  const resp = await fetch(`${API_URL}/dashboard/ganancias/periodo?${params}`, {
    headers: { "Authorization": `Bearer ${token}` },
  });
  if (!resp.ok) throw new Error(await resp.text());
  return await resp.json();
};

/**
 * Dashboard: productos más vendidos
 */
export const getProductosMasVendidos = async (limit = 5) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');
  const resp = await fetch(`${API_URL}/dashboard/productos-mas-vendidos?limit=${limit}`, {
    headers: { "Authorization": `Bearer ${token}` },
  });
  if (!resp.ok) throw new Error(await resp.text());
  return await resp.json();
};
