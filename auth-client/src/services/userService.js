import { secureGetItem } from '../utils/secureStorage';

const API_URL = 'http://localhost:8080/auth/users';
const getToken = () => secureGetItem('token');

/** GET perfil del usuario */
export const getUserProfile = async (userId) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(`${API_URL}/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al obtener perfil');
  }
  return resp.json();
};

/** PUT actualizar perfil */
export const updateProfile = async ({ userId, currentPassword, newUsername, newEmail }) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(`${API_URL}/${userId}/profile`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ currentPassword, newUsername, newEmail }),
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al actualizar perfil');
  }
  return resp.json();
};

/** PUT cambiar contraseña */
export const changePassword = async ({ userId, currentPassword, newPassword }) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const params = new URLSearchParams({ currentPassword, newPassword });
  const resp = await fetch(`${API_URL}/${userId}/password?${params}`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al cambiar contraseña');
  }
  return true;
};