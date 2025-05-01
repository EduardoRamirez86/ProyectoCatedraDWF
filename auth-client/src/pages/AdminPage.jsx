// src/pages/AdminPage.jsx
import React, { useState } from 'react';
import RopaCrud from '../components/RopaCrud';
import CompraCrud from '../components/CompraCrud'; // Import the CompraCrud component
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
            className={`sidebar__nav-item ${selectedMenu === 'compra' ? 'active' : ''}`}
            onClick={() => setSelectedMenu('compra')}
          >
            Compra
          </button>
          {/* Add more menu items here if needed */}
        </nav>
      </aside>
      <main className="main-content">
        {selectedMenu === 'ropa' && <RopaCrud />}
        {selectedMenu === 'compra' && <CompraCrud />}
        {/* Render other components based on selectedMenu */}
      </main>
    </div>
  );
}
