// src/components/Products.jsx
import React, { useEffect, useState, useContext } from 'react';
import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import '../style/Products.css';
import UserContext from '../context/UserContext'; // Import UserContext to get carritoId

export default function Products({ searchQuery }) {
  const [productos, setProductos] = useState([]);
  const { carritoId } = useContext(UserContext); // Get carritoId from context

  useEffect(() => {
    (async () => {
      try {
        const prods = await getAllProductos();
        setProductos(prods);
      } catch (e) {
        console.error(e);
        await MySwal.fire('Error', 'No se pudieron cargar productos.', 'error');
      }
    })();
  }, []);

  const filteredProducts = productos.filter((p) =>
    p.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleAdd = async (prod) => {
    if (!carritoId) {
      console.error('ID de carrito no disponible.');
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

  return (
    <div className="products-grid">
      {filteredProducts.map((p) => (
        <div key={p.idProducto} className="product-card">
          <img src={p.imagen} alt={p.nombre} className="product-image" />
          <h3 className="product-name">{p.nombre}</h3>
          <p className="product-description">{p.descripcion}</p>
          <div className="product-actions">
            <span className="product-price">${p.precio}</span>
            <button className="add-to-cart-btn" onClick={() => handleAdd(p)}>
              Añadir al carrito
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
