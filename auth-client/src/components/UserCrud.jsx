import React, { useEffect, useState } from "react";
import { getAllUsers, updateUserRole, deleteUser } from "../services/userService";

export default function UserCrud() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [roleUpdate, setRoleUpdate] = useState({});
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState("");

  const fetchUsers = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllUsers(pageNum, size);
      let list = [];
      let total = 1;
      let pageNumber = 0;
      let pageSize = size;
      // Soporta respuesta paginada estándar de Spring Data
      if (Array.isArray(result.content)) {
        list = result.content;
        total = typeof result.totalPages === "number" ? result.totalPages : 1;
        pageNumber = typeof result.number === "number" ? result.number : 0;
        pageSize = typeof result.size === "number" ? result.size : size;
      } else if (result.items) {
        list = result.items;
        total = result.totalPages || 1;
        pageNumber = result.page ?? pageNum;
        pageSize = result.size || size;
      } else if (Array.isArray(result)) {
        // fallback legacy
        list = result.slice(pageNum * size, pageNum * size + size);
        total = Math.ceil(result.length / size);
        pageNumber = pageNum;
        pageSize = size;
      }
      setUsers(list);
      setPage(pageNumber);
      setTotalPages(total);
      setSize(pageSize);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(page);
    // eslint-disable-next-line
  }, [page, size]);

  const handleRoleChange = (userId, newRole) => {
    setRoleUpdate((prev) => ({ ...prev, [userId]: newRole }));
  };

  const handleUpdateRole = async (userId) => {
    const newRole = roleUpdate[userId];
    if (!newRole) return;
    try {
      await updateUserRole(userId, newRole);
      setRoleUpdate((prev) => ({ ...prev, [userId]: "" }));
      fetchUsers(page);
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

  // Filtrado por nombre de usuario solo para la tabla actual
  const filteredUsers = search
    ? users.filter(user =>
        (user.username || "").toLowerCase().includes(search.toLowerCase())
      )
    : users;

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
        <div className="flex justify-center items-center gap-2 mt-8">
          <button
            onClick={handlePrev}
            disabled={page === 0}
            className={`px-4 py-2 rounded-l-lg border border-indigo-200 bg-white text-indigo-600 font-semibold transition
              ${page === 0 ? "opacity-50 cursor-not-allowed" : "hover:bg-indigo-50 hover:text-indigo-800"}`}
            aria-label="Anterior"
          >
            <i className="fas fa-chevron-left"></i> Anterior
          </button>
          <span className="px-4 py-2 bg-indigo-50 border-t border-b border-indigo-200 text-indigo-700 font-medium rounded-none select-none">
            Página <span className="font-bold">{page + 1}</span> de <span className="font-bold">{totalPages}</span>
          </span>
          <button
            onClick={handleNext}
            disabled={page >= totalPages - 1}
            className={`px-4 py-2 rounded-r-lg border border-indigo-200 bg-white text-indigo-600 font-semibold transition
              ${page >= totalPages - 1 ? "opacity-50 cursor-not-allowed" : "hover:bg-indigo-50 hover:text-indigo-800"}`}
            aria-label="Siguiente"
          >
            Siguiente <i className="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  );
}
