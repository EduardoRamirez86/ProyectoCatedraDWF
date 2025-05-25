// parametroService.js
import { secureGetItem } from '../utils/secureStorage';

// Change API_URL to match the base path of your controller
const API_URL = "http://localhost:8080/auth/parametros"; // <--- CHANGE THIS LINE

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
 * Obtener todos los parámetros
 */
export const getAllParametros = async () => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}/all`, { // Assuming /all is the endpoint for all params
    headers: { Authorization: `Bearer ${token}` },
  });

  return handleResponse(resp);
};

/**
 * Registrar un nuevo parámetro
 */
export const crearParametro = async (payload) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}`, {
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
 * Editar un parámetro por ID
 */
export const editarParametro = async (id, payload) => {
  const token = getToken();
  if (!token) throw new Error('No se encontró el token de autenticación');

  const resp = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  return handleResponse(resp);
};

/**
 * Obtener un parámetro por clave
 * Si no existe, retorna null
 */
export async function getParametroByClave(clave) {
  const token = getToken(); // Ensure token is used for this call too if it's secured
  if (!token) {
    throw new Error('No se encontró el token de autenticación');
  }
  try {
    // Correct URL construction: API_URL already has /parametros, and then /clave/{clave}
    const response = await fetch(`${API_URL}/clave/${clave}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) {
      throw new Error(`Error: ${await response.text()}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Error en getParametroByClave:", error);
    throw error;
  }
}