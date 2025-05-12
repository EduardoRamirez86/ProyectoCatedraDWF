// src/services/direccionService.js
import { secureGetItem } from '../utils/secureStorage';

const BASE = 'http://localhost:8080/auth/direcciones';

const getAuthHeaders = () => {
  const token = secureGetItem('token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};

// Obtener direcciones por ID de usuario
export const getByUserDirecciones = async (idUser) => {
  try {
    const response = await fetch(`${BASE}/user/${idUser}`, {
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      }
    });
    if (!response.ok) throw new Error('Error al cargar direcciones');
    return await response.json();
  } catch (error) {
    console.error(error);
    throw error;
  }
};

// Guardar una nueva dirección para un usuario
export const saveDireccion = async (direccion, idUser) => {
  try {
    const response = await fetch(`${BASE}?idUser=${idUser}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      },
      body: JSON.stringify(direccion)
    });
    if (!response.ok) throw new Error('Error al guardar dirección');
    return await response.json();
  } catch (error) {
    console.error(error);
    throw error;
  }
};

// Actualizar una dirección existente
export const updateDireccion = async (direccion) => {
  try {
    const response = await fetch(BASE, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      },
      body: JSON.stringify(direccion)
    });
    if (!response.ok) throw new Error('Error al actualizar dirección');
    return await response.json();
  } catch (error) {
    console.error(error);
    throw error;
  }
};
