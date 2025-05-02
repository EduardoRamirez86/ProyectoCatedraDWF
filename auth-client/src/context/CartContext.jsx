// src/context/CartContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import { getOrCreateCarrito } from '../services/carritoService';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [carrito, setCarrito] = useState(null);

  useEffect(() => {
    const fetchCarrito = async () => {
      try {
        const idUser = parseInt(localStorage.getItem('userId'), 10);
        if (!idUser) throw new Error('ID de usuario no proporcionado');
        const cart = await getOrCreateCarrito(idUser);
        setCarrito(cart);
        localStorage.setItem('carritoId', cart.idCarrito);
      } catch (error) {
        console.error('Error al obtener o crear el carrito:', error);
      }
    };

    fetchCarrito();
  }, []);

  return (
    <CartContext.Provider value={{ carrito, setCarrito }}>
      {children}
    </CartContext.Provider>
  );
};
