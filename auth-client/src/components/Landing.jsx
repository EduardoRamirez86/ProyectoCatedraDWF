// src/components/Landing.jsx
import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Header from './Header';
import { getAllProductos, getRecommendedProductos } from '../services/productoService';
import UserContext from '../context/UserContext';
import '../style/Landing.css';

export default function Landing() {
  const { userId } = useContext(UserContext);
  const [productosDestacados, setProductosDestacados] = useState([]);
  const [recomendados, setRecomendados] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        const all = await getAllProductos();
        // Ordenar por precio descendente y tomar los 5 más caros
        const destacados = all.sort((a, b) => b.precio - a.precio).slice(0, 5);
        setProductosDestacados(destacados);
        // Solo intentar recomendaciones si hay usuario logueado
        if (userId) {
          const rec = await getRecommendedProductos(userId);
          setRecomendados(rec);
        }
      } catch (error) {
        console.error('Error al obtener los productos:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchProductos();
  }, [userId]);

  if (loading) {
    return (
      <div className="landing">
        <Header />
        <p className="loading">Cargando productos...</p>
      </div>
    );
  }

  return (
    <div className="landing">
      <Header />

      <section className="hero">
        <div className="hero__overlay" />
        <motion.div
          className="hero__content"
          initial={{ scale: 0.85, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.7 }}
        >
          <h1 className="hero__title">Bienvenido a Fashion Store</h1>
          <p className="hero__subtitle">Descubre tu estilo con nuestras últimas colecciones</p>
          <Link to="/register" className="hero__cta">¡Comienza Ahora!</Link>
        </motion.div>
      </section>

      <section className="products">
        <h2 className="products__title">Productos Destacados</h2>
        <div className="products__grid">
          {productosDestacados.map(p => (
            <motion.div key={p.idProducto} className="product-card" whileHover={{ scale: 1.05 }} transition={{ type: 'spring', stiffness: 300 }}>
              <img src={p.imagen || 'https://via.placeholder.com/300'} alt={p.nombre} className="product-card__img" />
              <div className="product-card__info">
                <h3 className="product-card__name">{p.nombre}</h3>
                <p className="product-card__price">{new Intl.NumberFormat('es-SV',{style:'currency',currency:'USD'}).format(p.precio)}</p>
              </div>
            </motion.div>
          ))}
        </div>
      </section>

      {userId && recomendados.length > 0 && (
        <section className="products recomendados">
          <h2 className="products__title">Recomendados para ti</h2>
          <div className="products__grid">
            {recomendados.map(p => (
              <motion.div key={p.idProducto} className="product-card" whileHover={{ scale: 1.05 }} transition={{ type: 'spring', stiffness: 300 }}>
                <img src={p.imagen || 'https://via.placeholder.com/300'} alt={p.nombre} className="product-card__img" />
                <div className="product-card__info">
                  <h3 className="product-card__name">{p.nombre}</h3>
                  <p className="product-card__price">{new Intl.NumberFormat('es-SV',{style:'currency',currency:'USD'}).format(p.precio)}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}