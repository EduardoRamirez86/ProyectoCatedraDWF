// En Netlify, process.env.REACT_APP_API_BASE_URL es UNDEFINED si no lo configuras.
// Por lo tanto, usa el valor de fallback: http://localhost:8080/auth
const API_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/auth';