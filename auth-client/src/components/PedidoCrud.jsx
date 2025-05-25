import React, { useEffect, useState, useCallback } from "react";
import { getAllPedidos, updatePedidoEstado } from "../services/pedidoService";
import MySwal from "../utils/swal";
import "../style/Crud.css";

const estadosDisponibles = [
  "PENDIENTE",
  "PAGADO",
  "EN_PROCESO",
  "ENTREGADO",
  "CANCELADO",
];

export default function PedidoCrud({ admin = false }) {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const size = 10;

  const loadPedidos = useCallback(
  async (pg = 0) => {
    try {
      setLoading(true);
      const res = await getAllPedidos(pg, size);
      console.log("Respuesta getAllPedidos:", res);

      // Actualiza el estado con los valores devueltos
      setPedidos(res.items || []);
      setPage(res.page || 0);
      setTotalPages(res.totalPages || 1);
      console.log("Pedidos seteados:", res.items); // Para depurar
    } catch (e) {
      console.error(e);
      MySwal.fire("Error", "No se pudieron cargar los pedidos", "error");
    } finally {
      setLoading(false);
    }
  },
  [size]
);

  useEffect(() => {
    loadPedidos(0);
  }, [loadPedidos]);

  const handleEstadoChange = async (idPedido, nuevoEstado) => {
    try {
      await updatePedidoEstado(idPedido, nuevoEstado);
      await loadPedidos(page);
      MySwal.fire(
        "Estado actualizado",
        `Pedido #${idPedido} actualizado a ${nuevoEstado}`,
        "success"
      );
    } catch (e) {
      console.error(e);
      MySwal.fire("Error", "No se pudo actualizar el estado", "error");
    }
  };

  if (loading) return <p>Cargando pedidos…</p>;

  return (
    <div className="admin-container">
      <h1>Gestión de Pedidos</h1>
      {pedidos.length === 0 ? (
        <p>No hay pedidos</p>
      ) : (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Usuario</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map((p) => (
                <tr key={p.idPedido}>
                  <td>{p.idPedido}</td>
                  <td>{p.idCarrito}</td>
                  <td>${p.total.toFixed(2)}</td>
                  <td>{p.estado}</td>
                  <td>
                    <select
                      value={p.estado}
                      onChange={(e) =>
                        handleEstadoChange(p.idPedido, e.target.value)
                      }
                    >
                      {estadosDisponibles.map((e) => (
                        <option key={e} value={e}>
                          {e}
                        </option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="pagination">
            <button onClick={() => loadPedidos(page - 1)} disabled={page === 0}>
              « Anterior
            </button>
            <span>
              Página {page + 1} de {totalPages}
            </span>
            <button
              onClick={() => loadPedidos(page + 1)}
              disabled={page + 1 >= totalPages}
            >
              Siguiente »
            </button>
          </div>
        </>
      )}
    </div>
  );
}