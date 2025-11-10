// La variable de Netlify se usa si existe; si no, usa el Ngrok activo como fallback.
// Reemplaza "https://figurately-sinuous-isla.ngrok-free.dev" con tu URL activa.
const NGROK_URL = 'https://figurately-sinuous-isla.ngrok-free.dev'; 

// Esta es la variable central que todos los servicios deben importar.
export const BASE_API_URL = process.env.REACT_APP_API_BASE_URL || `${NGROK_URL}/auth`;