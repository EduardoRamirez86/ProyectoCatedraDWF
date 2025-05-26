import React from 'react';

export default function ContactUs() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-2xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-4 text-indigo-700">Contáctanos</h1>
        <p className="mb-4 text-gray-700">
          ¿Tienes preguntas, sugerencias o necesitas ayuda? Nuestro equipo está listo para ayudarte. Puedes comunicarte con nosotros a través de los siguientes medios:
        </p>
        <ul className="mb-6 list-disc list-inside text-gray-700">
          <li>
            <strong>Email:</strong>{' '}
            <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-600 hover:underline">
              soporte@tiendaecommerce.com
            </a>
          </li>
          <li>
            <strong>Teléfono:</strong> <span className="text-gray-800">+503 1234-5678</span>
          </li>
          <li>
            <strong>Dirección:</strong> <span className="text-gray-800">Calle Principal #123, San Salvador, El Salvador</span>
          </li>
          <li>
            <strong>Horario de atención:</strong> <span className="text-gray-800">Lunes a Viernes, 8:00 a.m. - 6:00 p.m.</span>
          </li>
        </ul>
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Formulario de Contacto</h2>
          <form className="space-y-4 max-w-md mx-auto bg-indigo-50 rounded-lg p-6 shadow">
            <div>
              <label className="block mb-1 font-medium text-gray-700">Nombre</label>
              <input
                type="text"
                className="w-full border border-indigo-200 rounded px-3 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
                placeholder="Tu nombre"
                disabled
              />
            </div>
            <div>
              <label className="block mb-1 font-medium text-gray-700">Correo electrónico</label>
              <input
                type="email"
                className="w-full border border-indigo-200 rounded px-3 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
                placeholder="tu@email.com"
                disabled
              />
            </div>
            <div>
              <label className="block mb-1 font-medium text-gray-700">Mensaje</label>
              <textarea
                className="w-full border border-indigo-200 rounded px-3 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
                rows={3}
                placeholder="Escribe tu mensaje aquí..."
                disabled
              />
            </div>
            <button
              type="button"
              className="bg-blue-500 text-white px-4 py-2 rounded font-semibold opacity-60 cursor-not-allowed w-full"
              disabled
            >
              Enviar (próximamente)
            </button>
          </form>
        </div>
        <p className="text-gray-700">
          También puedes encontrarnos en nuestras redes sociales para recibir atención personalizada y enterarte de nuestras novedades.
        </p>
      </main>
    </div>
  );
}
