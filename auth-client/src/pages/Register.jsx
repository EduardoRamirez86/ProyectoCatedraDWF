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
    confirmPassword: '',
    primerNombre: '',
    segundoNombre: '',
    primerApellido: '',
    segundoApellido: '',
    fechaNacimiento: '',
    telefono: '',
    dui: '',
    direccion: ''
  });

  const [step, setStep] = useState(1);
  const [error, setError] = useState('');
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    let newValue = value;

    // Formateo automático para DUI: 12345678-9
    if (name === "dui") {
      // Elimina todo lo que no sea dígito
      const digits = value.replace(/\D/g, "");
      if (digits.length <= 8) {
        newValue = digits;
      } else {
        newValue = digits.slice(0, 8) + "-" + digits.slice(8, 9);
      }
    }

    // Formateo automático para teléfono: 1234-5678
    if (name === "telefono") {
      const digits = value.replace(/\D/g, "");
      if (digits.length <= 4) {
        newValue = digits;
      } else {
        newValue = digits.slice(0, 4) + "-" + digits.slice(4, 8);
      }
    }

    setForm({ ...form, [name]: newValue });
  };

  const validateStep = (currentStep) => {
    let isValid = true;
    if (currentStep === 1) {
      if (!form.username || !form.email || !form.password || !form.confirmPassword) {
        setError('Todos los campos son requeridos');
        return false;
      }
      if (form.password !== form.confirmPassword) {
        setError('Las contraseñas no coinciden');
        return false;
      }
      if (form.password.length < 8) {
        setError('La contraseña debe tener al menos 8 caracteres');
        return false;
      }
    }

    if (currentStep === 2) {
      if (!form.primerNombre || !form.primerApellido || !form.fechaNacimiento) {
        setError('Los campos marcados con * son obligatorios');
        return false;
      }
    }

    if (currentStep === 3) {
      // Validar formato teléfono: 4 dígitos - 4 dígitos
      if (form.telefono && !/^\d{4}-\d{4}$/.test(form.telefono)) {
        setError('El teléfono debe tener el formato 1234-5678');
        return false;
      }
      // Validar formato DUI: 8 dígitos - 1 dígito
      if (form.dui && !/^\d{8}-\d{1}$/.test(form.dui)) {
        setError('El DUI debe tener el formato 12345678-9');
        return false;
      }
      // Validar que el nombre de usuario tenga entre 5 y 20 caracteres
            if (form.username.length < 5) {
        setError('El nombre de usuario tiene que llevar entre 5 y 20 caracteres');
        return false;
      }
    }

    setError('');
    return isValid;
  };

  const nextStep = (currentStep) => {
    if (!validateStep(currentStep)) return;
    setStep((prev) => prev + 1);
  };

  const prevStep = () => {
    setStep((prev) => prev - 1);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateStep(1) || !validateStep(2) || !validateStep(3)) return;

    try {
      // Validar y convertir fecha
      let fechaFormateada = form.fechaNacimiento;
      if (fechaFormateada && fechaFormateada.includes('-')) {
        const [yyyy, mm, dd] = fechaFormateada.split('-');
        fechaFormateada = `${dd}/${mm}/${yyyy}`;
      }
      

      const { fechaNacimiento, ...rest } = form;
      const response = await registerService({ ...rest, fechaNacimiento: fechaFormateada });
      
      if (typeof response === "string") {
        login(response);
        const decoded = jwtDecode(response);
        const roles = decoded.roles || [];
        navigate(roles.includes('ROLE_ADMIN') ? '/admin' : '/user');
      } else {
        // Si el backend retorna un objeto, revisa si hay errores
        if (response.errors && Array.isArray(response.errors) && response.errors.length > 0) {
          setError(response.errors.map(e => e.description || e.title || e.message).join(' | '));
        } else if (response && typeof response === "string") {
          setError(response);
        } else {
          setError('Error en el registro');
        }
      }
    } catch (err) {
      // Mostrar mensaje exacto del backend si existe
      if (err && err.response && err.response.data) {
        // Si el backend retorna un array de errores
        if (Array.isArray(err.response.data.errors) && err.response.data.errors.length > 0) {
          setError(err.response.data.errors.map(e => e.description || e.title || e.message).join(' | '));
        } else if (typeof err.response.data === "string") {
          setError(err.response.data);
        } else if (err.response.data.message) {
          setError(err.response.data.message);
        } else {
          setError('Error en el registro');
        }
      } else if (err && err.message) {
        setError(err.message);
      } else {
        setError('Error en el registro');
      }
    }
  };

  // Helper para saber si el input tiene valor (para floating label)
  const hasValue = (name) => form[name] && form[name].length > 0;

  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="slide-up bg-white rounded-2xl shadow-xl overflow-hidden w-full max-w-2xl"
      >
        {/* Header */}
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-indigo-600 opacity-90"></div>
          <div className="relative z-10 p-8 text-center text-white">
            <h1 className="text-3xl font-bold mb-2">Crea tu cuenta</h1>
            <p>Completa el formulario para registrarte</p>
          </div>
        </div>

        <div className="p-8 pt-6">
          {error && (
            <div id="errorMessage" className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm error-shake">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Step 1: Información básica */}
            {step === 1 && (
              <div id="step1" className="form-section">
                <h3 className="text-lg font-semibold text-gray-700 mb-4 flex items-center">
                  <span className="w-6 h-6 bg-blue-500 text-white rounded-full flex items-center justify-center mr-2">1</span>
                  Información básica
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="relative">
                    <input
                      type="text"
                      name="username"
                      value={form.username}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
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
                  <div className="relative">
                    <input
                      type="email"
                      name="email"
                      value={form.email}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="email"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("email") || document.activeElement?.name === "email") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-envelope mr-2"></i>Email
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="password"
                      name="password"
                      value={form.password}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
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
                  <div className="relative">
                    <input
                      type="password"
                      name="confirmPassword"
                      value={form.confirmPassword}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="confirmPassword"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("confirmPassword") || document.activeElement?.name === "confirmPassword") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-lock mr-2"></i>Confirmar Contraseña
                    </label>
                  </div>
                </div>
                <div className="flex justify-end mt-4">
                  <button type="button" onClick={() => nextStep(1)} className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition">
                    Siguiente <i className="fas fa-arrow-right ml-2"></i>
                  </button>
                </div>
              </div>
            )}

            {/* Step 2: Información personal */}
            {step === 2 && (
              <div id="step2" className="form-section">
                <h3 className="text-lg font-semibold text-gray-700 mb-4 flex items-center">
                  <span className="w-6 h-6 bg-blue-500 text-white rounded-full flex items-center justify-center mr-2">2</span>
                  Información personal
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="relative">
                    <input
                      type="text"
                      name="primerNombre"
                      value={form.primerNombre}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="primerNombre"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("primerNombre") || document.activeElement?.name === "primerNombre") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-signature mr-2"></i>Primer Nombre
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="text"
                      name="segundoNombre"
                      value={form.segundoNombre}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    />
                    <label
                      htmlFor="segundoNombre"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("segundoNombre") || document.activeElement?.name === "segundoNombre") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-signature mr-2"></i>Segundo Nombre
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="text"
                      name="primerApellido"
                      value={form.primerApellido}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="primerApellido"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("primerApellido") || document.activeElement?.name === "primerApellido") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-signature mr-2"></i>Primer Apellido
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="text"
                      name="segundoApellido"
                      value={form.segundoApellido}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    />
                    <label
                      htmlFor="segundoApellido"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("segundoApellido") || document.activeElement?.name === "segundoApellido") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-signature mr-2"></i>Segundo Apellido
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="date"
                      name="fechaNacimiento"
                      value={form.fechaNacimiento}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="fechaNacimiento"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("fechaNacimiento") || document.activeElement?.name === "fechaNacimiento") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-calendar-alt mr-2"></i>
                    </label>
                  </div>
                </div>
                <div className="flex justify-between mt-4">
                  <button type="button" onClick={prevStep} className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition">
                    <i className="fas fa-arrow-left mr-2"></i> Anterior
                  </button>
                  <button type="button" onClick={() => nextStep(2)} className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition">
                    Siguiente <i className="fas fa-arrow-right ml-2"></i>
                  </button>
                </div>
              </div>
            )}

            {/* Step 3: Información de contacto */}
            {step === 3 && (
              <div id="step3" className="form-section">
                <h3 className="text-lg font-semibold text-gray-700 mb-4 flex items-center">
                  <span className="w-6 h-6 bg-blue-500 text-white rounded-full flex items-center justify-center mr-2">3</span>
                  Información de contacto
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="relative">
                    <input
                      type="tel"
                      name="telefono"
                      value={form.telefono}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    />
                    <label
                      htmlFor="telefono"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("telefono") || document.activeElement?.name === "telefono") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-phone mr-2"></i>Teléfono
                    </label>
                  </div>
                  <div className="relative">
                    <input
                      type="text"
                      name="dui"
                      value={form.dui}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                      required
                    />
                    <label
                      htmlFor="dui"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("dui") || document.activeElement?.name === "dui") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-id-card mr-2"></i>DUI
                    </label>
                  </div>
                  <div className="relative col-span-1 md:col-span-2">
                    <input
                      type="text"
                      name="direccion"
                      value={form.direccion}
                      onChange={handleChange}
                      placeholder=" "
                      className="floating-input peer w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    />
                    <label
                      htmlFor="direccion"
                      className={
                        "floating-label absolute left-4 top-3 text-gray-500 pointer-events-none transition-all duration-200 " +
                        ((hasValue("direccion") || document.activeElement?.name === "direccion") ? "transform -translate-y-6 scale-90 text-blue-500 bg-white px-1" : "")
                      }
                    >
                      <i className="fas fa-home mr-2"></i>Dirección
                    </label>
                  </div>
                </div>
                <div className="flex justify-between mt-4">
                  <button type="button" onClick={prevStep} className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition">
                    <i className="fas fa-arrow-left mr-2"></i> Anterior
                  </button>
                  <button type="submit" className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition">
                    <i className="fas fa-user-plus mr-2"></i> Registrar cuenta
                  </button>
                </div>
              </div>
            )}
          </form>

          <div className="mt-6 text-center text-sm">
            <p className="text-gray-600">
              ¿Ya tienes una cuenta?{' '}
              <Link to="/login" className="text-blue-600 font-medium hover:underline">
                Inicia sesión
              </Link>
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  );
}