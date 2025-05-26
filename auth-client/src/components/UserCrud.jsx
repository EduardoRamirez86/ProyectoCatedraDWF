import React, { useEffect, useState } from "react";
import { getAllUsers, updateUserRole, deleteUser } from "../services/userService";

export default function UserCrud() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [roleUpdate, setRoleUpdate] = useState({});
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [search, setSearch] = useState("");
  const size = 10;

  const fetchUsers = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllUsers(pageNum, size);
      // Soporta respuesta paginada estándar de Spring Data
      const content = Array.isArray(result.content) ? result.content : [];
      setUsers(content);
      setPage(typeof result.number === "number" ? result.number : 0);
      setTotalPages(typeof result.totalPages === "number" ? result.totalPages : 1);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(page);
    // eslint-disable-next-line
  }, [page]);

  const handleRoleChange = (userId, newRole) => {
    setRoleUpdate((prev) => ({ ...prev, [userId]: newRole }));
  };

  const handleUpdateRole = async (userId) => {
    const newRole = roleUpdate[userId];
    if (!newRole) return;
    try {
      await updateUserRole(userId, newRole);
      setRoleUpdate((prev) => ({ ...prev, [userId]: "" }));
      // Espera a que termine y luego refresca la página 0 para evitar inconsistencias
      fetchUsers(0);
      setPage(0);
    } catch (err) {
      alert("Error al actualizar el rol: " + err.message);
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm("¿Seguro que deseas eliminar este usuario?")) return;
    try {
      await deleteUser(userId);
      fetchUsers(page);
    } catch (err) {
      alert("Error al eliminar usuario: " + err.message);
    }
  };

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  // Filtrado por nombre de usuario
  const filteredUsers = users.filter(user =>
    (user.username || "").toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <p className="text-center">Cargando usuarios...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-users-cog text-indigo-400"></i>
        Gestión de Usuarios
      </h2>
      <div className="mb-4 flex justify-end">
        <div className="relative w-72">
          <input
            type="text"
            placeholder="🔍 Buscar por nombre..."
            className="border border-indigo-300 focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 px-4 py-2 pl-10 rounded-full shadow-sm w-full transition"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <span className="absolute left-3 top-2.5 text-indigo-400 pointer-events-none">
            <i className="fas fa-search"></i>
          </span>
        </div>
      </div>
      <div className="bg-white shadow-md rounded-xl p-6">
        {filteredUsers.length === 0 ? (
          <p>No hay usuarios registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm text-left">
              <thead>
                <tr className="bg-indigo-50">
                  <th className="py-2 px-3 font-semibold">ID</th>
                  <th className="py-2 px-3 font-semibold">Nombre</th>
                  <th className="py-2 px-3 font-semibold">Email</th>
                  <th className="py-2 px-3 font-semibold">Fecha Nacimiento</th>
                  <th className="py-2 px-3 font-semibold">Teléfono</th>
                  <th className="py-2 px-3 font-semibold">Rol</th>
                  <th className="py-2 px-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((user) => (
                  <tr key={user.id} className="border-b">
                    <td className="py-2 px-3">{user.id}</td>
                    <td className="py-2 px-3">{user.username || user.nombre || '-'}</td>
                    <td className="py-2 px-3">{user.email}</td>
                    <td className="py-2 px-3">{user.fechaNacimiento || '-'}</td>
                    <td className="py-2 px-3">{user.telefono || '-'}</td>
                    <td className="py-2 px-3">
                      <span className="inline-block px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700">
                        {user.roleName || user.roles?.[0] || '-'}
                      </span>
                    </td>
                    <td className="py-2 px-3 flex gap-2">
                      <select
                        className="border rounded px-2 py-1 mr-2"
                        value={roleUpdate[user.id] || ""}
                        onChange={e => handleRoleChange(user.id, e.target.value)}
                      >
                        <option value="">Cambiar rol</option>
                        <option value="ROLE_USER">Usuario</option>
                        <option value="ROLE_EMPLOYEE">Empleado</option>
                        <option value="ROLE_ADMIN">Administrador</option>
                      </select>
                      <button
                        className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition"
                        onClick={() => handleUpdateRole(user.id)}
                        disabled={!roleUpdate[user.id]}
                      >
                        Actualizar
                      </button>
                      <button
                        className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
                        onClick={() => handleDelete(user.id)}
                      >
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <div className="flex justify-between items-center mt-6">
          <button
            onClick={handlePrev}
            disabled={page === 0}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Anterior
          </button>
          <span className="text-gray-700">
            Página {page + 1} de {totalPages}
          </span>
          <button
            onClick={handleNext}
            disabled={page >= totalPages - 1}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Siguiente
          </button>
        </div>
      </div>
    </div>
  );
}
