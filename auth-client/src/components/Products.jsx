import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import { CartContext } from '../context/CartContext';
import '../style/Products.css';

export default function Products({ searchQuery }) {
  const [productos, setProductos] = useState([]);
  const { carrito } = useContext(CartContext);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const prods = await getAllProductos();
        setProductos(prods);
      } catch (e) {
        console.error('Error al cargar productos:', e);
        await MySwal.fire('Error', 'No se pudieron cargar los productos.', 'error');
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
      console.error('Error al añadir al carrito:', e);
      await MySwal.fire('Error', 'No se pudo añadir al carrito.', 'error');
    }
  };

  const handleProductClick = (idProducto) => {
    navigate(`/producto/${idProducto}`);
  };

  return (
    <div className="products-grid">
      {filteredProducts.map((p) => (
        <div
          key={p.idProducto}
          className="product-card"
          onClick={() => handleProductClick(p.idProducto)}
          style={{ cursor: 'pointer' }}
        >
          <div className="product-image-container">
            <img
              src={p.imagen}
              alt={p.nombre}
              className="product-image"
            />
            <div className="product-hover-info">
              <p className="product-description">{p.descripcion}</p>
              <button
                className="add-to-cart-btn"
                onClick={(e) => {
                  e.stopPropagation(); // evita que el click se propague y redirija
                  handleAdd(p);
                }}
              >
                Añadir al carrito
              </button>
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
