import React, { useEffect, useState, useCallback } from "react";
import {
  getAllPedidos,
  updatePedidoEstado
} from "../services/pedidoService";
import MySwal from "../utils/swal";
import "../style/Crud.css";

const estadosDisponibles = [
  "PENDIENTE",
  "PAGADO",
  "EN_PROCESO",
  "ENTREGADO",
  "CANCELADO"
];

export default function PedidoCrud({ admin = false }) {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const size = 10;
  const [totalPages, setTotalPages] = useState(1);
  const [userError, setUserError] = useState(null);

  const loadPedidos = useCallback(
    async (pg = 0) => {
      try {
        setLoading(true);
        setUserError(null);
        // Usa getAllPedidos y toma los items del resultado
        const res = await getAllPedidos(pg, size);
        // DEBUG: muestra la estructura real de la respuesta
        console.log("Respuesta getAllPedidos:", res);

        // Si items está vacío pero hay _embedded, úsalo
        let items = [];
        let current = 0;
        let tp = 1;

        // Si tu backend retorna el modelo HAL, extrae manualmente
        if (Array.isArray(res.items) && res.items.length > 0) {
          items = res.items;
          current = res.page ?? 0;
          tp = res.totalPages ?? 1;
        } else if (res._embedded && Array.isArray(res._embedded.pedidoResponseList)) {
          items = res._embedded.pedidoResponseList;
          current = res.page?.number ?? 0;
          tp = res.page?.totalPages ?? 1;
        } else if (Array.isArray(res.content) && res.content.length > 0) {
          items = res.content;
          current = res.pageable?.pageNumber ?? 0;
          tp = res.totalPages ?? 1;
        } else if (Array.isArray(res._embedded?.pedidoResponseList)) {
          // fallback: si items está vacío pero _embedded sí existe
          items = res._embedded.pedidoResponseList;
          current = res.page?.number ?? 0;
          tp = res.page?.totalPages ?? 1;
        }

        // fallback final: si todo está vacío, intenta usar items aunque esté vacío
        if (!items.length && Array.isArray(res.items)) {
          items = res.items;
        }

        setPedidos(items);
        setPage(current);
        setTotalPages(tp);
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
  if (userError) return <p style={{ color: "red" }}>{userError}</p>;

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
