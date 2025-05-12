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
  const [scrollOffset, setScrollOffset] = useState(0);

  useEffect(() => {
    const handleScroll = () => setScrollOffset(window.scrollY);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        const all = await getAllProductos();
        const destacados = all.sort((a, b) => b.precio - a.precio).slice(0, 5);
        setProductosDestacados(destacados);
        if (userId) {
          const rec = await getRecommendedProductos(userId);
          setRecomendados(rec);
        }
      } catch (e) {
        console.error(e);
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

  const renderCard = p => (
    <motion.div
      key={p.idProducto}
      className="product-card"
      whileHover={{ scale: 1.03, y: -5 }}
      transition={{ type: "spring", stiffness: 300, damping: 20 }}
    >
      <img
        src={p.imagen || "https://via.placeholder.com/300"}
        alt={p.nombre}
        className="product-card__img"
      />
      <div className="product-card__info">
        <h3 className="product-card__name">{p.nombre}</h3>
        <p className="product-card__price">
          {new Intl.NumberFormat("es-SV", {
            style: "currency",
            currency: "USD",
          }).format(p.precio)}
        </p>
        <motion.div
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.95 }}
        >
          <Link to={`/producto/${p.idProducto}`} className="view-more-button">
            Ver más →
          </Link>
        </motion.div>
      </div>
    </motion.div>
  );

  return (
    <div className="landing">
      <Header />

      <section className="hero">
        <div className="hero__overlay" />
        <motion.div
          className="hero__content"
          initial={{ opacity: 0, y: 20, scale: 0.9 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
        >
          <h1 className="hero__title">Encuentra tu Estilo Único</h1>
          <p className="hero__subtitle">
            Descubre las últimas tendencias y eleva tu guardarropa con nuestra exclusiva colección.
          </p>
          <Link to="/register" className="hero__cta">
            Únete Ahora y Obtén 15% de Descuento
          </Link>
        </motion.div>
        <div
          className="hero__background"
          style={{ transform: `translateY(${scrollOffset * 0.2}px)` }}
        />
      </section>

      <section className="products">
        <h2 className="products__title">Novedades de Temporada</h2>
        <div className="products__grid">
          {productosDestacados.map(renderCard)}
        </div>
      </section>

      {userId && recomendados.length > 0 && (
        <section className="products recomendados">
          <h2 className="products__title">Recomendado para ti</h2>
          <div className="products__grid">
            {recomendados.map(renderCard)}
          </div>
        </section>
      )}
    </div>
  );
}
