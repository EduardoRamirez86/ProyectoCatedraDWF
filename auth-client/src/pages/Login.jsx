import React, { useState, useContext, useEffect } from 'react';
import { motion } from 'framer-motion';
import { login as loginService } from '../services/authService';
import { getOrCreateCarrito } from '../services/carritoService';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import { useNavigate, Link } from 'react-router-dom';
import { secureSetItem, secureRemoveItem } from '../utils/secureStorage';

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { userData, login } = useContext(AuthContext);
  const { setCarrito } = useContext(CartContext);
  const navigate = useNavigate();

  // Redirigir y asignar carrito si hay sesión iniciada
  useEffect(() => {
    const handlePostLogin = async () => {
      if (!userData) return;

      try {
        const cart = await getOrCreateCarrito(userData.userId);
        setCarrito(cart);
        secureSetItem('carritoId', cart.idCarrito.toString());

        const targetRoute = userData.roles.includes('ROLE_ADMIN') ? '/admin' : '/user';
        navigate(targetRoute, { replace: true });
      } catch (error) {
        setError('Error al cargar el carrito');
      }
    };

    handlePostLogin();
  }, [userData, navigate, setCarrito]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const token = await loginService(form.username, form.password);
      login(token);
      // El token no se almacena aquí, ya que el segundo código no lo hace
    } catch (err) {
      setError(err.message || 'Usuario o contraseña incorrectos');
      secureRemoveItem('token');
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Helper para saber si el input tiene valor (para floating label)
  const hasValue = (name) => form[name] && form[name].length > 0;

  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="slide-up bg-white rounded-2xl shadow-xl overflow-hidden w-full max-w-md"
      >
        {/* Header */}
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-indigo-600 opacity-90"></div>
          <div className="relative z-10 p-8 text-center text-white">
            <h1 className="text-3xl font-bold mb-2" style={{ color: '#fff' }}>
              Bienvenido de vuelta
            </h1>
            <p style={{ color: '#fff' }}>Inicia sesión para acceder a tu cuenta</p>
          </div>
        </div>

        {/* Formulario */}
        <div className="p-8 pt-6">
          <div
            id="errorMessage"
            className={`mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm ${error ? 'block' : 'hidden'}`}
          >
            {error}
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Usuario */}
            <div className="relative">
              <input
                type="text"
                id="username"
                name="username"
                value={form.username}
                onChange={handleChange}
                className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder=" "
                required
              />
              <label
                htmlFor="username"
                className={
                  "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                  ((hasValue("username") || document.activeElement?.name === "username") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                }
              >
                <i className="fas fa-user mr-2"></i>Usuario
              </label>
            </div>

            {/* Contraseña */}
            <div className="relative">
              <input
                type="password"
                id="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder=" "
                required
              />
              <label
                htmlFor="password"
                className={
                  "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                  ((hasValue("password") || document.activeElement?.name === "password") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                }
              >
                <i className="fas fa-lock mr-2"></i>Contraseña
              </label>
            </div>

            {/* Botón submit */}
            <button
              type="submit"
              className="w-full py-3 px-4 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white font-medium rounded-lg shadow-md transition duration-300 flex items-center justify-center"
            >
              Iniciar sesión
            </button>
          </form>

          {/* Registro */}
          <div className="mt-6 text-center text-sm">
            <p className="text-gray-600">
              ¿No tienes una cuenta?{' '}
              <Link to="/register" className="text-blue-600 font-medium hover:underline">
                Regístrate
              </Link>
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  );
}