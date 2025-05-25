// src/context/CartContext.jsx
import React, { createContext, useState, useEffect, useContext } from 'react';
import { getOrCreateCarrito } from '../services/carritoService';
import { secureGetItem, secureSetItem } from '../utils/secureStorage';
import UserContext from './UserContext';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true);
  const envio = 5; // Shipping fee
  const { userId } = useContext(UserContext);

  useEffect(() => {
    const fetchCarrito = async (idUser) => {
      try {
        const cart = await getOrCreateCarrito(idUser);
        setCarrito(cart);
        secureSetItem('carritoId', cart.idCarrito.toString());
      } catch (error) {
        console.error('Error al obtener o crear el carrito:', error);
      } finally {
        setLoading(false);
      }
    };

    // Solo obtener o crear carrito si el usuario está autenticado
    const storedUserId = parseInt(secureGetItem('userId'), 10);
    const idToUse = userId || storedUserId;

    if (idToUse) {
      fetchCarrito(idToUse);
    } else {
      // Usuario no autenticado: no hay carrito que pedir
      setCarrito(null);
      setLoading(false);
    }
  }, [userId]);

  return (
    <CartContext.Provider value={{ carrito, setCarrito, loading, envio }}>
      {children}
    </CartContext.Provider>
  );
};
