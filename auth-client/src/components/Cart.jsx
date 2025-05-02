import React, { useEffect, useState, useContext, useCallback } from 'react';
import { getCarritoItems } from '../services/carritoService';
import { removeCarritoItem, addCarritoItem } from '../services/carritoItemService';
import UserContext from '../context/UserContext';
import '../style/userPage.css';

export default function Cart() {
  const [items, setItems] = useState([]);
  const { carritoId } = useContext(UserContext); // Get carritoId from context

  const load = useCallback(async () => {
    if (!carritoId) {
      console.error('ID de carrito no disponible.');
      return;
    }
    try {
      const cartItems = await getCarritoItems(carritoId);
      console.log('Carrito Items:', cartItems); // Debugging log to verify data structure
      setItems(cartItems);
    } catch (error) {
      console.error('Error al cargar los ítems del carrito:', error.message);
    }
  }, [carritoId]);

  useEffect(() => {
    load();
  }, [load]);

  const handleRemove = async (id) => {
    try {
      await removeCarritoItem(id);
      load();
    } catch (error) {
      console.error('Error al eliminar ítem:', error.message);
    }
  };

  const handleQty = async (item, delta) => {
    const newQty = item.cantidad + delta;
    if (newQty < 1) return;
    try {
      await addCarritoItem({
        idCarrito: item.idCarrito,
        idProducto: item.producto?.idProducto, // Ensure producto is defined
        cantidad: newQty,
      });
      load();
    } catch (error) {
      console.error('Error al actualizar cantidad:', error.message);
    }
  };

  const subtotal = items.reduce((sum, i) => {
    const precio = i.producto?.precio || 0; // Default to 0 if precio is undefined
    return sum + i.cantidad * precio;
  }, 0);

  const envio = 5; // Fixed shipping cost
  const total = subtotal + envio;

  return (
    <div className="cart-container">
      {items.length === 0 ? (
        <p>Tu carrito está vacío.</p>
      ) : (
        <>
          <ul className="items-list">
            {items.map((i) => (
              <li key={i.idCarritoItem} className="cart-item">
                <img
                  src={i.producto?.imagen || 'placeholder.jpg'} // Fallback to a placeholder image
                  alt={i.producto?.nombre || 'Producto'}
                  className="cart-item-image"
                />
                <div className="cart-item-details">
                  <h4>{i.producto?.nombre || 'Producto desconocido'}</h4>
                  <p>Precio: ${i.producto?.precio?.toFixed(2) || '0.00'}</p>
                  <div className="qty-controls">
                    <button onClick={() => handleQty(i, -1)}>-</button>
                    <span>{i.cantidad}</span>
                    <button onClick={() => handleQty(i, 1)}>+</button>
                  </div>
                  <p>Total: ${(i.cantidad * (i.producto?.precio || 0)).toFixed(2)}</p>
                </div>
                <button onClick={() => handleRemove(i.idCarritoItem)} className="remove-item-btn">
                  🗑️
                </button>
              </li>
            ))}
          </ul>
          <div className="cart-summary">
            <p>Subtotal: ${subtotal.toFixed(2)}</p>
            <p>Envío: ${envio.toFixed(2)}</p>
            <h3>Total: ${total.toFixed(2)}</h3>
          </div>
        </>
      )}
    </div>
  );
}
