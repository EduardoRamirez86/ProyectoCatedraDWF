import React, { useEffect, useState } from "react";
import { getAllProductos, createProducto, updateProducto, deleteProducto } from "../services/productoService";

export default function ProductoCrud() {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editProducto, setEditProducto] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [size, setSize] = useState(10);

  const fetchProductos = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllProductos(pageNum, size);
      // Soporta respuesta paginada HAL con page info
      let list = [];
      let total = 1;
      let pageNumber = 0;
      if (Array.isArray(result)) {
        // Si el backend devuelve todos los productos como array, solo mostrar los de la página actual
        list = result.slice(pageNum * size, pageNum * size + size);
        total = Math.ceil(result.length / size);
        pageNumber = pageNum;
      } else if (result?._embedded?.productoResponseList) {
        list = result._embedded.productoResponseList;
        total = result.page?.totalPages || 1;
        pageNumber = result.page?.number ?? pageNum;
        setSize(result.page?.size || 10);
      }
      setProductos(list);
      setPage(pageNumber);
      setTotalPages(total);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProductos(page);
    // eslint-disable-next-line
  }, [page]);

  const handleEdit = (producto) => {
    setEditProducto(producto);
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este producto?")) return;
    try {
      await deleteProducto(id);
      fetchProductos(page);
    } catch (err) {
      alert("Error al eliminar: " + err.message);
    }
  };

  const handleModalClose = () => {
    setModalOpen(false);
    setEditProducto(null);
  };

  const handleModalSave = async (producto) => {
    try {
      if (producto.idProducto) {
        await updateProducto(producto.idProducto, producto);
      } else {
        await createProducto(producto);
      }
      handleModalClose();
      fetchProductos(page);
    } catch (err) {
      alert("Error al guardar: " + err.message);
    }
  };

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  if (loading) return <p className="text-center">Cargando productos...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-boxes text-indigo-400"></i>
        Gestión de Productos
      </h2>
      <div className="bg-white shadow-md rounded-xl p-6">
        <div className="flex justify-end mb-4">
          <button
            className="px-4 py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
            onClick={() => { setEditProducto(null); setModalOpen(true); }}
          >
            + Nuevo Producto
          </button>
        </div>
        {productos.length === 0 ? (
          <p>No hay productos registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm text-left">
              <thead>
                <tr className="bg-indigo-50">
                  <th className="py-2 px-3 font-semibold">ID</th>
                  <th className="py-2 px-3 font-semibold">Nombre</th>
                  <th className="py-2 px-3 font-semibold">Precio</th>
                  <th className="py-2 px-3 font-semibold">Stock</th>
                  <th className="py-2 px-3 font-semibold">Categoría</th>
                  <th className="py-2 px-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {productos.map((producto) => (
                  <tr key={producto.idProducto} className="border-b">
                    <td className="py-2 px-3">{producto.idProducto}</td>
                    <td className="py-2 px-3">{producto.nombre}</td>
                    <td className="py-2 px-3">${producto.precio}</td>
                    <td className="py-2 px-3">{producto.cantidad}</td>
                    <td className="py-2 px-3">{producto.nombreTipo || '-'}</td>
                    <td className="py-2 px-3 flex gap-2">
                      <button
                        className="px-3 py-1 bg-yellow-400 text-white rounded hover:bg-yellow-500 transition"
                        onClick={() => handleEdit(producto)}
                      >
                        Editar
                      </button>
                      <button
                        className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
                        onClick={() => handleDelete(producto.idProducto)}
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
      {/* Modal para crear/editar producto */}
      {modalOpen && (
        <ProductoModal
          producto={editProducto}
          onClose={handleModalClose}
          onSave={handleModalSave}
        />
      )}
    </div>
  );
}

// Modal de producto (puedes mejorar este modal según tus necesidades)
function ProductoModal({ producto, onClose, onSave }) {
  const [form, setForm] = useState(
    producto || { nombre: "", precio: "", cantidad: "", nombreTipo: "" }
  );

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(form);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl shadow-lg p-8 w-full max-w-md">
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
            <label className="block text-sm font-medium mb-1">Precio</label>
            <input
              name="precio"
              type="number"
              value={form.precio}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
              required
            />
          </div>
          <div>
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
          <div>
            <label className="block text-sm font-medium mb-1">Categoría</label>
            <input
              name="nombreTipo"
              value={form.nombreTipo}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
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