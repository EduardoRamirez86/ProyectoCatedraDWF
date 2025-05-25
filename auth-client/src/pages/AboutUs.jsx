import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function AboutUs() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Nuestra Historia</h1>
        <p className="mb-4">
          Bienvenido a Tienda Ecommerce, fundada en 2022 con la misión de ofrecer productos de calidad y la mejor experiencia de compra en línea. Desde nuestros inicios, hemos crecido gracias a la confianza de miles de clientes satisfechos en El Salvador.
        </p>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Misión</h2>
          <p>
            Brindar a nuestros clientes una plataforma segura, fácil de usar y con una amplia variedad de productos, garantizando siempre la mejor atención y satisfacción.
          </p>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Visión</h2>
          <p>
            Ser la tienda en línea líder en El Salvador y Centroamérica, reconocida por la innovación, calidad y excelencia en el servicio.
          </p>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Nuestros Valores</h2>
          <ul className="list-disc list-inside space-y-1">
            <li><strong>Compromiso:</strong> Nos esforzamos por superar las expectativas de nuestros clientes.</li>
            <li><strong>Innovación:</strong> Buscamos constantemente nuevas formas de mejorar la experiencia de compra.</li>
            <li><strong>Transparencia:</strong> Operamos con honestidad y claridad en cada proceso.</li>
            <li><strong>Calidad:</strong> Seleccionamos cuidadosamente cada producto para asegurar su excelencia.</li>
            <li><strong>Trabajo en equipo:</strong> Creemos en el poder de la colaboración para alcanzar grandes metas.</li>
          </ul>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2">Nuestro Equipo</h2>
          <p>
            Contamos con un equipo multidisciplinario de profesionales apasionados por el ecommerce, la tecnología y la atención al cliente. Juntos, trabajamos para ofrecerte la mejor experiencia posible.
          </p>
        </section>
      </main>
      <Footer />
    </>
  );
}