// src/services/direccionService.js
import { secureGetItem } from '../utils/secureStorage';
const BASE = 'http://localhost:8080/auth/direcciones';

export const getByUserDirecciones = async (idUser) => {
  const resp = await fetch(`${BASE}/user/${idUser}`);
  if (!resp.ok) throw new Error('Error al cargar direcciones');
  return resp.json();
};

export const saveDireccion = async (dir, idUser) => {
  const token = secureGetItem('token');
  const resp = await fetch(`${BASE}?idUser=${idUser}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(dir)
  });
  if (!resp.ok) throw new Error('Error al guardar dirección');
  return resp.json();
};



export const updateDireccion = async (dir) => {
  const token = secureGetItem('token');
  const resp = await fetch(`${BASE}`, {
    method: 'PUT',
    headers: { 'Content-Type':'application/json','Authorization':`Bearer ${token}` },
    body: JSON.stringify(dir)
  });
  if (!resp.ok) throw new Error('Error al actualizar dirección');
  return resp.json();
};