// src/pages/UserPage.jsx
import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import Cart from '../components/Cart';
import Products from '../components/Products';
import Checkout from '../components/Checkout';
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
          <button
            className={activeComponent === 'products' ? 'active' : ''}
            onClick={() => setActiveComponent('products')}
          >
            Productos
          </button>
          <button
            className={activeComponent === 'cart' ? 'active' : ''}
            onClick={() => setActiveComponent('cart')}
          >
            Carrito
          </button>
          <button
            className={activeComponent === 'checkout' ? 'active' : ''}
            onClick={() => setActiveComponent('checkout')}
          >
            Checkout
          </button>
        </nav>
        <input
          type="text"
          placeholder="Buscar productos..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="search-bar"
        />
      </header>

      <main className="user-page-content">
        {activeComponent === 'products' && <Products searchQuery={searchQuery} />}
        {activeComponent === 'cart' && <Cart />}
        {activeComponent === 'checkout' && <Checkout />}
      </main>
    </div>
  );
}
