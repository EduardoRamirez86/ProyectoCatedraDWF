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
      <div className="unauthenticated-message slide-in flex flex-col items-center justify-center min-h-screen bg-gray-50">
        <div className="bg-white rounded-lg shadow-md p-8 mt-12">
          <h2 className="text-2xl font-bold mb-2 text-gray-700">Usuario no autenticado</h2>
          <p className="text-gray-600">Por favor, inicia sesión para acceder a esta página.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="user-page min-h-screen bg-gray-50 flex flex-col">
      <header className="bg-white shadow-sm">
        <nav className="container mx-auto px-4 py-3 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex space-x-4">
          </div>
          <div className="relative flex-1 md:max-w-md">
            <input
              type="text"
              placeholder="Buscar productos..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="search-bar w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 transition"
            />
            <i className="fas fa-search absolute right-3 top-3 text-gray-400"></i>
          </div>
        </nav>
      </header>

      <main className="flex-1 container mx-auto px-4 py-6">
        <div className="slide-in">
          {activeComponent === 'products' && (
            <Products searchQuery={searchQuery} />
          )}
        </div>
      </main>
    </div>
  );
}
