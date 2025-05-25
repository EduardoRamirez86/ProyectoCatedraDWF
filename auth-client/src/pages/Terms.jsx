import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function Terms() {
  return (
    <>
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Términos y Condiciones</h1>
        <section className="mb-4">
          <p>
            Lee nuestros términos y condiciones para conocer tus derechos y obligaciones al usar nuestra plataforma. Al acceder y utilizar Tienda Ecommerce, aceptas cumplir con las siguientes disposiciones:
          </p>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">1. Uso de la Plataforma</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Debes ser mayor de edad para realizar compras.</li>
            <li>La información proporcionada debe ser verídica y actualizada.</li>
            <li>No está permitido el uso indebido de la plataforma ni actividades fraudulentas.</li>
          </ul>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">2. Propiedad Intelectual</h2>
          <p>
            Todos los contenidos, marcas y diseños son propiedad de Tienda Ecommerce o sus proveedores y están protegidos por la ley.
          </p>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">3. Compras y Pagos</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Los precios pueden cambiar sin previo aviso.</li>
            <li>Las compras están sujetas a disponibilidad de stock.</li>
            <li>El pago debe realizarse a través de los métodos autorizados.</li>
          </ul>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">4. Limitación de Responsabilidad</h2>
          <p>
            Tienda Ecommerce no se responsabiliza por daños indirectos, incidentales o consecuentes derivados del uso de la plataforma.
          </p>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2">5. Modificaciones</h2>
          <p>
            Nos reservamos el derecho de modificar estos términos en cualquier momento. Te recomendamos revisarlos periódicamente.
          </p>
        </section>
      </main>
    </>
  );
}