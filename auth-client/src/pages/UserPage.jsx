// src/components/Products.jsx
import React, { useEffect, useState, useContext } from 'react';
import UserContext from '../context/UserContext';
import { getAllProductos } from '../services/productoService';
import { getOrCreateCarrito } from '../services/carritoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import '../style/userPage.css';

export default function Products() {
  const [productos, setProductos] = useState([]);
  const { userId, carritoId, setCarritoId } = useContext(UserContext);
  const [isAuthenticated, setIsAuthenticated] = useState(true); // Track authentication status

  useEffect(() => {
    if (!userId) {
      console.warn('Usuario no autenticado. ID de usuario no encontrado.');
      setIsAuthenticated(false); // Mark user as unauthenticated
      return;
    }

    (async () => {
      try {
        const prods = await getAllProductos();
        setProductos(prods);

        if (!carritoId) {
          const cart = await getOrCreateCarrito(userId);
          setCarritoId(cart.idCarrito); // Ensure setCarritoId is called correctly
          localStorage.setItem('carritoId', cart.idCarrito);
        }
      } catch (e) {
        console.error(e);
        await MySwal.fire('Error', e.message || 'No se pudieron cargar productos.', 'error');
      }
    })();
  }, [userId, carritoId, setCarritoId]);

  const handleAdd = async (prod) => {
    if (!carritoId) {
      console.error('Carrito no inicializado');
      await MySwal.fire('Error', 'El carrito no está disponible.', 'error');
      return;
    }

    try {
      await addCarritoItem({
        idCarrito: carritoId,
        idProducto: prod.idProducto,
        cantidad: 1,
      });
      await MySwal.fire('Agregado', `"${prod.nombre}" añadido al carrito.`, 'success');
    } catch (e) {
      console.error(e);
      await MySwal.fire('Error', 'No se pudo añadir al carrito.', 'error');
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="unauthenticated-message">
        <h2>Usuario no autenticado</h2>
        <p>Por favor, inicia sesión para acceder a esta página.</p>
      </div>
    );
  }

  return (
    <div className="products-grid">
      {productos.map((p) => (
        <div key={p.idProducto} className="card">
          <img src={p.imagen} alt={p.nombre} />
          <h3>{p.nombre}</h3>
          <p>{p.descripcion}</p>
          <div className="actions">
            <span>${p.precio}</span>
            <button onClick={() => handleAdd(p)}>Añadir</button>
          </div>
        </div>
      ))}
    </div>
  );
}
