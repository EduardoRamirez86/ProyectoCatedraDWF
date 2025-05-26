import React, { useEffect, useState, useCallback } from "react";
import {
  getGananciasTotales,
  getGananciasPorPeriodo,
  getProductosMasVendidos,
} from "../services/pedidoService";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";
import dayjs from "dayjs";

export default function AdminDashboard() {
  const [gananciasTotales, setGananciasTotales] = useState(0);
  const [gananciasPeriodo, setGananciasPeriodo] = useState(0);
  const [productosMasVendidos, setProductosMasVendidos] = useState([]);

  const [fechaInicio, setFechaInicio] = useState(dayjs().startOf("month").format("YYYY-MM-DD"));
  const [fechaFin, setFechaFin] = useState(dayjs().format("YYYY-MM-DD"));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchTopProducts = useCallback(async () => {
    try {
      const raw = await getProductosMasVendidos(5);
      return Object.entries(raw).map(([nombre, cantidad]) => ({ nombre, cantidad }));
    } catch {
      return [];
    }
  }, []);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [totales, top5, periodo] = await Promise.all([
          getGananciasTotales(),
          fetchTopProducts(),
          getGananciasPorPeriodo(fechaInicio, fechaFin),
        ]);
        setGananciasTotales(totales);
        setProductosMasVendidos(top5);
        setGananciasPeriodo(periodo);
        setError(null);
      } catch (e) {
        setError(e.message || "Error al cargar datos");
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [fetchTopProducts, fechaInicio, fechaFin]);

  const handlePeriodo = async e => {
    e.preventDefault();
    if (fechaFin < fechaInicio) {
      setError("La fecha final debe ser igual o posterior a la inicial.");
      return;
    }
    setLoading(true);
    try {
      const periodo = await getGananciasPorPeriodo(fechaInicio, fechaFin);
      setGananciasPeriodo(periodo);
      setError(null);
    } catch (e) {
      setError(e.message || "Error al consultar periodo");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex-grow flex items-center justify-center">
        <div className="animate-spin h-12 w-12 border-4 border-t-transparent rounded-full"></div>
      </div>
    );
  }

  if (error) {
    return <div className="text-red-500 text-center mt-4">{error}</div>;
  }

  return (
    <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
      {/* Metric Cards */}
      <div className="space-y-6">
        <div className="bg-gradient-to-r from-indigo-600 to-indigo-400 text-white rounded-xl p-6 shadow-lg">
          <h3 className="text-lg font-semibold">Ganancias Totales</h3>
          <p className="mt-2 text-3xl font-bold">
            ${gananciasTotales.toLocaleString("es-SV", { minimumFractionDigits: 2 })}
          </p>
        </div>
        <div className="bg-gradient-to-r from-green-600 to-green-400 text-white rounded-xl p-6 shadow-lg">
          <h3 className="text-lg font-semibold">
            Ganancias ({dayjs(fechaInicio).format("D MMM")} - {dayjs(fechaFin).format("D MMM")})
          </h3>
          <p className="mt-2 text-3xl font-bold">
            ${gananciasPeriodo.toLocaleString("es-SV", { minimumFractionDigits: 2 })}
          </p>
          <form onSubmit={handlePeriodo} className="mt-4 flex space-x-2 items-center">
            <input
              type="date"
              className="rounded-lg px-3 py-1 text-gray-800"
              value={fechaInicio}
              onChange={e => setFechaInicio(e.target.value)}
            />
            <input
              type="date"
              className="rounded-lg px-3 py-1 text-gray-800"
              value={fechaFin}
              onChange={e => setFechaFin(e.target.value)}
            />
            <button
              type="submit"
              className="bg-white text-green-700 px-4 py-1 rounded-lg font-semibold shadow hover:bg-green-50 transition"
            >
              Actualizar
            </button>
          </form>
        </div>
      </div>

      {/* Top 5 Products Chart */}
      <div className="bg-white rounded-xl p-6 shadow-lg">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Top 5 Productos</h3>
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={productosMasVendidos} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="nombre" tick={{ fontSize: 12 }} />
            <YAxis />
            <Tooltip formatter={value => [value, "Vendidos"]} />
            <Bar dataKey="cantidad" fill="#6366F1" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
