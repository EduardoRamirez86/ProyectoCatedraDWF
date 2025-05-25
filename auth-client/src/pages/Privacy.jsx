import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function Privacy() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Política de Privacidad</h1>
        <section className="mb-4">
          <p>
            En Tienda Ecommerce protegemos tu información personal y explicamos cómo la recopilamos y usamos. Al utilizar nuestra plataforma, aceptas las prácticas descritas a continuación.
          </p>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">1. Información que Recopilamos</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Datos de contacto: nombre, correo electrónico, dirección y teléfono.</li>
            <li>Información de pago: solo para procesar compras, no almacenamos datos de tarjetas.</li>
            <li>Datos de navegación: cookies y registros de actividad en la web.</li>
          </ul>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">2. Uso de la Información</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Procesar y entregar tus pedidos.</li>
            <li>Mejorar la experiencia de usuario y personalizar el contenido.</li>
            <li>Enviar notificaciones y promociones (puedes darte de baja en cualquier momento).</li>
          </ul>
        </section>
        <section className="mb-4">
          <h2 className="text-xl font-semibold mb-2">3. Protección de Datos</h2>
          <p>
            Utilizamos medidas de seguridad técnicas y organizativas para proteger tu información contra accesos no autorizados.
          </p>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2">4. Tus Derechos</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Acceder, corregir o eliminar tus datos personales.</li>
            <li>Solicitar información sobre el uso de tus datos.</li>
            <li>Retirar tu consentimiento en cualquier momento.</li>
          </ul>
          <p className="mt-2">
            Para ejercer tus derechos, contáctanos en <a href="mailto:privacidad@tiendaecommerce.com" className="text-blue-500">privacidad@tiendaecommerce.com</a>.
          </p>
        </section>
      </main>
      <Footer />
    </>
  );
}