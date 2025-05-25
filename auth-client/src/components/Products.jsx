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

// Puedes recibir searchQuery como prop y filtrar productos aquí
export default function Products({ searchQuery = "" }) {
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
    className="product-card bg-white rounded-lg shadow-md overflow-hidden cursor-pointer"
    onClick={() => navigate(`/producto/${p.idProducto}`)}
  >
    {/* Imagen del producto */}
    <div className="relative">
      <img src={p.imagen} alt={p.nombre} className="w-full h-40 object-cover" />
      {/* Botón de carrito en la esquina inferior derecha */}
      <button
        className="absolute bottom-2 right-2 bg-blue-500 text-white px-3 py-1 rounded-full hover:bg-blue-600 transition"
        onClick={e => { e.stopPropagation(); handleAdd(p); }}
      >
        <i className="fas fa-shopping-cart"></i>
      </button>
    </div>

    {/* Detalles del producto */}
    <div className="p-4">
      <h3 className="product-name font-semibold text-lg mb-1">{p.nombre}</h3>
      <p className="product-description text-gray-600 text-sm mb-2">{p.descripcion}</p>
      <span className="product-price text-blue-500 font-bold">${p.precio.toFixed(2)}</span>
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
      <div className="flex justify-center items-center gap-4 mt-8">
        <button
          onClick={() => loadPage(page - 1)}
          disabled={page === 0 || loading}
          className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50 transition"
        >
          <i className="fas fa-chevron-left mr-1"></i> Anterior
        </button>
        <span className="text-gray-700 font-medium">
          Página <span className="font-bold">{page + 1}</span> de <span className="font-bold">{totalPages}</span>
        </span>
        <button
          onClick={() => loadPage(page + 1)}
          disabled={page + 1 >= totalPages || loading}
          className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50 transition"
        >
          Siguiente <i className="fas fa-chevron-right ml-1"></i>
        </button>
      </div>
    </div>
  );
}
