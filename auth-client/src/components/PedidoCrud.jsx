import React, { useEffect, useState } from "react";
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

export default function PedidoCrud() {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadPedidos = async () => {
    try {
      setLoading(true);
      const data = await getAllPedidos();
      setPedidos(data);
    } catch (e) {
      console.error(e);
      MySwal.fire("Error", "No se pudieron cargar los pedidos", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPedidos();
  }, []);

  const handleEstadoChange = async (idPedido, nuevoEstado) => {
    try {
      await updatePedidoEstado(idPedido, nuevoEstado);
      await loadPedidos();
      MySwal.fire("Estado actualizado", `Pedido #${idPedido} actualizado a ${nuevoEstado}`, "success");
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
            {pedidos.map(p => (
              <tr key={p.idPedido}>
                <td>{p.idPedido}</td>
                <td>{p.idCarrito}</td>
                <td>${p.total}</td>
                <td>{p.estado}</td>
                <td>
                  <select
                    value={p.estado}
                    onChange={e => handleEstadoChange(p.idPedido, e.target.value)}
                  >
                    <option value={p.estado}>{p.estado}</option>
                    {estadosDisponibles
                      .filter(e => e !== p.estado)
                      .map(e => (
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
      )}
    </div>
  );
}
