import React from 'react';

export default function Blog() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Blog</h1>
        <p className="mb-4 text-gray-700">
          Entérate de las últimas noticias, consejos y tendencias en ecommerce. Nuestro blog está diseñado para ayudarte a sacar el máximo provecho de tus compras en línea.
        </p>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Categorías Populares</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Guías de compra</li>
            <li>Consejos de seguridad en línea</li>
            <li>Novedades y lanzamientos</li>
            <li>Historias de clientes</li>
            <li>Tecnología y tendencias</li>
          </ul>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Artículos Recientes</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
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
    </div>
  );
}