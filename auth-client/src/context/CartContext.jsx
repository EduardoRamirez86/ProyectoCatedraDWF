// CartContext.jsx
import React, { createContext, useState, useEffect, useContext } from 'react';
import { getOrCreateCarrito } from '../services/carritoService';
import { secureGetItem, secureSetItem } from '../utils/secureStorage';
import UserContext from './UserContext';
import { getParametroByClave } from '../services/parametroService';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true);
  const [envio, setEnvio] = useState(undefined);
  const { userId } = useContext(UserContext);

  useEffect(() => {
    let mounted = true;
    const fetchCarrito = async (idUser) => {
      try {
        const cart = await getOrCreateCarrito(idUser);
        if (mounted) setCarrito(cart);
        secureSetItem('carritoId', cart.idCarrito.toString());
      } catch (error) {
        if (mounted) setCarrito(null);
        console.error('Error al obtener o crear el carrito:', error);
      } finally {
        if (mounted) setLoading(false);
      }
    };

    const fetchEnvio = async () => {
      try {
        // Solo intenta obtener el parámetro si hay token
        const token = secureGetItem("token");
        if (!token) {
          setEnvio(5);
          return;
        }
        const param = await getParametroByClave('costo_envio');
        if (param && param.valor) {
          setEnvio(Number(param.valor));
        } else {
          setEnvio(5);
        }
      } catch (e) {
        // Solo loguea si hay token, si no, ignora el error
        const token = secureGetItem("token");
        if (token) {
          console.error('Error al obtener el costo de envío:', e);
        }
        setEnvio(5);
      }
    };

    const storedUserId = parseInt(secureGetItem('userId'), 10);
    const idToUse = userId || storedUserId;

    if (idToUse) {
      fetchCarrito(idToUse);
    } else {
      setCarrito(null);
      setLoading(false);
    }
    fetchEnvio();

    return () => { mounted = false; };
  }, [userId]);

  return (
    <CartContext.Provider value={{ carrito, setCarrito, loading, envio }}>
      {children}
    </CartContext.Provider>
  );
};