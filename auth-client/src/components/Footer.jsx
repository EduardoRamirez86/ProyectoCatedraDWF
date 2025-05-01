// src/components/Footer.jsx
import React from 'react';
import { motion } from 'framer-motion';
import '../style/Footer.css';

export default function Footer() {
  return (
    <motion.footer
      className="footer"
      initial={{ y: 60, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.6, delay: 0.3 }}
    >
      <div className="footer__links">
        <a href="/">Inicio</a>
        <a href="/about">Nosotros</a>
        <a href="/contact">Contacto</a>
      </div>
      <div className="footer__copy">
        &copy; {new Date().getFullYear()} MiApp. Todos los derechos reservados.
      </div>
    </motion.footer>
  );
}
