import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function Blog() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Blog</h1>
        <p className="mb-4">
          Entérate de las últimas noticias, consejos y tendencias en ecommerce. Nuestro blog está diseñado para ayudarte a sacar el máximo provecho de tus compras en línea.
        </p>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Categorías Populares</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Guías de compra</li>
            <li>Consejos de seguridad en línea</li>
            <li>Novedades y lanzamientos</li>
            <li>Historias de clientes</li>
            <li>Tecnología y tendencias</li>
          </ul>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2">Artículos Recientes</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>
              <strong>Cómo elegir el producto ideal para ti</strong> <span className="text-gray-500">(Mayo 2024)</span>
            </li>
            <li>
              <strong>5 consejos para comprar seguro en internet</strong> <span className="text-gray-500">(Abril 2024)</span>
            </li>
            <li>
              <strong>Lo último en tecnología para el hogar</strong> <span className="text-gray-500">(Marzo 2024)</span>
            </li>
          </ul>
        </section>
      </main>
      <Footer />
    </>
  );
}