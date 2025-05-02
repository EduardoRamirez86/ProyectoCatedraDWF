import React, { useEffect, useState, useContext, useCallback } from 'react';
import { getCarritoItems } from '../services/carritoService';
import { removeCarritoItem, addCarritoItem } from '../services/carritoItemService';
import UserContext from '../context/UserContext';
import '../style/userPage.css';

export default function Cart() {
  const [items, setItems] = useState([]);
  const { userId } = useContext(UserContext); // Obtener userId del contexto

  const load = useCallback(async () => {
    if (!userId) {
      console.error('ID de usuario no disponible.');
      return;
    }
    try {
      const cartItems = await getCarritoItems(userId);
      setItems(cartItems);
    } catch (error) {
      console.error('Error al cargar los ítems del carrito:', error.message);
    }
  }, [userId]);

  useEffect(() => {
    load();
  }, [load]);

  const handleRemove = async (id) => {
    await removeCarritoItem(id);
    load();
  };

  const handleQty = async (item, delta) => {
    const newQty = item.cantidad + delta;
    if (newQty < 1) return;
    await addCarritoItem({
      idCarrito: item.idCarrito,
      idProducto: item.producto.idProducto,
      cantidad: newQty,
    });
    load();
  };

  const subtotal = items.reduce((sum, i) => sum + i.cantidad * i.producto.precio, 0);
  const envio = 5;
  const total = subtotal + envio;

  return (
    <div className="cart-container">
      {items.length === 0 ? (
        <p>Carrito vacío</p>
      ) : (
        <>
          <ul className="items-list">
            {items.map((i) => (
              <li key={i.idCarritoItem}>
                <img src={i.producto.imagen} alt={i.producto.nombre} />
                <div>
                  <h4>{i.producto.nombre}</h4>
                  <div className="qty-controls">
                    <button onClick={() => handleQty(i, -1)}>-</button>
                    <span>{i.cantidad}</span>
                    <button onClick={() => handleQty(i, 1)}>+</button>
                  </div>
                  <button onClick={() => handleRemove(i.idCarritoItem)}>🗑️</button>
                </div>
              </li>
            ))}
          </ul>
          <div className="summary">
            <p>Subtotal: ${subtotal.toFixed(2)}</p>
            <p>Envío: ${envio.toFixed(2)}</p>
            <h3>Total: ${total.toFixed(2)}</h3>
          </div>
        </>
      )}
    </div>
  );
}
