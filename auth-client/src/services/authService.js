import { jwtDecode } from 'jwt-decode'; // Use named import for jwtDecode

const API_URL = 'http://localhost:8080/auth'; // Eliminamos la barra final para evitar duplicados

export async function register({
  username,
  email,
  password,
  primerNombre,
  segundoNombre,
  primerApellido,
  segundoApellido,
  fechaNacimiento,
  telefono,
  dui,
  direccion
}) {
  try {
    const res = await fetch(`${API_URL}/register`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
      },
      body: JSON.stringify({
        username,
        email,
        password,
        primerNombre,
        segundoNombre,
        primerApellido,
        segundoApellido,
        fechaNacimiento,
        
        telefono,
        dui,
        direccion
      })
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Error al registrar');
    }

    return await res.text(); // retorna el token
  } catch (error) {
    console.error('Error en register:', error.message);
    throw error;
  }
}

export async function login(username, password) {
  try {
    const res = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
      },
      body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
      let errorMsg = 'Error al iniciar sesión';
      try {
        const err = await res.json();
        // Si el backend retorna un mensaje específico, úsalo
        if (err && err.message) {
          errorMsg = err.message;
        } else if (res.status === 401) {
          errorMsg = 'Usuario o contraseña incorrectos';
        }
      } catch {
        if (res.status === 401) {
          errorMsg = 'Usuario o contraseña incorrectos';
        }
      }
      throw new Error(errorMsg);
    }

    const token = await res.text();
    const decoded = jwtDecode(token);

    if (!decoded || !decoded.userId) {
      throw new Error('El token no contiene un userId válido.');
    }

    localStorage.setItem('userId', decoded.userId); // Store userId in localStorage
    return token;
  } catch (error) {
    console.error('Error en login:', error.message);
    throw error;
  }
}
