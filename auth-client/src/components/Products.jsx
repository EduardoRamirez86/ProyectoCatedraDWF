// src/components/Products.jsx
import React, { useEffect, useState, useContext } from 'react';
import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import '../style/userPage.css';
import { CartContext } from '../context/CartContext';

export default function Products() {
  const [productos, setProductos] = useState([]);
  const { carrito } = useContext(CartContext);

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

  const handleAdd = async (prod) => {
    try {
      if (!carrito) {
        await MySwal.fire('Error', 'Carrito no disponible.', 'error');
        return;
      }
      await addCarritoItem({ idCarrito: carrito.idCarrito, idProducto: prod.idProducto, cantidad: 1 });
      await MySwal.fire('Agregado', `"${prod.nombre}" añadido al carrito.`, 'success');
    } catch (e) {
      console.error(e);
      await MySwal.fire('Error', 'No se pudo añadir al carrito.', 'error');
    }
  };

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
