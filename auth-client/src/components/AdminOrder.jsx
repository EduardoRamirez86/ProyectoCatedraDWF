// src/components/AdminOrders.js
import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { getAllPedidos } from '../services/pedidoService';

export default function AdminOrders() {
  const { token } = useContext(AuthContext);
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!token) return;

    setLoading(true);
    setError(null);

    getAllPedidos(page, 10)
      .then(result => {
        setOrders(result.items || []);
        setPage(result.page ?? 0);
        setTotalPages(result.totalPages ?? 1);
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [token, page]);

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  if (loading) return <p className="text-center">Cargando pedidos...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Todos los Pedidos (Admin)</h2>
      {orders.length === 0 ? (
        <p>No hay pedidos registrados.</p>
      ) : (
        <div>
          <ul className="mb-4">
            {orders.map((order) => (
              <li key={order.idPedido} className="border-b pb-4 mb-4">
                <p><strong>ID Pedido:</strong> {order.idPedido}</p>
                <p><strong>Usuario:</strong> {order.nombreUsuario || 'Desconocido'}</p>
                <p><strong>Fecha:</strong> {order.fechaInicio ? new Date(order.fechaInicio).toLocaleString() : '-'}</p>
                <p><strong>Total:</strong> ${order.total}</p>
                <p><strong>Puntos:</strong> {order.puntosTotales}</p>
                <p><strong>Estado:</strong> {order.estado}</p>
                <p><strong>Tipo de Pago:</strong> {order.tipoPago}</p>
              </li>
            ))}
          </ul>
          <div className="flex justify-between items-center">
            <button
              onClick={handlePrev}
              disabled={page === 0}
              className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
            >
              Anterior
            </button>
            <span>
              Página {page + 1} de {totalPages}
            </span>
            <button
              onClick={handleNext}
              disabled={page >= totalPages - 1}
              className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
            >
              Siguiente
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
