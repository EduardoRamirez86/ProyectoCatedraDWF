import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import { getUserProfile, updateProfile, changePassword } from '../services/userService';

export default function Profile() {
  const { token, userData } = useContext(AuthContext);
  const userId = userData?.userId;

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
      setProfileMsg(`Error: ${err.message}`);
    }
  };

  const handlePwdChange = (e) => {
    setPwdValues({ ...pwdValues, [e.target.name]: e.target.value });
  };

  const handleChangePassword = async () => {
    setPwdMsg(null);
    if (pwdValues.newPassword !== pwdValues.confirmPassword) {
      setPwdMsg('La nueva contraseña y su confirmación no coinciden.');
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
      setPwdMsg(`Error: ${err.message}`);
    }
  };

  if (loading) return <p>Cargando...</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return (
    <main className="container mx-auto px-4 py-6">
      <h1 className="text-2xl font-semibold mb-4">Mi Perfil</h1>

      {/* Vista / Edición de perfil */}
      <div className="mb-8">
        {editMode ? (
          <div className="space-y-4">
            <div>
              <label className="block font-medium">Username</label>
              <input
                name="newUsername"
                value={formValues.newUsername}
                onChange={handleProfileChange}
                className="w-full border px-2 py-1 rounded"
              />
            </div>
            <div>
              <label className="block font-medium">Email</label>
              <input
                name="newEmail"
                type="email"
                value={formValues.newEmail}
                onChange={handleProfileChange}
                className="w-full border px-2 py-1 rounded"
              />
            </div>
            <div>
              <label className="block font-medium">Contraseña actual</label>
              <input
                name="currentPassword"
                type="password"
                value={formValues.currentPassword}
                onChange={handleProfileChange}
                className="w-full border px-2 py-1 rounded"
              />
            </div>
            {profileMsg && (
              <p className={profileMsg.startsWith('Error') ? 'text-red-500' : 'text-green-600'}>
                {profileMsg}
              </p>
            )}
            <div className="flex space-x-2">
              <button
                onClick={handleUpdateProfile}
                className="bg-indigo-600 text-white px-4 py-2 rounded"
              >
                Guardar
              </button>
              <button
                onClick={() => {
                  setEditMode(false);
                  setProfileMsg(null);
                }}
                className="px-4 py-2 rounded border"
              >
                Cancelar
              </button>
            </div>
          </div>
        ) : (
          <div className="space-y-2">
            <p>
              <strong>Username:</strong> {profile.username}
            </p>
            <p>
              <strong>Email:</strong> {profile.email}
            </p>
            <p>
              <strong>Fecha de Nacimiento:</strong>{' '}
              {new Date(profile.fechaNacimiento).toLocaleDateString()}
            </p>
            <p>
              <strong>Teléfono:</strong> {profile.telefono || '-'}
            </p>
            <button
              onClick={() => setEditMode(true)}
              className="mt-2 bg-indigo-600 text-white px-4 py-2 rounded"
            >
              Editar Perfil
            </button>
          </div>
        )}
      </div>

      {/* Cambio de contraseña */}
      <div className="space-y-4">
        <h2 className="text-xl font-semibold">Cambiar Contraseña</h2>
        <div>
          <label className="block font-medium">Contraseña actual</label>
          <input
            name="currentPassword"
            type="password"
            value={pwdValues.currentPassword}
            onChange={handlePwdChange}
            className="w-full border px-2 py-1 rounded"
          />
        </div>
        <div>
          <label className="block font-medium">Nueva contraseña</label>
          <input
            name="newPassword"
            type="password"
            value={pwdValues.newPassword}
            onChange={handlePwdChange}
            className="w-full border px-2 py-1 rounded"
          />
        </div>
        <div>
          <label className="block font-medium">Confirmar nueva contraseña</label>
          <input
            name="confirmPassword"
            type="password"
            value={pwdValues.confirmPassword}
            onChange={handlePwdChange}
            className="w-full border px-2 py-1 rounded"
          />
        </div>
        {pwdMsg && (
          <p className={pwdMsg.startsWith('Error') ? 'text-red-500' : 'text-green-600'}>
            {pwdMsg}
          </p>
        )}
        <button
          onClick={handleChangePassword}
          className="bg-indigo-600 text-white px-4 py-2 rounded"
        >
          Cambiar Contraseña
        </button>
      </div>
    </main>
  );
}