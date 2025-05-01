// src/pages/AdminPage.jsx
import React, { useState } from 'react';
import RopaCrud from '../components/RopaCrud';
import ProductoCrud from '../components/ProductoCrud'; // Import the CompraCrud component
import '../style/adminPage.css'; // Asegúrate de importar el CSS

export default function AdminPage() {
  const [selectedMenu, setSelectedMenu] = useState('ropa');

  return (
    <div className="admin-layout">
      <aside className="sidebar">
        <div className="sidebar__brand">Admin</div>
        <nav className="sidebar__nav">
          <button
            className={`sidebar__nav-item ${selectedMenu === 'ropa' ? 'active' : ''}`}
            onClick={() => setSelectedMenu('ropa')}
          >
            Ropa
          </button>
          <button
            className={`sidebar__nav-item ${selectedMenu === 'producto' ? 'active' : ''}`}
            onClick={() => setSelectedMenu('producto')}
          >
            Producto
          </button>
          {/* Add more menu items here if needed */}
        </nav>
      </aside>
      <main className="main-content">
        {selectedMenu === 'ropa' && <RopaCrud />}
        {selectedMenu === 'producto' && <ProductoCrud />}
        {/* Render other components based on selectedMenu */}
      </main>
    </div>
  );
}
