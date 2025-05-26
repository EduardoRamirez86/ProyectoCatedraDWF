import React, { useEffect, useState } from "react";
import { getAllPedidos, updatePedidoEstado } from "../services/pedidoService";

export default function PedidoCrud() {
  const [pedidos, setPedidos] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [estadoUpdate, setEstadoUpdate] = useState({});
  const [size, setSize] = useState(10);

  const fetchPedidos = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllPedidos(pageNum, size);
      let list = [];
      let total = 1;
      let pageNumber = 0;
      if (Array.isArray(result)) {
        // Si el backend devuelve todos los pedidos como array, solo mostrar los de la página actual
        list = result.slice(pageNum * size, pageNum * size + size);
        total = Math.ceil(result.length / size);
        pageNumber = pageNum;
      } else if (result.items) {
        list = result.items;
        total = result.totalPages || 1;
        pageNumber = result.page ?? pageNum;
        setSize(result.size || 10);
      }
      setPedidos(list);
      setPage(pageNumber);
      setTotalPages(total);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPedidos(page);
    // eslint-disable-next-line
  }, [page]);

  const handleEstadoChange = (idPedido, newEstado) => {
    setEstadoUpdate((prev) => ({ ...prev, [idPedido]: newEstado }));
  };

  const handleUpdateEstado = async (idPedido) => {
    const newEstado = estadoUpdate[idPedido];
    if (!newEstado) return;
    try {
      await updatePedidoEstado(idPedido, newEstado);
      fetchPedidos(page);
    } catch (err) {
      alert("Error al actualizar el estado: " + err.message);
    }
  };

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  // Función utilitaria para mostrar la dirección de un pedido
  function renderDireccion(pedido) {
    const alias = pedido.aliasDireccion;
    const calle = pedido.calleDireccion;
    const ciudad = pedido.ciudadDireccion;
    const depto = pedido.departamentoDireccion;
    if (alias || calle || ciudad || depto) {
      return [alias, calle, ciudad, depto].filter(Boolean).join(", ");
    }
    // Fallback si no hay detalles, muestra el idDireccion
    return pedido.idDireccion ? `ID: ${pedido.idDireccion}` : "-";
  }

  if (loading) return <p className="text-center">Cargando pedidos...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-clipboard-list text-indigo-400"></i>
        Gestión de Pedidos
      </h2>
      <div className="bg-white shadow-md rounded-xl p-6">
        {pedidos.length === 0 ? (
          <p>No hay pedidos registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm text-left">
              <thead>
                <tr className="bg-indigo-50">
                  <th className="py-2 px-3 font-semibold">ID</th>
                  <th className="py-2 px-3 font-semibold">Dirección</th>
                  <th className="py-2 px-3 font-semibold">Fecha</th>
                  <th className="py-2 px-3 font-semibold">Total</th>
                  <th className="py-2 px-3 font-semibold">Estado</th>
                  <th className="py-2 px-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map((pedido) => (
                  <tr key={pedido.idPedido} className="border-b">
                    <td className="py-2 px-3">{pedido.idPedido}</td>
                    <td className="py-2 px-3">{renderDireccion(pedido)}</td>
                    <td className="py-2 px-3">
                      {pedido.fechaInicio
                        ? new Date(pedido.fechaInicio).toLocaleString()
                        : "-"}
                    </td>
                    <td className="py-2 px-3">${pedido.total}</td>
                    <td className="py-2 px-3">
                      <span
                        className={`inline-block px-3 py-1 rounded-full text-xs font-semibold
                        ${
                          pedido.estado === "ENTREGADO"
                            ? "bg-green-100 text-green-800"
                            : pedido.estado === "CANCELADO"
                            ? "bg-red-100 text-red-800"
                            : "bg-yellow-100 text-yellow-800"
                        }`}
                      >
                        {pedido.estado}
                      </span>
                    </td>
                    <td className="py-2 px-3 flex gap-2">
                      <select
                        className="border rounded px-2 py-1 mr-2"
                        value={estadoUpdate[pedido.idPedido] || ""}
                        onChange={(e) =>
                          handleEstadoChange(pedido.idPedido, e.target.value)
                        }
                      >
                        <option value="">Cambiar estado</option>
                        <option value="PENDIENTE">Pendiente</option>
                        <option value="PAGADO">Pagado</option>
                        <option value="EN_PROCESO">En Proceso</option>
                        <option value="ENTREGADO">Entregado</option>
                        <option value="CANCELADO">Cancelado</option>
                      </select>
                      <button
                        className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition"
                        onClick={() => handleUpdateEstado(pedido.idPedido)}
                        disabled={!estadoUpdate[pedido.idPedido]}
                      >
                        Actualizar
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