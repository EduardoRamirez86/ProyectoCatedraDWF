import React, { useEffect, useState, useContext, useCallback } from 'react';
import { getCarritoItems } from '../services/carritoService';
import { removeCarritoItem, updateCarritoItem } from '../services/carritoItemService';
import { CartContext } from '../context/CartContext';
import '../style/userPage.css';

export default function Cart() {
  const [items, setItems] = useState([]);
  const [subtotal, setSubtotal] = useState(0);
  const [total, setTotal] = useState(0);
  const envio = 5;
  const { carrito, loading } = useContext(CartContext);

  // Función para agrupar items optimizada con useCallback
const agruparItems = useCallback((itemsOriginales) => {
  const itemsMap = new Map();

  itemsOriginales.forEach((item) => {
    const key = item.producto?.idProducto;
    if (itemsMap.has(key)) {
      const existente = itemsMap.get(key);
      existente.cantidad += item.cantidad;
    } else {
      itemsMap.set(key, { ...item });
    }
  });

  return Array.from(itemsMap.values());
}, []);

  // Función para calcular totales
  const calculateTotals = useCallback((itemsList) => {
    const newSubtotal = itemsList.reduce(
      (sum, i) => sum + (i.cantidad * (i.producto?.precio || 0)),
      0
    );
    const newTotal = newSubtotal + envio;
    setSubtotal(newSubtotal);
    setTotal(newTotal);
  }, [envio]);

  // Carga inicial de items
  const load = useCallback(async () => {
    if (loading || !carrito?.idCarrito) return;
    
    try {
      const cartItems = await getCarritoItems(carrito.idCarrito);
      const groupedItems = agruparItems(cartItems);
      setItems(groupedItems);
      calculateTotals(groupedItems);
    } catch (error) {
      console.error('Error al cargar ítems:', error.message);
    }
  }, [carrito, loading, agruparItems, calculateTotals]);

  useEffect(() => {
    load();
  }, [load]);

  // Manejar eliminación de item
  const handleRemove = async (idProducto) => {
    const itemToRemove = items.find((i) => i.producto?.idProducto === idProducto);
    if (!itemToRemove) return;

    try {
      // Actualización optimista
      const updatedItems = items.filter((i) => i.producto.idProducto !== idProducto);
      setItems(updatedItems);
      calculateTotals(updatedItems);
      
      await removeCarritoItem(itemToRemove.idCarritoItem);
    } catch (error) {
      console.error('Error al eliminar ítem:', error.message);
      load(); // Recargar datos originales
    }
  };

  // Manejar cambio de cantidad
  const handleQty = async (item, delta) => {
    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad < 1) return;

    try {
      // Actualización optimista
      const updatedItems = items.map((i) =>
        i.producto.idProducto === item.producto.idProducto
          ? { ...i, cantidad: nuevaCantidad }
          : i
      );
      
      setItems(updatedItems);
      calculateTotals(updatedItems);

      // Persistir cambios en el servidor
      await updateCarritoItem({
        idCarritoItem: item.idCarritoItem,
        idCarrito: carrito.idCarrito,
        idProducto: item.producto.idProducto,
        cantidad: nuevaCantidad,
      });
    } catch (error) {
      console.error('Error al actualizar cantidad:', error.message);
      load(); // Recargar datos originales
    }
  };

  if (loading) return <div className="loading-message">Cargando carrito...</div>;

  return (
    <div className="cart-container">
      {items.length === 0 ? (
        <p className="empty-cart-message">Tu carrito está vacío.</p>
      ) : (
        <>
          <ul className="items-list">
            {items.map((item) => (
              <li
                key={item.producto.idProducto}
                className="cart-item"
              >
                <div className="image-container">
                  <img
                    src={item.producto.imagen || '/placeholder-product.jpg'}
                    alt={item.producto.nombre}
                    className="cart-item-image"
                  />
                </div>
                
                <div className="cart-item-details">
                  <h4 className="product-name">{item.producto.nombre}</h4>
                  <p className="product-price">
                    Precio unitario: ${item.producto.precio.toFixed(2)}
                  </p>
                  
                  <div className="qty-controls-container">
                    <div className="qty-controls">
                      <button
                        className="qty-button"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleQty(item, -1);
                        }}
                        disabled={item.cantidad <= 1}
                      >
                        -
                      </button>
                      <span className="qty-display">{item.cantidad}</span>
                      <button
                        className="qty-button"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleQty(item, 1);
                        }}
                      >
                        +
                      </button>
                    </div>
                    
                    <p className="item-total">
                      Total: ${(item.cantidad * item.producto.precio).toFixed(2)}
                    </p>
                  </div>
                </div>
                
                <button
                  className="remove-item-button"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemove(item.producto.idProducto);
                  }}
                >
                  Eliminar
                </button>
              </li>
            ))}
          </ul>

          <div className="cart-summary">
            <div className="summary-row">
              <span>Subtotal:</span>
              <span>${subtotal.toFixed(2)}</span>
            </div>
            <div className="summary-row">
              <span>Envío:</span>
              <span>${envio.toFixed(2)}</span>
            </div>
            <div className="summary-row total">
              <span>Total:</span>
              <span>${total.toFixed(2)}</span>
            </div>
          </div>
        </>
      )}
    </div>
  );
}