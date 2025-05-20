import React, { useEffect, useState, useContext, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getAllProductosPaged,
  getRecommendedProductos
} from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import MySwal from '../utils/swal';
import { CartContext } from '../context/CartContext';
import UserContext from '../context/UserContext';
import '../style/Products.modules.css';

export default function Products({ searchQuery }) {
  const [allProductos, setAllProductos]   = useState([]);
  const [loading, setLoading]             = useState(true);
  const [page, setPage]                   = useState(0);
  const [totalPages, setTotalPages]       = useState(1);
  const size = 10;

  const { carrito } = useContext(CartContext);
  const { userId }  = useContext(UserContext);
  const navigate    = useNavigate();

  const loadPage = useCallback(async (pg = 0) => {
    setLoading(true);
    try {
      // 1) Traer recomendaciones (sin paginar)
      let recomendadosIds = [];
      if (userId) {
        const recs = await getRecommendedProductos(userId);
        recomendadosIds = recs.map(r => r.idProducto);
      }

      // 2) Traer página de productos
      const res = await getAllProductosPaged(pg, size);
      const items = res.items.map(p => ({
        ...p,
        recomendado: recomendadosIds.includes(p.idProducto)
      }));

      setAllProductos(items);
      setPage(res.page);
      setTotalPages(res.totalPages);
    } catch (err) {
      console.error(err);
      await MySwal.fire('Error', 'No se pudieron cargar los productos.', 'error');
    } finally {
      setLoading(false);
    }
  }, [userId, size]);

  useEffect(() => {
    loadPage(0);
  }, [loadPage]);

  if (loading) {
    return <p className="loading">Cargando productos…</p>;
  }

  // Filtrar por búsqueda
  const filtrados = allProductos.filter(p =>
    p.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const recomendados     = filtrados.filter(p => p.recomendado);
  const sinRecomendados  = filtrados.filter(p => !p.recomendado);

  // Temporada: top 5 no recomendados por precio
  const temporada = [...sinRecomendados]
    .sort((a, b) => b.precio - a.precio)
    .slice(0, 5);

  // Restantes: resto de la página menos temporada
  const restantes = sinRecomendados
    .filter(p => !temporada.some(t => t.idProducto === p.idProducto))
    .slice(0, 5);

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

      {/* Paginación */}
      <div className="pagination">
        <button
          onClick={() => loadPage(page - 1)}
          disabled={page === 0 || loading}
        >
          « Anterior
        </button>
        <span>
          Página {page + 1} de {totalPages}
        </span>
        <button
          onClick={() => loadPage(page + 1)}
          disabled={page + 1 >= totalPages || loading}
        >
          Siguiente »
        </button>
      </div>
    </div>
  );
}
