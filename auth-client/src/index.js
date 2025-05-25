import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { UserProvider } from './context/UserContext';
import { CartProvider } from './context/CartContext';

const root = createRoot(document.getElementById('root'));
root.render(
  <UserProvider>
    <AuthProvider>
      <CartProvider>
        <App />
      </CartProvider>
    </AuthProvider>
  </UserProvider>
);