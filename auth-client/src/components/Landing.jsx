// src/components/Landing.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import '../style/Landing.css';

export default function Landing() {
  const products = [
    { id: 1, name: 'Camiseta Clásica', price: '$19.99', image: 'https://via.placeholder.com/300?text=Camiseta' },
    { id: 2, name: 'Jeans Comfort',    price: '$49.99', image: 'https://via.placeholder.com/300?text=Jeans' },
    { id: 3, name: 'Chaqueta Urbana',  price: '$89.99', image: 'https://via.placeholder.com/300?text=Chaqueta' },
  ];

  return (
    <div className="landing">
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
          {products.map(p => (
            <motion.div
              key={p.id}
              className="product-card"
              whileHover={{ scale: 1.05 }}
              transition={{ type: 'spring', stiffness: 300 }}
            >
              <img src={p.image} alt={p.name} className="product-card__img" />
              <div className="product-card__info">
                <h3 className="product-card__name">{p.name}</h3>
                <p className="product-card__price">{p.price}</p>
              </div>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
}