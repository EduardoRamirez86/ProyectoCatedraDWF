import React, { useState, useContext } from 'react';
import { motion } from 'framer-motion';
import { jwtDecode } from 'jwt-decode';
import { register as registerService } from '../services/authService';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import '../style/AuthForm.css';

export default function Register() {
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    primerNombre: '',
    segundoNombre: '',
    primerApellido: '',
    segundoApellido: '',
    fechaNacimiento: '',
    telefono: '',
    dui: '',
    direccion: ''
  });
  const [error, setError] = useState('');
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleChange = e =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const token = await registerService(form);
      login(token);
      const decoded = jwtDecode(token);
      const roles = decoded.roles || [];
      navigate(roles.includes('ROLE_ADMIN') ? '/admin' : '/user');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <motion.div
      className="auth-container"
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      <h2 className="auth-title">Registro</h2>
      {error && <p className="error-message">{error}</p>}

      <form onSubmit={handleSubmit}>
        {['username', 'email', 'password'].map(field => (
          <div className="form-group" key={field}>
            <input
              name={field}
              type={field === 'email' ? 'email' : field === 'password' ? 'password' : 'text'}
              placeholder=" "
              value={form[field]}
              onChange={handleChange}
              required
            />
            <label>
              {field === 'username'
                ? 'Usuario'
                : field.charAt(0).toUpperCase() + field.slice(1)}
            </label>
          </div>
        ))}

        <div className="form-group">
          <input name="primerNombre" placeholder=" " value={form.primerNombre} onChange={handleChange} required/>
          <label>Primer Nombre</label>
        </div>
        <div className="form-group">
          <input name="segundoNombre" placeholder=" " value={form.segundoNombre} onChange={handleChange}/>
          <label>Segundo Nombre</label>
        </div>
        <div className="form-group">
          <input name="primerApellido" placeholder=" " value={form.primerApellido} onChange={handleChange} required/>
          <label>Primer Apellido</label>
        </div>
        <div className="form-group">
          <input name="segundoApellido" placeholder=" " value={form.segundoApellido} onChange={handleChange}/>
          <label>Segundo Apellido</label>
        </div>

        <div className="form-group">
          <input name="fechaNacimiento" type="date" placeholder=" " value={form.fechaNacimiento} onChange={handleChange} required/>
          <label>Fecha de Nacimiento</label>
        </div>

        <div className="form-group">
          <input name="telefono" placeholder=" " value={form.telefono} onChange={handleChange}/>
          <label>Teléfono</label>
        </div>
        <div className="form-group">
          <input name="DUI" placeholder=" " value={form.DUI} onChange={handleChange}/>
          <label>DUI</label>
        </div>
        <div className="form-group">
          <input name="direccion" placeholder=" " value={form.direccion} onChange={handleChange}/>
          <label>Dirección</label>
        </div>

        <button className="form-button" type="submit">Registrar</button>
      </form>

      <div className="auth-footer-text">
        ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
      </div>
    </motion.div>
  );
}