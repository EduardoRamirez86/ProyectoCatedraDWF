import React, { useEffect, useState, useContext } from 'react';
import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import { CartContext } from '../context/CartContext';
import '../style/Products.css';

export default function Products({ searchQuery }) {
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

  const filteredProducts = productos.filter((p) =>
    p.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

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
      {filteredProducts.map((p) => (
        <div key={p.idProducto} className="product-card">
          <div className="product-image-container">
            <img src={p.imagen} alt={p.nombre} className="product-image" />
            <div className="product-hover-info">
              <p className="product-description">{p.descripcion}</p>
              <button className="add-to-cart-btn" onClick={() => handleAdd(p)}>Añadir al carrito</button>
            </div>
          </div>
          <div className="product-details">
            <h3 className="product-name">{p.nombre}</h3>
            <span className="product-price">${p.precio}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
