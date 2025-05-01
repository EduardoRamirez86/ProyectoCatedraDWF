// src/utils/swal.js
import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';

// Crea una instancia que acepta contenido React
const MySwal = withReactContent(Swal);

export default MySwal;
