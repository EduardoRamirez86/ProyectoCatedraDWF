// src/components/UserOrders.js
import React, { useEffect, useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const API_URL = 'http://localhost:8080/auth/pedido/user';

export default function UserOrders() {
  const { token, userData } = useContext(AuthContext);
  const userId = userData?.userId;
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchOrders = async (pageNum = 0) => {
    setLoading(true);
    setError(null);
    try {
      const resp = await fetch(`${API_URL}/${userId}?page=${pageNum}&size=10`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!resp.ok) throw new Error('Error al obtener pedidos');
      const data = await resp.json();
      const pedidos = data?._embedded?.pedidoResponseList || [];
      // Filtra todos los pedidos excepto ENTREGADO y CANCELADO
      const filtered = pedidos.filter(
        p => p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO'
      );
      setOrders(Array.isArray(filtered) ? filtered : []);
      setPage(data?.page?.number ?? 0);
      setTotalPages(data?.page?.totalPages ?? 1);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token && userId) fetchOrders(page);
    // eslint-disable-next-line
  }, [token, userId, page]);

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  if (loading) return <p className="text-center">Cargando pedidos...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-3xl font-bold mb-6">Mis Pedidos</h1>
      <div className="bg-white shadow-md rounded-lg p-6">
        {orders.length === 0 ? (
          <p>No tienes pedidos registrados.</p>
        ) : (
          <ul className="space-y-4">
            {orders.map((order) => (
              <li key={order.idPedido} className="border-b pb-4">
                <div className="flex flex-wrap justify-between items-center">
                  <div>
                    <p className="font-semibold text-lg">Pedido #{order.idPedido}</p>
                    <p className="text-gray-600">
                      <strong>Fecha:</strong> {order.fechaInicio ? new Date(order.fechaInicio).toLocaleString() : '-'}
                    </p>
                    <p className="text-gray-600">
                      <strong>Total:</strong> ${order.total}
                    </p>
                    <p className="text-gray-600">
                      <strong>Puntos:</strong> {order.puntosTotales}
                    </p>
                  </div>
                  <div className="text-right">
                    <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${order.estado === 'PENDIENTE' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'}`}>
                      {order.estado}
                    </span>
                    <p className="mt-2 text-gray-600">
                      <strong>Pago:</strong> {order.tipoPago}
                    </p>
                  </div>
                </div>
              </li>
            ))}
          </ul>
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