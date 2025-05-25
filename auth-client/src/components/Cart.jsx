import React, { useEffect, useState, useContext, useCallback } from 'react';
import { getCarritoItems } from '../services/carritoService';
import { removeCarritoItem, updateCarritoItem } from '../services/carritoItemService';
import { CartContext } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';
import '../style/userPage.css';

export default function Cart() {
  const [items, setItems] = useState([]);
  const [subtotal, setSubtotal] = useState(0);
  const [total, setTotal] = useState(0);
  const envio = 5;
  const { carrito, loading } = useContext(CartContext);
  const navigate = useNavigate();

  // Agrupa items por producto
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

  // Calcula totales
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

  // Eliminar item
  const handleRemove = async (idProducto) => {
    const itemToRemove = items.find((i) => i.producto?.idProducto === idProducto);
    if (!itemToRemove) return;
    try {
      const updatedItems = items.filter((i) => i.producto.idProducto !== idProducto);
      setItems(updatedItems);
      calculateTotals(updatedItems);
      await removeCarritoItem(itemToRemove.idCarritoItem);
    } catch (error) {
      console.error('Error al eliminar ítem:', error.message);
      load();
    }
  };

  // Cambiar cantidad
  const handleQty = async (item, delta) => {
    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad < 1) return;
    try {
      const updatedItems = items.map((i) =>
        i.producto.idProducto === item.producto.idProducto
          ? { ...i, cantidad: nuevaCantidad }
          : i
      );
      setItems(updatedItems);
      calculateTotals(updatedItems);
      await updateCarritoItem({
        idCarritoItem: item.idCarritoItem,
        idCarrito: carrito.idCarrito,
        idProducto: item.producto.idProducto,
        cantidad: nuevaCantidad,
      });
    } catch (error) {
      console.error('Error al actualizar cantidad:', error.message);
      load();
    }
  };

  if (loading) return <div className="text-center py-10 text-gray-500">Cargando carrito...</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-shopping-cart text-indigo-400"></i>
        Mi Carrito
      </h1>
      <div className="bg-white shadow-md rounded-xl p-6">
        {items.length === 0 ? (
          <p className="text-center text-gray-500">Tu carrito está vacío.</p>
        ) : (
          <>
            <ul className="divide-y">
              {items.map((item) => (
                <li
                  key={item.producto.idProducto}
                  className="flex flex-col md:flex-row items-center gap-4 py-4"
                >
                  <div className="w-24 h-24 flex-shrink-0 flex items-center justify-center bg-gray-50 rounded-lg overflow-hidden">
                    <img
                      src={item.producto.imagen || '/placeholder-product.jpg'}
                      alt={item.producto.nombre}
                      className="object-cover w-full h-full"
                    />
                  </div>
                  <div className="flex-1 w-full">
                    <h4 className="font-semibold text-lg">{item.producto.nombre}</h4>
                    <p className="text-gray-600 text-sm mb-1">{item.producto.descripcion}</p>
                    <span className="text-indigo-600 font-bold">
                      ${item.producto.precio.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex flex-col items-center gap-2">
                    <div className="flex items-center gap-2">
                      <button
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleQty(item, -1);
                        }}
                        disabled={item.cantidad <= 1}
                      >
                        -
                      </button>
                      <span className="font-semibold">{item.cantidad}</span>
                      <button
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleQty(item, 1);
                        }}
                      >
                        +
                      </button>
                    </div>
                    <span className="text-gray-700 text-sm">
                      Total: ${(item.cantidad * item.producto.precio).toFixed(2)}
                    </span>
                  </div>
                  <button
                    className="ml-4 px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition"
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
            <div className="border-t pt-6 mt-6 flex flex-col md:flex-row justify-between items-center gap-6">
              <div className="space-y-2">
                <div className="flex justify-between gap-8">
                  <span className="font-medium">Subtotal:</span>
                  <span>${subtotal ? subtotal.toFixed(2) : '0.00'}</span>
                </div>
                <div className="flex justify-between gap-8">
                  <span className="font-medium">Envío:</span>
                  <span>${envio ? envio.toFixed(2) : '0.00'}</span>
                </div>
                <div className="flex justify-between gap-8 text-lg font-bold text-indigo-700">
                  <span>Total:</span>
                  <span>${total ? total.toFixed(2) : '0.00'}</span>
                </div>
              </div>
              <button
                className="px-6 py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition w-full md:w-auto"
                onClick={() => navigate('/user/checkout', { state: { total } })}
              >
                Proceder al Pago
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}