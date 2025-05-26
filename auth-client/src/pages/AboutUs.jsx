import React from 'react';

export default function AboutUs() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Nuestra Historia</h1>
        <p className="mb-4 text-gray-700">
          Bienvenido a Tienda Ecommerce, fundada en 2022 con la misión de ofrecer productos de calidad y la mejor experiencia de compra en línea. Desde nuestros inicios, hemos crecido gracias a la confianza de miles de clientes satisfechos en El Salvador.
        </p>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Misión</h2>
          <p className="text-gray-700">
            Brindar a nuestros clientes una plataforma segura, fácil de usar y con una amplia variedad de productos, garantizando siempre la mejor atención y satisfacción.
          </p>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Visión</h2>
          <p className="text-gray-700">
            Ser la tienda en línea líder en El Salvador y Centroamérica, reconocida por la innovación, calidad y excelencia en el servicio.
          </p>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Nuestros Valores</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li><strong>Compromiso:</strong> Nos esforzamos por superar las expectativas de nuestros clientes.</li>
            <li><strong>Innovación:</strong> Buscamos constantemente nuevas formas de mejorar la experiencia de compra.</li>
            <li><strong>Transparencia:</strong> Operamos con honestidad y claridad en cada proceso.</li>
            <li><strong>Calidad:</strong> Seleccionamos cuidadosamente cada producto para asegurar su excelencia.</li>
            <li><strong>Trabajo en equipo:</strong> Creemos en el poder de la colaboración para alcanzar grandes metas.</li>
          </ul>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Nuestro Equipo</h2>
          <p className="text-gray-700">
            Contamos con un equipo multidisciplinario de profesionales apasionados por el ecommerce, la tecnología y la atención al cliente. Juntos, trabajamos para ofrecerte la mejor experiencia posible.
          </p>
        </section>
      </main>
    </div>
  );
}