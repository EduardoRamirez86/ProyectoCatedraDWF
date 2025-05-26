import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import { getUserProfile, updateProfile, changePassword } from '../services/userService';
import { Link, Outlet, useLocation } from 'react-router-dom';

export default function Profile() {
  const { token, userData } = useContext(AuthContext);
  const userId = userData?.userId;
  const location = useLocation();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Estados de edición
  const [editMode, setEditMode] = useState(false);
  const [formValues, setFormValues] = useState({
    newUsername: '',
    newEmail: '',
    currentPassword: '',
  });
  const [profileMsg, setProfileMsg] = useState(null);

  // Estados de contraseña
  const [pwdValues, setPwdValues] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [pwdMsg, setPwdMsg] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      if (!token || !userId) {
        setError('Usuario no autenticado');
        setLoading(false);
        return;
      }
      try {
        const data = await getUserProfile(userId);
        setProfile(data);
        setFormValues({
          newUsername: data.username,
          newEmail: data.email,
          currentPassword: '',
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [token, userId]);

  const handleProfileChange = (e) => {
    setFormValues({ ...formValues, [e.target.name]: e.target.value });
  };

  const handleUpdateProfile = async () => {
    setProfileMsg(null);
    // Validación de username
    if (
      !formValues.newUsername ||
      formValues.newUsername.length < 5 ||
      formValues.newUsername.length > 20
    ) {
      setProfileMsg('El nombre de usuario debe tener entre 5 y 20 caracteres.');
      return;
    }
    // Validación de email básica (opcional, ya que el backend valida)
    if (!formValues.newEmail || !/\S+@\S+\.\S+/.test(formValues.newEmail)) {
      setProfileMsg('Por favor ingresa un correo electrónico válido.');
      return;
    }
    if (!formValues.currentPassword) {
      setProfileMsg('Por favor ingresa tu contraseña actual.');
      return;
    }
    try {
      const updated = await updateProfile({
        userId,
        currentPassword: formValues.currentPassword,
        newUsername: formValues.newUsername,
        newEmail: formValues.newEmail,
      });
      setProfile(updated);
      setProfileMsg('Perfil actualizado correctamente.');
      setEditMode(false);
    } catch (err) {
      // Manejo de mensaje de contraseña incorrecta
      let msg = err.message;
      try {
        // Si el error es un string con JSON, intenta parsear
        const match = msg && msg.match(/"description":"([^"]+)"/);
        if (match && match[1] && match[1].toLowerCase().includes('contraseña actual incorrecta')) {
          msg = 'Contraseña incorrecta';
        }
        // Si el error es un objeto con errors
        if (err.errors && Array.isArray(err.errors)) {
          const found = err.errors.find(e => e.description && e.description.toLowerCase().includes('contraseña actual incorrecta'));
          if (found) msg = 'Contraseña incorrecta';
        }
      } catch {}
      setProfileMsg(`Error: ${msg}`);
    }
  };

  const handlePwdChange = (e) => {
    setPwdValues({ ...pwdValues, [e.target.name]: e.target.value });
  };

  const handleChangePassword = async () => {
    setPwdMsg(null);
    // Validación de longitud de nueva contraseña
    if (pwdValues.newPassword.length < 8) {
      setPwdMsg('La nueva contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (pwdValues.newPassword !== pwdValues.confirmPassword) {
      setPwdMsg('La nueva contraseña y su confirmación no coinciden.');
      return;
    }
    if (!pwdValues.currentPassword) {
      setPwdMsg('Por favor ingresa tu contraseña actual.');
      return;
    }
    try {
      await changePassword({
        userId,
        currentPassword: pwdValues.currentPassword,
        newPassword: pwdValues.newPassword,
      });
      setPwdMsg('Contraseña cambiada con éxito.');
      setPwdValues({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err) {
      // Manejo de mensaje de contraseña incorrecta
      let msg = err.message;
      try {
        const match = msg && msg.match(/"description":"([^"]+)"/);
        if (match && match[1] && match[1].toLowerCase().includes('contraseña actual incorrecta')) {
          msg = 'Contraseña incorrecta';
        }
        if (err.errors && Array.isArray(err.errors)) {
          const found = err.errors.find(e => e.description && e.description.toLowerCase().includes('contraseña actual incorrecta'));
          if (found) msg = 'Contraseña incorrecta';
        }
      } catch {}
      setPwdMsg(`Error: ${msg}`);
    }
  };

  if (loading) return <p className="text-center">Cargando...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  // Solo muestra la info personal y cambio de contraseña si la ruta es exactamente /profile
  const isProfileRoot = location.pathname === '/profile';

  return (
    <div className="container mx-auto px-4 py-6 flex">
      {/* Navegación lateral */}
      <nav className="w-1/4 bg-gray-100 p-4 rounded-lg">
        <h2 className="text-xl font-semibold mb-4">Mi Cuenta</h2>
        <ul className="space-y-2">
          <li>
            <Link to="/profile" className="block p-2 hover:bg-gray-200 rounded">
              Perfil
            </Link>
          </li>
          <li>
            <Link to="/profile/user-orders" className="block p-2 hover:bg-gray-200 rounded">
              Mis Pedidos
            </Link>
          </li>
          <li>
            <Link to="/profile/puntos" className="block p-2 hover:bg-gray-200 rounded">
              Historial de Puntos
            </Link>
          </li>
          <li>
            <Link to="/profile/pedidos" className="block p-2 hover:bg-gray-200 rounded">
              Historial de Pedidos
            </Link>
          </li>
        </ul>
      </nav>

      {/* Contenido principal */}
      <main className="w-3/4 pl-6">
        {/* Renderiza el contenido de la subruta */}
        <Outlet />
        {/* Solo muestra el perfil si está en /profile */}
        {isProfileRoot && (
          <>
            <h1 className="text-3xl font-bold mb-8 text-indigo-700">Mi Perfil</h1>
            {/* Tarjeta de perfil */}
            <div className="bg-gradient-to-br from-indigo-50 to-white shadow-lg rounded-xl p-8 mb-8 border border-indigo-100">
              <h2 className="text-2xl font-semibold mb-6 text-indigo-800 flex items-center gap-2">
                <i className="fas fa-user-circle text-indigo-400 text-3xl"></i>
                Información Personal
              </h2>
              {editMode ? (
                <div className="space-y-5">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                    <input
                      name="newUsername"
                      value={formValues.newUsername}
                      onChange={handleProfileChange}
                      className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                    <input
                      name="newEmail"
                      type="email"
                      value={formValues.newEmail}
                      onChange={handleProfileChange}
                      className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña actual</label>
                    <input
                      name="currentPassword"
                      type="password"
                      value={formValues.currentPassword}
                      onChange={handleProfileChange}
                      className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                    />
                  </div>
                  {profileMsg && (
                    <p
                      className={`mt-2 text-sm ${
                        profileMsg.startsWith('Error') ? 'text-red-600' : 'text-green-600'
                      }`}
                    >
                      {profileMsg}
                    </p>
                  )}
                  <div className="flex space-x-4 mt-4">
                    <button
                      onClick={handleUpdateProfile}
                      className="px-5 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
                    >
                      Guardar
                    </button>
                    <button
                      onClick={() => {
                        setEditMode(false);
                        setProfileMsg(null);
                      }}
                      className="px-5 py-2 border border-indigo-300 rounded-lg font-semibold hover:bg-indigo-50 transition"
                    >
                      Cancelar
                    </button>
                  </div>
                </div>
              ) : (
                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <i className="fas fa-user text-indigo-400"></i>
                    <span className="text-gray-700"><strong>Username:</strong> {profile.username}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <i className="fas fa-envelope text-indigo-400"></i>
                    <span className="text-gray-700"><strong>Email:</strong> {profile.email}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <i className="fas fa-birthday-cake text-indigo-400"></i>
                    <span className="text-gray-700"><strong>Fecha de Nacimiento:</strong> {new Date(profile.fechaNacimiento).toLocaleDateString()}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <i className="fas fa-phone text-indigo-400"></i>
                    <span className="text-gray-700"><strong>Teléfono:</strong> {profile.telefono || '-'}</span>
                  </div>
                  <button
                    onClick={() => setEditMode(true)}
                    className="mt-6 px-6 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
                  >
                    Editar Perfil
                  </button>
                </div>
              )}
            </div>

            {/* Tarjeta de cambio de contraseña */}
            <div className="bg-gradient-to-br from-indigo-50 to-white shadow-lg rounded-xl p-8 border border-indigo-100">
              <h2 className="text-2xl font-semibold mb-6 text-indigo-800 flex items-center gap-2">
                <i className="fas fa-key text-indigo-400 text-2xl"></i>
                Cambiar Contraseña
              </h2>
              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña actual</label>
                  <input
                    name="currentPassword"
                    type="password"
                    value={pwdValues.currentPassword}
                    onChange={handlePwdChange}
                    className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nueva contraseña</label>
                  <input
                    name="newPassword"
                    type="password"
                    value={pwdValues.newPassword}
                    onChange={handlePwdChange}
                    className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Confirmar nueva contraseña
                  </label>
                  <input
                    name="confirmPassword"
                    type="password"
                    value={pwdValues.confirmPassword}
                    onChange={handlePwdChange}
                    className="w-full px-3 py-2 border border-indigo-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 transition"
                  />
                </div>
                {pwdMsg && (
                  <p
                    className={`mt-2 text-sm ${
                      pwdMsg.startsWith('Error') ? 'text-red-600' : 'text-green-600'
                    }`}
                  >
                    {pwdMsg}
                  </p>
                )}
                <button
                  onClick={handleChangePassword}
                  className="px-5 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
                >
                  Cambiar Contraseña
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}