// src/pages/AdminPage.jsx
import React, { useState, useContext } from "react";
import PedidoCrud from "../components/PedidoCrud";
import ProductoCrud from "../components/ProductoCrud";
import AdminDashboard from "../components/AdminDashboard";
import ParametroCrud from "../components/ParametroCrud";
import UserCrud from "../components/UserCrud";
import { AuthContext } from "../context/AuthContext";

export default function AdminPage() {
  const [menu, setMenu] = useState("dashboard");
  const { userData } = useContext(AuthContext);            // <-- cambia aquí

  // Asegura que roles sea un array
  const roles = Array.isArray(userData?.roles)
    ? userData.roles
    : typeof userData?.roles === "string"
    ? [userData.roles]
    : [];

  const isAdmin = roles.includes("ROLE_ADMIN");
  const isEmployee = roles.includes("ROLE_EMPLOYEE");

  // Acceso restringido
  if (!userData || (!isAdmin && !isEmployee)) {
    return (
      <div className="min-h-screen flex items-center justify-center text-red-600 text-xl font-semibold">
        No tienes acceso a esta página.
      </div>
    );
  }

  const buttons = [
    {
      key: "dashboard",
      icon: "fas fa-chart-line",
      title: "Dashboard",
      description: "Estadísticas de ventas y productos más vendidos.",
      show: true,
    },
    {
      key: "productos",
      icon: "fas fa-boxes",
      title: "Productos",
      description: "Administra el catálogo de productos, agrega, edita o elimina artículos.",
      show: true,
    },
    {
      key: "pedidos",
      icon: "fas fa-clipboard-list",
      title: "Pedidos",
      description: "Revisa, gestiona y actualiza el estado de los pedidos de los clientes.",
      show: true,
    },
    {
      key: "usuarios",
      icon: "fas fa-users-cog",
      title: "Usuarios",
      description: "Gestiona los usuarios registrados, roles y permisos.",
      show: isAdmin,
    },
    {
      key: "parametros",
      icon: "fas fa-sliders-h",
      title: "Parámetros",
      description: "Modifica parámetros globales como costo de envío y descuento de cupón.",
      show: true,
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-white py-10 px-4">
      <div className="max-w-5xl mx-auto">
        <h1 className="text-4xl font-bold text-indigo-700 mb-8 flex items-center gap-3">
          <i className="fas fa-tools text-indigo-400"></i>
          Panel de Administración
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-8 mb-12">
          {buttons
            .filter((btn) => btn.show)
            .map(({ key, icon, title, description }) => (
              <button
                key={key}
                onClick={() => setMenu(key)}
                className={`bg-white rounded-xl shadow-lg p-6 border border-indigo-100 hover:shadow-xl transition w-full text-left ${
                  menu === key ? "ring-2 ring-indigo-400" : ""
                }`}
              >
                <div className="flex items-center gap-3 mb-4">
                  <i className={`${icon} text-indigo-500 text-2xl`}></i>
                  <h2 className="text-xl font-semibold text-indigo-800">{title}</h2>
                </div>
                <p className="text-gray-600">{description}</p>
              </button>
            ))}
        </div>

        {/* Renderizado del contenido según el menú */}
        <div>
          {menu === "dashboard" && <AdminDashboard />}
          {menu === "productos" && <ProductoCrud />}
          {menu === "pedidos" && <PedidoCrud />}
          {menu === "parametros" && <ParametroCrud />}
          {menu === "usuarios" && isAdmin && <UserCrud />}
        </div>
      </div>
    </div>
  );
}
