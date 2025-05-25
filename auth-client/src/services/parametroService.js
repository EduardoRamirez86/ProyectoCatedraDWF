// parametroService.js
import { secureGetItem } from '../utils/secureStorage';

// THIS IS THE CRITICAL CHANGE: "parametro" -> "parametros"
const API_URL = "http://localhost:8080/auth/parametros"; // Corrected to plural 'parametros'

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
  const token = getToken();
  if (!token) {
    throw new Error('No se encontró el token de autenticación');
  }
  try {
    // This URL construction is now correct because API_URL is corrected
    const response = await fetch(`${API_URL}/clave/${clave}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    // Check for response.ok before parsing JSON
    if (!response.ok) {
      // Attempt to read error message from backend if available, otherwise use status text
      const errorBody = await response.text(); // Read as text first
      throw new Error(`Error ${response.status}: ${errorBody || response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Error en getParametroByClave:", error);
    throw error;
  }
}