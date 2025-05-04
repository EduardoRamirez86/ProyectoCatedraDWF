import React, { useState, useContext } from 'react';
import { motion } from 'framer-motion';
import { jwtDecode } from 'jwt-decode';
import { login as loginService } from '../services/authService';
import { getOrCreateCarrito } from '../services/carritoService';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import { useNavigate, Link } from 'react-router-dom';
import '../style/AuthForm.css';

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { login } = useContext(AuthContext);
  const { setCarrito } = useContext(CartContext);
  const navigate = useNavigate();

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const token = await loginService(form.username, form.password);
      login(token);
      const decoded = jwtDecode(token);
      
      // Almacenar usuario en localStorage
      localStorage.setItem('userId', decoded.userId);

      // Manejar carrito
      const cart = await getOrCreateCarrito(decoded.userId);
      setCarrito(cart);
      localStorage.setItem('carritoId', cart.idCarrito);

      // Navegación según rol
      navigate(decoded.roles.includes('ROLE_ADMIN') ? '/admin' : '/user');
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
      <h2 className="auth-title">Login</h2>
      {error && <p className="error-message">{error}</p>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <input
            name="username"
            placeholder=" "
            value={form.username}
            onChange={handleChange}
            required
          />
          <label>Usuario</label>
        </div>

        <div className="form-group">
          <input
            name="password"
            type="password"
            placeholder=" "
            value={form.password}
            onChange={handleChange}
            required
          />
          <label>Contraseña</label>
        </div>

        <button className="form-button" type="submit">Entrar</button>
      </form>

      <div className="auth-footer-text">
        ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
      </div>
    </motion.div>
  );
}