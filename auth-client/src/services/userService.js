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
  return true; // 204 No Content no devuelve un cuerpo de respuesta
};

// --- Nuevas funciones para los endpoints de administrador ---

/** GET obtener todos los usuarios paginados (Admin) */
export const getAllUsersPaginated = async (page = 0, size = 10, sort = 'idUser,asc') => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(`${API_URL}/paginated?page=${page}&size=${size}&sort=${sort}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al obtener usuarios paginados');
  }
  return resp.json(); // Esto devolverá el objeto Page de Spring Data
};

/** POST crear un nuevo usuario (Admin) */
export const createUser = async (userData) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(userData),
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al crear usuario');
  }
  return resp.json();
};

/** PUT actualizar usuario por ID (Admin) */
export const updateUserByAdmin = async (userId, userData) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(`${API_URL}/${userId}/admin`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(userData),
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al actualizar usuario por admin');
  }
  return resp.json();
};

/** DELETE eliminar un usuario por ID (Admin) */
export const deleteUser = async (userId) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  const resp = await fetch(`${API_URL}/${userId}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al eliminar usuario');
  }
  return true; // 204 No Content
};

/** GET obtener todos los usuarios (sin paginar, si aún lo necesitas) */
export const listUsers = async () => {
    const token = getToken();
    if (!token) throw new Error('No autenticado');
    const resp = await fetch(API_URL, {
        headers: { Authorization: `Bearer ${token}` },
    });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Error al obtener la lista de usuarios');
    }
    return resp.json();
};

// Alias para compatibilidad con UserCrud
export const getAllUsers = getAllUsersPaginated;

// Actualizar solo el rol de un usuario (Admin)
export const updateUserRole = async (userId, newRole) => {
  const token = getToken();
  if (!token) throw new Error('No autenticado');
  // Suponiendo que el backend espera un array de roles
  const resp = await fetch(`${API_URL}/${userId}/admin`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ roles: [newRole] }),
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || 'Error al actualizar rol');
  }
  return resp.json();
};