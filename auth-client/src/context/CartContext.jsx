import React, { createContext, useState, useEffect } from 'react';
import { getOrCreateCarrito } from '../services/carritoService';
import { secureGetItem, secureSetItem } from '../utils/secureStorage';
import UserContext from '../context/UserContext';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true); // Para manejar el estado de carga
  const userContext = React.useContext(UserContext);

  useEffect(() => {
    const fetchCarrito = async () => {
      try {
        let idUser = userContext?.userId;
        if (!idUser) {
          idUser = parseInt(secureGetItem('userId'), 10);
          if (!idUser) {
            console.warn('ID de usuario no disponible, reintentando...');
            setTimeout(fetchCarrito, 1000); // Reintentar después de 1 segundo
            return;
          }
        }
        const cart = await getOrCreateCarrito(idUser);
        setCarrito(cart);
        secureSetItem('carritoId', cart.idCarrito.toString());
      } catch (error) {
        console.error('Error al obtener o crear el carrito:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchCarrito();
  }, [userContext]);

  return (
    <CartContext.Provider value={{ carrito, setCarrito, loading }}>
      {children}
    </CartContext.Provider>
  );
};