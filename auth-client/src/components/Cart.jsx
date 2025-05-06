import React, { useEffect, useState, useContext, useCallback } from 'react';
import { getCarritoItems } from '../services/carritoService';
import { removeCarritoItem, updateCarritoItem } from '../services/carritoItemService';
import UserContext from '../context/UserContext';
import '../style/userPage.css';

export default function Cart() {
  const [items, setItems] = useState([]);
  const { carritoId } = useContext(UserContext);
  const [subtotal, setSubtotal] = useState(0);
  const envio = 5;

  const agruparItems = (itemsOriginales) => {
    const agrupados = [];

    itemsOriginales.forEach((item) => {
      const existente = agrupados.find(
        (i) => i.producto?.idProducto === item.producto?.idProducto
      );

      if (existente) {
        existente.cantidad += item.cantidad;
        existente.idCarritoItem = item.idCarritoItem; // último ID usado
      } else {
        agrupados.push({ ...item });
      }
    });

    return agrupados;
  };

  const load = useCallback(async () => {
    if (!carritoId) {
      console.error('ID de carrito no disponible.');
      return;
    }

    try {
      const cartItems = await getCarritoItems(carritoId);
      const agrupados = agruparItems(cartItems);
      setItems(agrupados);
    } catch (error) {
      console.error('Error al cargar ítems:', error.message);
    }
  }, [carritoId]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    const nuevoSubtotal = items.reduce((sum, i) => {
      const precio = i.producto?.precio || 0;
      return sum + i.cantidad * precio;
    }, 0);
    setSubtotal(nuevoSubtotal);
  }, [items]);

  const handleRemove = async (idProducto) => {
    const item = items.find(i => i.producto?.idProducto === idProducto);
    if (!item) return;

    try {
      await removeCarritoItem(item.idCarritoItem);
      load();
    } catch (error) {
      console.error('Error al eliminar ítem:', error.message);
    }
  };

  const handleQty = async (item, delta) => {
    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad < 1) return;

    try {
      await updateCarritoItem({
        idCarritoItem: item.idCarritoItem,
        idCarrito: carritoId,
        idProducto: item.producto.idProducto,
        cantidad: nuevaCantidad
      });
      

      load();
    } catch (error) {
      console.error('Error al actualizar cantidad:', error.message);
    }
  };

  const total = subtotal + envio;

  return (
    <div className="cart-container">
      {items.length === 0 ? (
        <p>Tu carrito está vacío.</p>
      ) : (
        <>
          <ul className="items-list">
            {items.map((item) => (
              <li key={item.producto?.idProducto} className="cart-item">
                <img
                  src={item.producto?.imagen || 'placeholder.jpg'}
                  alt={item.producto?.nombre || 'Producto'}
                  className="cart-item-image"
                />
                <div className="cart-item-details">
                  <h4>{item.producto?.nombre || 'Producto desconocido'}</h4>
                  <p>Precio: ${item.producto?.precio?.toFixed(2) || '0.00'}</p>
                  <div className="qty-controls">
                    <button onClick={() => handleQty(item, -1)}>-</button>
                    <span>{item.cantidad}</span>
                    <button onClick={() => handleQty(item, 1)}>+</button>
                  </div>
                  <p>Total: ${(item.cantidad * (item.producto?.precio || 0)).toFixed(2)}</p>
                </div>
                <button
                  onClick={() => handleRemove(item.producto?.idProducto)}
                  className="remove-item-btn"
                >
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
