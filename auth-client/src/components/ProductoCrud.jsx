import React, { useEffect, useState, useContext } from "react";
import { getAllProductosPaged, createProducto, updateProducto, deleteProducto } from "../services/productoService";
import { AuthContext } from "../context/AuthContext";

export default function ProductoCrud() {
  const [productos, setProductos] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editProducto, setEditProducto] = useState(null);
  const [size] = useState(10);
  const [search, setSearch] = useState("");

  const { userData } = useContext(AuthContext);
  const roles = Array.isArray(userData?.roles) ? userData.roles : [userData?.roles];
  const isAdmin = roles.includes("ROLE_ADMIN");

  // --- Lógica de paginación igual a PedidoCrud ---
  const fetchProductos = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllProductosPaged(pageNum, size);
      // result: { items, page, size, totalPages, totalElements }
      setProductos(result.items || []);
      setPage(result.page ?? 0);
      setTotalPages(result.totalPages ?? 1);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProductos(page);
    // eslint-disable-next-line
  }, [page, size]);

  const handleEdit = (producto) => {
    if (!isAdmin) return;
    setEditProducto(producto);
    setModalOpen(true);
  };

  const handleAdd = () => {
    if (!isAdmin) return;
    setEditProducto(null);
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!isAdmin) return;
    if (!window.confirm("¿Seguro que deseas eliminar este producto?")) return;
    try {
      await deleteProducto(id);
      fetchProductos(page);
    } catch (err) {
      alert("Error al eliminar: " + err.message);
    }
  };

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  // Filtrado solo para la tabla actual
  const filtered = search
    ? productos.filter(p =>
        (p.nombre || "").toLowerCase().includes(search.toLowerCase())
      )
    : productos;

  if (loading) return <p className="text-center">Cargando productos...</p>;
  if (error)   return <p className="text-center text-red-500">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-boxes text-indigo-400"></i>
        Gestión de Productos
      </h2>
      {isAdmin && (
        <div className="mb-6 flex justify-between items-center">
          <span className="text-lg font-medium text-gray-700">
            Agrega nuevos productos al catálogo
          </span>
          <button
            className="flex items-center gap-2 px-5 py-2 bg-indigo-600 text-white rounded-full font-semibold shadow hover:bg-indigo-700 transition focus:outline-none focus:ring-2 focus:ring-indigo-400"
            onClick={handleAdd}
          >
            <i className="fas fa-plus"></i>
            Agregar Producto
          </button>
        </div>
      )}
      <div className="mb-4 flex justify-end">
        <input
          type="text"
          placeholder="🔍 Buscar producto..."
          className="border px-4 py-2 rounded-full w-64"
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>
      <div className="bg-white shadow-md rounded-xl p-6">
        {filtered.length === 0 ? (
          <p>No hay productos registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm text-left">
              <thead>
                <tr className="bg-indigo-50">
                  <th className="py-2 px-3 font-semibold">ID</th>
                  <th className="py-2 px-3 font-semibold">Nombre</th>
                  <th className="py-2 px-3 font-semibold">Descripción</th>
                  <th className="py-2 px-3 font-semibold">Precio</th>
                  <th className="py-2 px-3 font-semibold">Costo</th>
                  <th className="py-2 px-3 font-semibold">Stock</th>
                  <th className="py-2 px-3 font-semibold">Imagen</th>
                  <th className="py-2 px-3 font-semibold">Puntos</th>
                  <th className="py-2 px-3 font-semibold">Tipo</th>
                  <th className="py-2 px-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((producto) => (
                  <tr key={producto.idProducto} className="border-b">
                    <td className="py-2 px-3">{producto.idProducto}</td>
                    <td className="py-2 px-3">{producto.nombre}</td>
                    <td className="py-2 px-3">{producto.descripcion}</td>
                    <td className="py-2 px-3">${producto.precio}</td>
                    <td className="py-2 px-3">${producto.costo}</td>
                    <td className="py-2 px-3">{producto.cantidad}</td>
                    <td className="py-2 px-3">
                      {producto.imagen && (
                        <img src={producto.imagen} alt={producto.nombre} className="w-12 h-12 object-cover rounded" />
                      )}
                    </td>
                    <td className="py-2 px-3">{producto.cantidadPuntos}</td>
                    <td className="py-2 px-3">{producto.nombreTipo || producto.tipoProducto?.nombre || '-'}</td>
                    <td className="py-2 px-3 flex gap-2">
                      <button
                        className="px-3 py-1 bg-yellow-400 text-white rounded hover:bg-yellow-500 transition"
                        onClick={() => handleEdit(producto)}
                        disabled={!isAdmin}
                        title={isAdmin ? "Editar producto" : "Solo el administrador puede editar"}
                      >
                        Editar
                      </button>
                      <button
                        className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
                        onClick={() => handleDelete(producto.idProducto)}
                        disabled={!isAdmin}
                        title={isAdmin ? "Eliminar producto" : "Solo el administrador puede eliminar"}
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
      {/* Modal de creación/edición */}
      {modalOpen && isAdmin && (
        <ProductoModal
          producto={editProducto}
          onClose={() => setModalOpen(false)}
          onSave={async prod => {
            if (prod.idProducto) await updateProducto(prod.idProducto, prod);
            else              await createProducto(prod);
            setModalOpen(false);
            fetchProductos(page);
          }}
          tiposProducto={[]} // Si tienes tipos, pásalos aquí
        />
      )}
    </div>
  );
}

function ProductoModal({ producto, onClose, onSave, tiposProducto }) {
  const [form, setForm] = useState(
    producto || {
      nombre: "",
      descripcion: "",
      precio: "",
      costo: "",
      cantidad: "",
      imagen: "",
      cantidadPuntos: "",
      idTipoProducto: "",
    }
  );

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleTipoChange = (e) => {
    setForm({ ...form, idTipoProducto: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(form);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl shadow-lg p-8 w-full max-w-lg">
        <h3 className="text-xl font-bold mb-4">{form.idProducto ? "Editar Producto" : "Nuevo Producto"}</h3>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Nombre</label>
            <input
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Descripción</label>
            <textarea
              name="descripcion"
              value={form.descripcion}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
              rows={2}
              maxLength={500}
            />
          </div>
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="block text-sm font-medium mb-1">Precio</label>
              <input
                name="precio"
                type="number"
                step="0.01"
                value={form.precio}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                required
              />
            </div>
            <div className="flex-1">
              <label className="block text-sm font-medium mb-1">Costo</label>
              <input
                name="costo"
                type="number"
                step="0.01"
                value={form.costo}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                required
              />
            </div>
          </div>
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="block text-sm font-medium mb-1">Stock</label>
              <input
                name="cantidad"
                type="number"
                value={form.cantidad}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                required
              />
            </div>
            <div className="flex-1">
              <label className="block text-sm font-medium mb-1">Puntos</label>
              <input
                name="cantidadPuntos"
                type="number"
                value={form.cantidadPuntos}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Imagen (URL)</label>
            <input
              name="imagen"
              value={form.imagen}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Tipo de Producto</label>
            <select
              name="idTipoProducto"
              value={form.idTipoProducto}
              onChange={handleTipoChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
              required
            >
              <option value="">Selecciona un tipo</option>
              {tiposProducto && tiposProducto.map(tp => (
                <option key={tp.idTipoProducto} value={tp.idTipoProducto}>
                  {tp.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="flex justify-end gap-2 mt-4">
            <button
              type="button"
              className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 transition"
              onClick={onClose}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
            >
              Guardar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}