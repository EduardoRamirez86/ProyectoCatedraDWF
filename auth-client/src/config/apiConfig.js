// Valor de Ngrok activo como fallback. Incluye el /auth.
const NGROK_FALLBACK_URL = 'https://figurately-sinuous-isla.ngrok-free.dev/auth'; 
const LOCAL_FALLBACK_URL = 'http://localhost:8080/auth';

// 🔴 Usamos la variable de Netlify si está definida, si no, usamos el Ngrok para pruebas.
export const BASE_API_URL = process.env.REACT_APP_API_BASE_URL || NGROK_FALLBACK_URL;
// También podrías usar LOCAL_FALLBACK_URL si prefieres probar localmente sin Ngrok.