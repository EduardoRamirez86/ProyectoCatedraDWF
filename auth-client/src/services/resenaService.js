import { secureGetItem } from '../utils/secureStorage';

const API_URL = "http://localhost:8080/auth/resenas";

// Mapea números a RatingEnum
const mapRatingToEnum = (rating) => {
  const ratingMap = {
    1: 'ONE',
    2: 'TWO',
    3: 'THREE',
    4: 'FOUR',
    5: 'FIVE',
  };
  return ratingMap[rating] || 'FIVE'; // Valor por defecto en caso de un rating no mapeado
};

// Crea una reseña para un producto
export const crearResena = async ({ idProducto, comentario, rating }) => {
  const token = secureGetItem('token');
  const idUser = secureGetItem('userId');

  if (!token) throw new Error('No se encontró el token de autenticación');
  if (!idUser) throw new Error('No se encontró el ID de usuario en secure-ls');
  if (!idProducto || !comentario || !rating) {
    throw new Error('idProducto, comentario y rating son obligatorios para crear una reseña');
  }

  const ratingEnum = mapRatingToEnum(parseInt(rating, 10));

  const headers = new Headers({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  });

  try {
    const resp = await fetch(API_URL, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ idProducto, comentario, rating: ratingEnum, idUser: parseInt(idUser, 10) }),
    });

    const text = await resp.text();
    if (!resp.ok) {
      let errorDetail = text;
      try {
        const errorJson = JSON.parse(text);
        errorDetail = errorJson.message || JSON.stringify(errorJson);
      } catch (e) { /* No es JSON, usa el texto crudo */ }
      throw new Error(`Error al crear reseña: ${resp.status} - ${errorDetail}`);
    }
    return JSON.parse(text);
  } catch (error) {
    console.error('Error en crearResena:', error);
    throw error;
  }
};

// Obtiene las reseñas de un producto (Endpoint no paginado - puedes mantenerlo o eliminarlo si no lo usas)
export const obtenerResenasPorProducto = async (idProducto) => {
  const token = secureGetItem('token');
  if (!token) throw new Error('No se encontró el token de autenticación');
  if (!idProducto) throw new Error('ID de producto no proporcionado');

  const headers = new Headers({
    'Authorization': `Bearer ${token}`,
  });

  try {
    const resp = await fetch(`${API_URL}/producto/${idProducto}`, {
      headers: headers,
    });

    const text = await resp.text();
    if (!resp.ok) {
      let errorDetail = text;
      try {
        const errorJson = JSON.parse(text);
        errorDetail = errorJson.message || JSON.stringify(errorJson);
      } catch (e) { /* No es JSON, usa el texto crudo */ }
      throw new Error(`Error al obtener reseñas: ${resp.status} - ${errorDetail}`);
    }
    return JSON.parse(text);
  } catch (error) {
    console.error('Error en obtenerResenasPorProducto:', error);
    throw error;
  }
};

// Función para obtener TODAS las reseñas de forma paginada (desde el endpoint general del backend)
export const getAllResenasPaginated = async (page = 0, size = 10) => {
  const token = secureGetItem('token');
  if (!token) throw new Error('No se encontró el token de autenticación');

  const headers = new Headers({
    'Authorization': `Bearer ${token}`, // **CORRECCIÓN: Backtick correctamente cerrado**
  });

  try {
    const resp = await fetch(`${API_URL}?page=${page}&size=${size}`, {
      headers: headers,
    });

    const text = await resp.text();
    if (!resp.ok) {
      let errorDetail = text;
      try {
        const errorJson = JSON.parse(text);
        errorDetail = errorJson.message || JSON.stringify(errorJson);
      } catch (e) { /* No es JSON, usa el texto crudo */ }
      throw new Error(`Error al obtener reseñas paginadas (todas): ${resp.status} - ${errorDetail}`);
    }
    return JSON.parse(text); // Esto devolverá el PagedModel de HATEOAS
  } catch (error) {
    console.error('Error en getAllResenasPaginated:', error);
    throw error;
  }
};

// --- NUEVA FUNCIÓN PARA OBTENER RESEÑAS POR PRODUCTO DE FORMA PAGINADA ---
// Esta es la función clave que usará tu ProductDetail.jsx
export const obtenerResenasPorProductoPaginadas = async (idProducto, page = 0, size = 5) => {
  const token = secureGetItem('token');
  if (!token) throw new Error('No se encontró el token de autenticación');
  if (!idProducto) throw new Error('ID de producto no proporcionado para la paginación de reseñas');

  const headers = new Headers({
    'Authorization': `Bearer ${token}`,
  });

  try {
    const resp = await fetch(`${API_URL}/producto/${idProducto}/paginated?page=${page}&size=${size}`, {
      headers: headers,
    });

    const text = await resp.text();
    if (!resp.ok) {
      let errorDetail = text;
      try {
        const errorJson = JSON.parse(text);
        errorDetail = errorJson.message || JSON.stringify(errorJson);
      } catch (e) { /* No es JSON, usa el texto crudo */ }
      throw new Error(`Error al obtener reseñas paginadas por producto: ${resp.status} - ${errorDetail}`);
    }
    return JSON.parse(text); // Esto devolverá el PagedModel de HATEOAS
  } catch (error) {
    console.error(`Error en obtenerResenasPorProductoPaginadas para producto ${idProducto}:`, error);
    throw error;
  }
};