// src/components/Footer.jsx
import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';

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
      <div style={{ maxWidth: "1200px", margin: "0 auto", padding: "0 1rem" }}>
        <div style={{ display: "grid", gap: "2rem", marginBottom: "2rem" }}>
          <style>{`
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
            .footer-copy { color: #a0aec0; margin: 0; }
            .footer-divider { border-top: 1px solid #374151; margin: 2rem 0 1.5rem; }
          `}</style>

          <div
            className="footer-grid"
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(1, minmax(0, 1fr))",
              gap: "2rem"
            }}
          >
            {/* Logo + descripción */}
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

            {/* Tienda */}
            <div>
              <h4 className="footer-title">Tienda</h4>
              <ul className="footer-list">
                <li><Link to="/products" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Todos los productos</Link></li>
                <li><Link to="/featured" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Destacados</Link></li>
                <li><Link to="/new-arrivals" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Novedades</Link></li>
                <li><Link to="/sale" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>En oferta</Link></li>
                <li><Link to="/gift-cards" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Tarjetas de regalo</Link></li>
              </ul>
            </div>

            {/* Atención al Cliente */}
            <div>
              <h4 className="footer-title">Atención al Cliente</h4>
              <ul className="footer-list">
                <li><Link to="/contact" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Contáctanos</Link></li>
                <li><Link to="/faq" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Preguntas frecuentes</Link></li>
                <li><Link to="/shipping-policy" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Política de envíos</Link></li>
                <li><Link to="/returns" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Devoluciones y reembolsos</Link></li>
                <li><a href="#" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Rastrear pedido</a></li>
              </ul>
            </div>

            {/* Acerca de */}
            <div>
              <h4 className="footer-title">Acerca de</h4>
              <ul className="footer-list">
                <li><Link to="/about" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Nuestra historia</Link></li>
                <li><Link to="/careers" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Empleos</Link></li>
                <li><Link to="/terms" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Términos y condiciones</Link></li>
                <li><Link to="/privacy" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Política de privacidad</Link></li>
                <li><Link to="/blog" className="footer-link" style={{ color: "#a0aec0", textDecoration: "none" }}>Blog</Link></li>
              </ul>
            </div>
          </div>

        </div>

        <div className="footer-divider"></div>

        <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start" }}>
          <p className="footer-copy">
            &copy; {new Date().getFullYear()} Tienda Ecommerce. Todos los derechos reservados.
          </p>
        </div>
      </div>
    </motion.footer>
  );
}
