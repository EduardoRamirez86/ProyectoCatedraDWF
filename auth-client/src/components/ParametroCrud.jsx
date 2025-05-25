import React, { useEffect, useState } from "react";
import {
  getAllParametros,
  editarParametro,
} from "../services/parametroService";

export default function ParametroCrud() {
  const [parametros, setParametros] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editId, setEditId] = useState(null);
  const [editValue, setEditValue] = useState("");
  const [msg, setMsg] = useState(null);

  const fetchParametros = async () => {
    setLoading(true);
    setMsg(null);
    try {
      const res = await getAllParametros();
      // Si el backend devuelve { items: [...] }
      const list = Array.isArray(res)
        ? res
        : Array.isArray(res.items)
        ? res.items
        : Array.isArray(res._embedded?.parametroResponseList)
        ? res._embedded.parametroResponseList
        : [];
      setParametros(list);
    } catch (err) {
      setMsg("Error al cargar parámetros: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchParametros();
  }, []);

  const handleEdit = (param) => {
    setEditId(param.idParametro);
    setEditValue(param.valor);
    setMsg(null);
  };

  const handleCancel = () => {
    setEditId(null);
    setEditValue("");
    setMsg(null);
  };

  const handleSave = async (param) => {
    setMsg(null);
    try {
      await editarParametro(param.idParametro, {
        ...param,
        valor: editValue,
      });
      setMsg("Parámetro actualizado correctamente.");
      setEditId(null);
      setEditValue("");
      fetchParametros();
    } catch (err) {
      setMsg("Error al actualizar: " + err.message);
    }
  };

  if (loading) return <p className="text-center">Cargando parámetros...</p>;

  return (
    <div className="container mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-sliders-h text-indigo-400"></i>
        Parámetros del Sistema
      </h2>
      <div className="bg-white shadow-md rounded-xl p-6">
        {msg && (
          <div className={`mb-4 text-center ${msg.startsWith("Error") ? "text-red-500" : "text-green-600"}`}>
            {msg}
          </div>
        )}
        {parametros.length === 0 ? (
          <p>No hay parámetros registrados.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm text-left">
              <thead>
                <tr className="bg-indigo-50">
                  <th className="py-2 px-3 font-semibold">Clave</th>
                  <th className="py-2 px-3 font-semibold">Valor</th>
                  <th className="py-2 px-3 font-semibold">Descripción</th>
                  <th className="py-2 px-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {parametros.map((param) => (
                  <tr key={param.idParametro} className="border-b">
                    <td className="py-2 px-3">{param.clave}</td>
                    <td className="py-2 px-3">
                      {editId === param.idParametro ? (
                        <input
                          type="text"
                          value={editValue}
                          onChange={e => setEditValue(e.target.value)}
                          className="border rounded px-2 py-1 w-32"
                        />
                      ) : (
                        param.valor
                      )}
                    </td>
                    <td className="py-2 px-3">{param.descripcion}</td>
                    <td className="py-2 px-3 flex gap-2">
                      {editId === param.idParametro ? (
                        <>
                          <button
                            className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700 transition"
                            onClick={() => handleSave(param)}
                          >
                            Guardar
                          </button>
                          <button
                            className="px-3 py-1 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition"
                            onClick={handleCancel}
                          >
                            Cancelar
                          </button>
                        </>
                      ) : (
                        <button
                          className="px-3 py-1 bg-yellow-400 text-white rounded hover:bg-yellow-500 transition"
                          onClick={() => handleEdit(param)}
                        >
                          Editar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
