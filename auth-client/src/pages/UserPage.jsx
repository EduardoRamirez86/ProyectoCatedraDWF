// src/pages/UserPage.jsx
import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Products from '../components/Products';
import '../style/userPage.css';

export default function UserPage() {
  const { token } = useContext(AuthContext);
  const [activeComponent, setActiveComponent] = useState('products');
  const [searchQuery, setSearchQuery] = useState('');

  if (!token) {
    return (
      <div className="unauthenticated-message">
        <h2>Usuario no autenticado</h2>
        <p>Por favor, inicia sesión para acceder a esta página.</p>
      </div>
    );
  }

  return (
    <div className="user-page">
      <header className="user-page-header">
        <nav className="user-page-nav">
          <div className="nav-buttons">
            <button
              className={activeComponent === 'products' ? 'active' : ''}
              onClick={() => setActiveComponent('products')}
            >
              Productos
            </button>
          </div>
          <input
            type="text"
            placeholder="Buscar productos..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-bar"
          />
        </nav>
      </header>

      <main className="user-page-content">
        {activeComponent === 'products' && <Products searchQuery={searchQuery} />}
      </main>
    </div>
  );
}
