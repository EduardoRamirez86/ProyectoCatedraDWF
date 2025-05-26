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

  // Para mostrar feedback de consulta de periodo
  const [periodoMsg, setPeriodoMsg] = useState(null);

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
    setPeriodoMsg(null);
    setError(null);
    if (!fechaInicio || !fechaFin) {
      setPeriodoMsg("Selecciona ambas fechas.");
      return;
    }
    if (fechaFin < fechaInicio) {
      setPeriodoMsg("La fecha final debe ser igual o posterior a la inicial.");
      return;
    }
    try {
      const res = await getGananciasPorPeriodo(fechaInicio, fechaFin);
      setGananciasPeriodo(res);
      setPeriodoMsg(null);
    } catch (err) {
      setPeriodoMsg("No se pudo consultar el periodo.");
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
        <div className="flex justify-center items-center h-32">
          <span className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mr-3"></span>
          <span className="text-gray-500">Cargando estadísticas...</span>
        </div>
      ) : error ? (
        <div className="text-center text-red-500">{error}</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          {/* Ganancias Totales */}
          <div className="bg-white rounded-xl shadow p-6 border border-indigo-100 flex flex-col items-center">
            <span className="text-gray-500 mb-2">Ganancias Totales</span>
            <span className="text-3xl font-bold text-indigo-700">
              ${Number(gananciasTotales ?? 0).toLocaleString("es-SV", { minimumFractionDigits: 2 })}
            </span>
            <span className="mt-2 text-xs text-gray-400">Acumulado histórico</span>
          </div>
          {/* Ganancias por periodo */}
          <div className="bg-white rounded-xl shadow p-6 border border-indigo-100 flex flex-col items-center">
            <form onSubmit={handlePeriodo} className="flex flex-col items-center gap-2 w-full">
              <span className="text-gray-500 mb-2">Ganancias por periodo</span>
              <div className="flex flex-col sm:flex-row gap-2 w-full">
                <input
                  type="date"
                  value={fechaInicio}
                  onChange={e => setFechaInicio(e.target.value)}
                  className="border rounded px-2 py-1 w-full min-w-[140px] max-w-[180px] text-center"
                  required
                  placeholder="Fecha inicio"
                  style={{ minWidth: 0 }}
                />
                <input
                  type="date"
                  value={fechaFin}
                  onChange={e => setFechaFin(e.target.value)}
                  className="border rounded px-2 py-1 w-full min-w-[140px] max-w-[180px] text-center"
                  required
                  placeholder="Fecha fin"
                  style={{ minWidth: 0 }}
                />
              </div>
              <button
                type="submit"
                className="mt-2 px-4 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700 transition"
              >
                Consultar
              </button>
            </form>
            {periodoMsg && (
              <span className="mt-2 text-sm text-red-500">{periodoMsg}</span>
            )}
            {gananciasPeriodo !== null && !periodoMsg && (
              <span className="mt-3 text-xl font-bold text-indigo-700">
                ${Number(gananciasPeriodo).toLocaleString("es-SV", { minimumFractionDigits: 2 })}
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
                  <li key={i} className="flex justify-between items-center">
                    <span className="font-medium text-indigo-800 truncate max-w-[120px]">{p.nombre}</span>
                    <span className="text-gray-700 font-semibold">{p.cantidad} vendidos</span>
                  </li>
                ))
              )}
            </ul>
            <div className="mt-4">
              <i className="fas fa-trophy text-yellow-400 mr-2"></i>
              <span className="text-xs text-gray-500">Basado en ventas históricas</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}