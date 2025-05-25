// src/components/Footer.jsx
import React from 'react';
import { motion } from 'framer-motion';

export default function Footer() {
  return (
    <motion.footer
      initial={{ y: 60, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.6, delay: 0.3 }}
      style={{
        background: "#232b39",
        color: "#fff",
        paddingTop: "3rem",
        paddingBottom: "1.5rem"
      }}
    >
      <div
        style={{
          maxWidth: "1200px",
          margin: "0 auto",
          paddingLeft: "1rem",
          paddingRight: "1rem"
        }}
      >
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(1, minmax(0, 1fr))",
            gap: "2rem",
            marginBottom: "2rem"
          }}
        >
          <style>
            {`
              @media (min-width: 768px) {
                .footer-grid {
                  grid-template-columns: repeat(4, minmax(0, 1fr)) !important;
                }
              }
              .footer-link:hover { color: #fff !important; text-decoration: underline; }
              .footer-title { font-size: 1.125rem; font-weight: 600; margin-bottom: 1rem; }
              .footer-list { list-style: none; padding: 0; margin: 0; }
              .footer-list li { margin-bottom: 0.5rem; }
              .footer-social a { color: #a0aec0; margin-right: 1rem; font-size: 1.1rem; transition: color 0.2s; }
              .footer-social a:last-child { margin-right: 0; }
              .footer-social a:hover { color: #fff; }
              .footer-copy { color: #a0aec0; margin-bottom: 0; }
              .footer-divider { border-top: 1px solid #374151; margin-top: 2rem; margin-bottom: 1.5rem; }
            `}
          </style>
          <div className="footer-grid" style={{
            display: "grid",
            gridTemplateColumns: "repeat(1, minmax(0, 1fr))",
            gap: "2rem"
          }}>
            <div>
              <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "1rem" }}>
                <i className="fas fa-bolt" style={{ color: "#818cf8", fontSize: "2rem" }}></i>
                <h3 style={{ fontSize: "1.25rem", fontWeight: "bold" }}>
                  Tienda<span style={{ color: "#818cf8" }}>Ecommerce</span>
                </h3>
              </div>
              <p style={{ color: "#a0aec0", marginBottom: "1rem" }}>
                Plataforma de comercio electrónico premium que ofrece los mejores productos a precios competitivos.
              </p>
              <div className="footer-social" style={{ display: "flex", gap: "1rem" }}>
                <a href="#" aria-label="Facebook"><i className="fab fa-facebook-f"></i></a>
                <a href="#" aria-label="Twitter"><i className="fab fa-twitter"></i></a>
                <a href="#" aria-label="Instagram"><i className="fab fa-instagram"></i></a>
                <a href="#" aria-label="Pinterest"><i className="fab fa-pinterest"></i></a>
              </div>
            </div>
            <div>
              <h4 className="footer-title">Tienda</h4>
              <ul className="footer-list">
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Todos los productos</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Destacados</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Novedades</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>En oferta</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Tarjetas de regalo</a></li>
              </ul>
            </div>
            <div>
              <h4 className="footer-title">Atención al Cliente</h4>
              <ul className="footer-list">
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Contáctanos</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Preguntas frecuentes</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Política de envíos</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Devoluciones y reembolsos</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Rastrear pedido</a></li>
              </ul>
            </div>
            <div>
              <h4 className="footer-title">Acerca de</h4>
              <ul className="footer-list">
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Nuestra historia</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Empleos</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Términos y condiciones</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Política de privacidad</a></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Blog</a></li>
              </ul>
            </div>
          </div>
        </div>
        <div className="footer-divider"></div>
        <div style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "flex-start",
          justifyContent: "space-between"
        }}>
          <p className="footer-copy">
            &copy; {new Date().getFullYear()} Tienda Ecommerce. Todos los derechos reservados.
          </p>
        </div>
      </div>
    </motion.footer>
  );
}
