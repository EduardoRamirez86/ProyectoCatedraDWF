// src/pages/Login.jsx
import React, { useState, useContext, useEffect } from 'react';
import { motion } from 'framer-motion';
import { login as loginService } from '../services/authService';
import { getOrCreateCarrito } from '../services/carritoService';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import { useNavigate, Link } from 'react-router-dom';
import '../style/AuthForm.css';

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { userData, login } = useContext(AuthContext);
  const { setCarrito } = useContext(CartContext);
  const navigate = useNavigate();

  // Efecto para manejar la redirección y carga del carrito
  useEffect(() => {
    const handlePostLogin = async () => {
      if (!userData) return;

      try {
        // 1. Obtener carrito
        const cart = await getOrCreateCarrito(userData.userId);
        
        // 2. Actualizar contexto del carrito
        setCarrito(cart);
        localStorage.setItem('carritoId', cart.idCarrito);
        
        // 3. Redirección final
        const targetRoute = userData.roles.includes('ROLE_ADMIN') 
          ? '/admin' 
          : '/user';
        
        console.log('Redirigiendo a:', targetRoute);
        navigate(targetRoute, { replace: true });
        
      } catch (error) {
        console.error('Error post-login:', error);
        setError('Error al cargar la sesión');
      }
    };

    handlePostLogin();
  }, [userData, navigate, setCarrito]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 1. Obtener token
      const token = await loginService(form.username, form.password);
      
      // 2. Actualizar contexto de autenticación
      login(token);
      
      console.log('Login exitoso, token recibido:', token);
      
    } catch (err) {
      console.error('Error en login:', err);
      setError(err.message || 'Error de autenticación');
      localStorage.removeItem('token');
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
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
