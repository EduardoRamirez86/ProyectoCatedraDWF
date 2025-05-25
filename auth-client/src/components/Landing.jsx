import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import Header from './Header';
import Footer from './Footer';
import { getAllProductos, getRecommendedProductos } from '../services/productoService';
import UserContext from '../context/UserContext';

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
      <div style={{ fontFamily: "'Helvetica Neue', sans-serif", color: "#333", minHeight: "100vh", margin: 0, padding: 0 }}>
        <Header />
        <p style={{ textAlign: "center", marginTop: "2rem" }}>Cargando productos...</p>
      </div>
    );
  }

  const renderCard = p => (
    <div
      key={p.idProducto}
      className="product-card"
      style={{
        background: "#fff",
        borderRadius: "1rem",
        boxShadow: "0 2px 8px rgba(0,0,0,0.08)",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        margin: "0 auto",
        maxWidth: 340,
        border: "1px solid #f3f4f6"
      }}
    >
      <div style={{
        width: "100%",
        height: 180,
        background: "#f3f4f6",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        position: "relative"
      }}>
        <img
          src={p.imagen || "https://via.placeholder.com/300"}
          alt={p.nombre}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "cover",
            borderTopLeftRadius: "1rem",
            borderTopRightRadius: "1rem"
          }}
        />
        {/* Badge de descuento si aplica */}
        {p.precioOriginal && p.precioOriginal > p.precio && (
          <span style={{
            position: "absolute",
            top: 12,
            right: 12,
            background: "#22c55e",
            color: "#fff",
            fontWeight: "bold",
            fontSize: "0.95rem",
            borderRadius: "0.5rem",
            padding: "0.25rem 0.7rem"
          }}>
            {Math.round(((p.precioOriginal - p.precio) / p.precioOriginal) * 100)}% OFF
          </span>
        )}
      </div>
      <div style={{
        padding: "2rem 1.5rem 1.5rem 1.5rem",
        width: "100%",
        textAlign: "center",
        flexGrow: 1,
        display: "flex",
        flexDirection: "column",
        justifyContent: "flex-end"
      }}>
        <h3 style={{
          fontSize: "1.15rem",
          fontWeight: 600,
          marginBottom: "0.5rem",
          color: "#222"
        }}>
          {p.nombre}
        </h3>
        {/* Categoría eliminada */}
        <div style={{ marginBottom: "1.2rem" }}>
          <span style={{
            color: "#6366f1",
            fontWeight: "bold",
            fontSize: "1.25rem"
          }}>
            {new Intl.NumberFormat("es-SV", {
              style: "currency",
              currency: "USD",
            }).format(p.precio)}
          </span>
          {p.precioOriginal && p.precioOriginal > p.precio && (
            <span style={{
              color: "#9ca3af",
              textDecoration: "line-through",
              fontSize: "1rem",
              marginLeft: "0.7rem"
            }}>
              {new Intl.NumberFormat("es-SV", {
                style: "currency",
                currency: "USD",
              }).format(p.precioOriginal)}
            </span>
          )}
        </div>
      </div>
    </div>
  );

  return (
    <div style={{ fontFamily: "'Helvetica Neue', sans-serif", color: "#333", minHeight: "100vh", margin: 0, padding: 0 }}>
      <Header />

      {/* Hero Section */}
      <section
        style={{
          position: "relative",
          background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
          minHeight: "60vh",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          color: "white",
          textAlign: "center",
          overflow: "hidden"
        }}
      >
        {/* Ejemplo de bloque destacado tipo "hero" */}
        <div style={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          justifyContent: "center",
          width: "100%",
          maxWidth: "1200px",
          margin: "0 auto",
          zIndex: 2,
          position: "relative",
          padding: "2rem 1rem"
        }}>
          <div style={{ flex: 1, textAlign: "left" }}>
            <h1 style={{
              fontSize: "2.8rem",
              fontWeight: "bold",
              marginBottom: "1rem",
              lineHeight: 1.1,
              color: 'white'
            }}>
              Productos Premium<br />Entregados a tu Puerta
            </h1>
          </div>
        </div>
        
      </section>

      {/* Productos destacados */}
      <section style={{ padding: "4rem 0" }}>
        <h2 style={{
          fontSize: "2.8rem",
          marginBottom: "3rem",
          color: "#222",
          textAlign: "center"
        }}>
          Novedades de Temporada
        </h2>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
            gap: "2.5rem"
          }}
        >
          {productosDestacados.map(renderCard)}
        </div>
      </section>

      {/* Recomendados */}
      {userId && recomendados.length > 0 && (
        <section style={{ padding: "4rem 0" }}>
          <h2 style={{
            fontSize: "2.8rem",
            marginBottom: "3rem",
            color: "#222",
            textAlign: "center"
          }}>
            Recomendado para ti
          </h2>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
              gap: "2.5rem"
            }}
          >
            {recomendados.map(renderCard)}
          </div>
        </section>
      )}

      <Footer />
    </div>
  );
}
