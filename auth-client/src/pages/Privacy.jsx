import React from 'react';

export default function Privacy() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Política de Privacidad</h1>
        <section className="mb-6">
          <p className="text-gray-700">
            En Tienda Ecommerce protegemos tu información personal y explicamos cómo la recopilamos y usamos. Al utilizar nuestra plataforma, aceptas las prácticas descritas a continuación.
          </p>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">1. Información que Recopilamos</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Datos de contacto: nombre, correo electrónico, dirección y teléfono.</li>
            <li>Información de pago: solo para procesar compras, no almacenamos datos de tarjetas.</li>
            <li>Datos de navegación: cookies y registros de actividad en la web.</li>
          </ul>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">2. Uso de la Información</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Procesar y entregar tus pedidos.</li>
            <li>Mejorar la experiencia de usuario y personalizar el contenido.</li>
            <li>Enviar notificaciones y promociones (puedes darte de baja en cualquier momento).</li>
          </ul>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">3. Protección de Datos</h2>
          <p className="text-gray-700">
            Utilizamos medidas de seguridad técnicas y organizativas para proteger tu información contra accesos no autorizados.
          </p>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">4. Tus Derechos</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Acceder, corregir o eliminar tus datos personales.</li>
            <li>Solicitar información sobre el uso de tus datos.</li>
            <li>Retirar tu consentimiento en cualquier momento.</li>
          </ul>
          <p className="mt-2 text-gray-700">
            Para ejercer tus derechos, contáctanos en{' '}
            <a href="mailto:privacidad@tiendaecommerce.com" className="text-blue-600 hover:underline">
              privacidad@tiendaecommerce.com
            </a>.
          </p>
        </section>
      </main>
    </div>
  );
}