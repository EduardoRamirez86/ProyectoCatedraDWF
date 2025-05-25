import React, { useEffect, useState } from "react";
import {
  getGananciasTotales,
  getGananciasPorPeriodo,
  getProductosMasVendidos,
} from "../services/pedidoService";

export default function AdminDashboard() {
  const [gananciasTotales, setGananciasTotales] = useState(null);
  const [gananciasPeriodo, setGananciasPeriodo] = useState(null);
  const [productosMasVendidos, setProductosMasVendidos] = useState([]);
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const [totales, masVendidos] = await Promise.all([
          getGananciasTotales(),
          getProductosMasVendidos(5),
        ]);
        setGananciasTotales(totales);
        setProductosMasVendidos(
          Object.entries(masVendidos).map(([nombre, cantidad]) => ({ nombre, cantidad }))
        );
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  const handlePeriodo = async (e) => {
    e.preventDefault();
    setGananciasPeriodo(null);
    setError(null);
    if (!fechaInicio || !fechaFin) return;
    try {
      const res = await getGananciasPorPeriodo(fechaInicio, fechaFin);
      setGananciasPeriodo(res);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold text-indigo-700 mb-6 flex items-center gap-2">
        <i className="fas fa-chart-line text-indigo-400"></i>
        Dashboard de Ventas
      </h2>
      {loading ? (
        <p className="text-center">Cargando estadísticas...</p>
      ) : error ? (
        <p className="text-red-500 text-center">{error}</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          {/* Ganancias Totales */}
          <div className="bg-white rounded-xl shadow p-6 border border-indigo-100 flex flex-col items-center">
            <span className="text-gray-500 mb-2">Ganancias Totales</span>
            <span className="text-3xl font-bold text-indigo-700">
              ${gananciasTotales ?? 0}
            </span>
          </div>
          {/* Ganancias por periodo */}
          <div className="bg-white rounded-xl shadow p-6 border border-indigo-100 flex flex-col items-center">
            <form onSubmit={handlePeriodo} className="flex flex-col items-center gap-2 w-full">
              <span className="text-gray-500 mb-2">Ganancias por periodo</span>
              <div className="flex gap-2 w-full">
                <input
                  type="date"
                  value={fechaInicio}
                  onChange={e => setFechaInicio(e.target.value)}
                  className="border rounded px-2 py-1 w-full"
                  required
                />
                <input
                  type="date"
                  value={fechaFin}
                  onChange={e => setFechaFin(e.target.value)}
                  className="border rounded px-2 py-1 w-full"
                  required
                />
              </div>
              <button
                type="submit"
                className="mt-2 px-4 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition"
              >
                Consultar
              </button>
            </form>
            {gananciasPeriodo !== null && (
              <span className="mt-3 text-xl font-bold text-indigo-700">
                ${gananciasPeriodo}
              </span>
            )}
          </div>
          {/* Productos más vendidos */}
          <div className="bg-white rounded-xl shadow p-6 border border-indigo-100">
            <span className="text-gray-500 mb-2 block">Top 5 más vendidos</span>
            <ul className="mt-2 space-y-2">
              {productosMasVendidos.length === 0 ? (
                <li className="text-gray-400">Sin datos</li>
              ) : (
                productosMasVendidos.map((p, i) => (
                  <li key={i} className="flex justify-between">
                    <span className="font-medium text-indigo-800">{p.nombre}</span>
                    <span className="text-gray-700">{p.cantidad} vendidos</span>
                  </li>
                ))
              )}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
