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
  return ratingMap[rating] || 'FIVE'; // Valor por defecto
};

// Crea una reseña para un producto
export const crearResena = async ({ idProducto, comentario, rating }) => {
  const token = secureGetItem('token');
  const idUser = secureGetItem('userId');
  if (!token) throw new Error('No se encontró el token de autenticación');
  if (!idUser) throw new Error('No se encontró el ID de usuario en secure-ls');
  if (!idProducto || !comentario || !rating) {
    throw new Error('idProducto, comentario y rating son obligatorios');
  }

  const ratingEnum = mapRatingToEnum(parseInt(rating, 10));

  const headers = new Headers({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  });

  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: headers,
    body: JSON.stringify({ idProducto, comentario, rating: ratingEnum, idUser: parseInt(idUser, 10) }),
  });

  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al crear reseña: ${text}`);
  return JSON.parse(text);
};

// Obtiene las reseñas de un producto
export const obtenerResenasPorProducto = async (idProducto) => {
  const token = secureGetItem('token');
  if (!token) throw new Error('No se encontró el token de autenticación');
  if (!idProducto) throw new Error('ID de producto no proporcionado');

  const headers = new Headers({
    'Authorization': `Bearer ${token}`,
  });

  const resp = await fetch(`${API_URL}/producto/${idProducto}`, {
    headers: headers,
  });

  const text = await resp.text();
  if (!resp.ok) throw new Error(`Error al obtener reseñas: ${text}`);
  return JSON.parse(text);
};