// src/pages/AdminPage.jsx
import React, { useState } from "react";
import PedidoCrud from "../components/PedidoCrud";
import ProductoCrud from "../components/ProductoCrud";
import AdminDashboard from "../components/AdminDashboard";
import ParametroCrud from "../components/ParametroCrud";
import "../style/adminPage.css";

// Elimina imports y estados no usados, y muestra el CRUD según el menú
export default function AdminPage() {
  const [menu, setMenu] = useState("dashboard");

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-white py-10 px-4">
      <div className="max-w-5xl mx-auto">
        <h1 className="text-4xl font-bold text-indigo-700 mb-8 flex items-center gap-3">
          <i className="fas fa-tools text-indigo-400"></i>
          Panel de Administración
        </h1>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-8 mb-12">
          <button
            className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
              menu === "dashboard" ? "ring-2 ring-indigo-400" : ""
            }`}
            onClick={() => setMenu("dashboard")}
          >
            <div className="flex items-center gap-3 mb-4">
              <i className="fas fa-chart-line text-indigo-500 text-2xl"></i>
              <h2 className="text-xl font-semibold text-indigo-800">Dashboard</h2>
            </div>
            <p className="text-gray-600">
              Estadísticas de ventas y productos más vendidos.
            </p>
          </button>
          <button
            className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
              menu === "productos" ? "ring-2 ring-indigo-400" : ""
            }`}
            onClick={() => setMenu("productos")}
          >
            <div className="flex items-center gap-3 mb-4">
              <i className="fas fa-boxes text-indigo-500 text-2xl"></i>
              <h2 className="text-xl font-semibold text-indigo-800">Productos</h2>
            </div>
            <p className="text-gray-600">
              Administra el catálogo de productos, agrega, edita o elimina artículos.
            </p>
          </button>
          <button
            className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
              menu === "pedidos" ? "ring-2 ring-indigo-400" : ""
            }`}
            onClick={() => setMenu("pedidos")}
          >
            <div className="flex items-center gap-3 mb-4">
              <i className="fas fa-clipboard-list text-indigo-500 text-2xl"></i>
              <h2 className="text-xl font-semibold text-indigo-800">Pedidos</h2>
            </div>
            <p className="text-gray-600">
              Revisa, gestiona y actualiza el estado de los pedidos de los clientes.
            </p>
          </button>
          <button
            className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
              menu === "usuarios" ? "ring-2 ring-indigo-400" : ""
            }`}
            onClick={() => setMenu("usuarios")}
          >
            <div className="flex items-center gap-3 mb-4">
              <i className="fas fa-users-cog text-indigo-500 text-2xl"></i>
              <h2 className="text-xl font-semibold text-indigo-800">Usuarios</h2>
            </div>
            <p className="text-gray-600">
              Gestiona los usuarios registrados, roles y permisos.
            </p>
          </button>
          <button
            className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
              menu === "parametros" ? "ring-2 ring-indigo-400" : ""
            }`}
            onClick={() => setMenu("parametros")}
          >
            <div className="flex items-center gap-3 mb-4">
              <i className="fas fa-sliders-h text-indigo-500 text-2xl"></i>
              <h2 className="text-xl font-semibold text-indigo-800">Parámetros</h2>
            </div>
            <p className="text-gray-600">
              Modifica parámetros globales como costo de envío y descuento de cupón.
            </p>
          </button>
        </div>
        {/* Renderizado condicional del contenido */}
        <div>
          {menu === "dashboard" && <AdminDashboard />}
          {menu === "pedidos" && <PedidoCrud />}
          {menu === "productos" && <ProductoCrud />}
          {menu === "parametros" && <ParametroCrud />}
          {/* Aquí puedes agregar UserCrud si lo tienes */}
        </div>
      </div>
    </div>
  );
}