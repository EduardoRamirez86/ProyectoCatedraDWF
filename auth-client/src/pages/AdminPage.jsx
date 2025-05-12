// src/pages/AdminPage.jsx
import React, { useState } from "react";
import RopaCrud from "../components/RopaCrud";
import ProductoCrud from "../components/ProductoCrud";
import PedidoCrud from "../components/PedidoCrud";  // <— lo añadimos
import "../style/adminPage.css";

export default function AdminPage() {
  const [menu, setMenu] = useState("ropa");
  return (
    <div className="admin-layout">
      <aside className="sidebar">
        <div className="sidebar__brand">Admin</div>
        <nav className="sidebar__nav">
          <button onClick={() => setMenu("ropa")} className={menu==="ropa"?"active":""}>Ropa</button>
          <button onClick={() => setMenu("producto")} className={menu==="producto"?"active":""}>Producto</button>
          <button onClick={() => setMenu("pedido")} className={menu==="pedido"?"active":""}>Pedidos</button>
        </nav>
      </aside>
      <main className="main-content">
        {menu === "ropa" && <RopaCrud />}
        {menu === "producto" && <ProductoCrud />}
        {menu === "pedido" && <PedidoCrud />}
      </main>
    </div>
  );
}
