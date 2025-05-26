import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { getHistorialPuntosByUser } from '../services/historialPuntosService';

export default function PointsHistory() {
  const { userData, token } = useContext(AuthContext);
  const [pointsHistory, setPointsHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const size = 10;

  useEffect(() => {
    const fetchPointsHistory = async () => {
      if (!token || !userData?.userId) {
        setError('Usuario no autenticado');
        setLoading(false);
        return;
      }
      try {
        const data = await getHistorialPuntosByUser(userData.userId, page, size);
        const historial = data._embedded?.historialPuntosResponseList || [];
        setPointsHistory(historial);
        setTotalPages(data.page?.totalPages || 1);
        setError(null);
      } catch (err) {
        setError(err.message);
        setPointsHistory([]);
      } finally {
        setLoading(false);
      }
    };

    setLoading(true);
    fetchPointsHistory();
  }, [token, userData, page]);

  if (loading) return <p className="text-center">Cargando...</p>;
  if (error) return <p className="text-red-500 text-center">{error}</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="text-3xl font-bold mb-6">Historial de Puntos</h1>
      <div className="bg-white shadow-md rounded-lg p-6">
        {pointsHistory.length === 0 ? (
          <p>No hay historial de puntos.</p>
        ) : (
          <ul className="space-y-4">
            {pointsHistory.map((entry) => {
              const puntosCambiados = entry.cantidadNueva - entry.cantidadAnterior;
              return (
                <li key={entry.idHistorialPuntos} className="border-b pb-4">
                  <div className="flex flex-wrap justify-between items-center">
                    <div>
                      <p className="font-semibold text-lg">
                        {entry.fecha ? new Date(entry.fecha).toLocaleDateString() : '-'}
                      </p>
                      <p className="text-gray-600">
                        <strong>Cambio de Puntos:</strong> {puntosCambiados > 0 ? `+${puntosCambiados}` : puntosCambiados}
                      </p>
                      <p className="text-gray-600">
                        <strong>Puntos Anteriores:</strong> {entry.cantidadAnterior}
                      </p>
                      <p className="text-gray-600">
                        <strong>Puntos Nuevos:</strong> {entry.cantidadNueva}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-gray-600">
                        <strong>ID Pedido:</strong> {entry.idPedido || '-'}
                      </p>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
        <div className="flex justify-between items-center mt-6">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Anterior
          </button>
          <span className="text-gray-700">
            Página {page + 1} de {totalPages}
          </span>
          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Siguiente
          </button>
        </div>
      </div>
    </div>
  );
}
