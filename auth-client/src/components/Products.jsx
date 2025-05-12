import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllProductos, getRecommendedProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import { CartContext } from '../context/CartContext';
import UserContext from '../context/UserContext';
import '../style/Products.css';

export default function Products({ searchQuery }) {
  const [allProductos, setAllProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const { carrito } = useContext(CartContext);
  const { userId } = useContext(UserContext);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const prods = await getAllProductos();

        // si hay userId, marca recomendados
        let marcados = prods;
        if (userId) {
          const recs = await getRecommendedProductos(userId);
          const recIds = new Set(recs.map(r => r.idProducto));
          marcados = prods.map(p => ({
            ...p,
            recomendado: recIds.has(p.idProducto)
          }));
        }

        setAllProductos(marcados);
      } catch (err) {
        console.error(err);
        await MySwal.fire('Error', 'No se pudieron cargar los productos.', 'error');
      } finally {
        setLoading(false);
      }
    })();
  }, [userId]);

  if (loading) {
    return <p className="loading">Cargando productos...</p>;
  }

  // aplica búsqueda
  const filtrados = allProductos.filter(p =>
    p.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // sección recomendados
  const recomendados = userId
    ? filtrados.filter(p => p.recomendado).slice(0, 5)
    : [];

  // quita recomendados del resto
  const sinRecomendados = filtrados.filter(p => !p.recomendado);

  // temporada: top 5 más caros entre los sinRecomendados
  const temporada = [...sinRecomendados]
    .sort((a, b) => b.precio - a.precio)
    .slice(0, 5);

  // el resto: quita los de temporada también
  const restantes = sinRecomendados.filter(
    p => !temporada.some(t => t.idProducto === p.idProducto)
  ).slice(0, 5);

  const handleAdd = async p => {
    try {
      if (!carrito) throw new Error('Carrito no disponible');
      await addCarritoItem({
        idCarrito: carrito.idCarrito,
        idProducto: p.idProducto,
        cantidad: 1
      });
      await MySwal.fire('Agregado', `"${p.nombre}" añadido al carrito.`, 'success');
    } catch (err) {
      console.error(err);
      await MySwal.fire('Error', err.message || 'No se pudo añadir al carrito.', 'error');
    }
  };

  const renderCard = p => (
    <div
      key={p.idProducto}
      className="product-card"
      onClick={() => navigate(`/producto/${p.idProducto}`)}
    >
      <div className="product-image-container">
        <img src={p.imagen} alt={p.nombre} className="product-image" />
        <div className="product-hover-info">
          <p className="product-description">{p.descripcion}</p>
          <button
            className="add-to-cart-btn"
            onClick={e => { e.stopPropagation(); handleAdd(p); }}
          >
            Añadir al carrito
          </button>
        </div>
      </div>
      <div className="product-details">
        <h3 className="product-name">{p.nombre}</h3>
        <span className="product-price">${p.precio.toFixed(2)}</span>
      </div>
    </div>
  );

  return (
    <div className="products-wrapper">

      {recomendados.length > 0 && (
        <section className="products-section">
          <h2>Recomendados para ti</h2>
          <div className="products-grid">
            {recomendados.map(renderCard)}
          </div>
        </section>
      )}

      <section className="products-section">
        <h2>Temporada</h2>
        <div className="products-grid">
          {temporada.map(renderCard)}
        </div>
      </section>

      <section className="products-section">
        <h2>Otros productos</h2>
        <div className="products-grid">
          {restantes.map(renderCard)}
        </div>
      </section>

    </div>
  );
}
