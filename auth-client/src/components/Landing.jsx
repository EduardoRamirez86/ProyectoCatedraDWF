// src/components/Landing.jsx
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Header from './Header';
import { getAllProductos } from '../services/productoService';
import '../style/Landing.css';

export default function Landing() {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        const data = await getAllProductos();
        setProductos(data);
      } catch (error) {
        console.error('Error al obtener los productos:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProductos();
  }, []);

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
          {productos.length > 0 ? (
            productos.map(p => (
              <motion.div
                key={p.idProducto}
                className="product-card"
                whileHover={{ scale: 1.05 }}
                transition={{ type: 'spring', stiffness: 300 }}
              >
                <img
                  src={p.imagen || 'https://via.placeholder.com/300'}
                  alt={p.nombre}
                  className="product-card__img"
                />
                <div className="product-card__info">
                  <h3 className="product-card__name">{p.nombre}</h3>
                  <p className="product-card__price">
                    {new Intl.NumberFormat('es-SV', {
                      style: 'currency',
                      currency: 'USD'
                    }).format(p.precio)}
                  </p>
                </div>
              </motion.div>
            ))
          ) : (
            <p>No se encontraron productos.</p>
          )}
        </div>
      </section>
    </div>
  );
}
