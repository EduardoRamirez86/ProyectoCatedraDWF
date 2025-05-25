import React from 'react';

export default function ContactUs() {
  return (
    <>

      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Contáctanos</h1>
        <p className="mb-4">
          ¿Tienes preguntas, sugerencias o necesitas ayuda? Nuestro equipo está listo para ayudarte. Puedes comunicarte con nosotros a través de los siguientes medios:
        </p>
        <ul className="mb-4 list-disc list-inside">
          <li>
            <strong>Email:</strong>{' '}
            <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-500">
              soporte@tiendaecommerce.com
            </a>
          </li>
          <li>
            <strong>Teléfono:</strong> +503 1234-5678
          </li>
          <li>
            <strong>Dirección:</strong> Calle Principal #123, San Salvador, El Salvador
          </li>
          <li>
            <strong>Horario de atención:</strong> Lunes a Viernes, 8:00 a.m. - 6:00 p.m.
          </li>
        </ul>
        <div className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Formulario de Contacto</h2>
          <form className="space-y-3 max-w-md">
            <div>
              <label className="block mb-1 font-medium">Nombre</label>
              <input type="text" className="w-full border rounded px-2 py-1" placeholder="Tu nombre" />
            </div>
            <div>
              <label className="block mb-1 font-medium">Correo electrónico</label>
              <input type="email" className="w-full border rounded px-2 py-1" placeholder="tu@email.com" />
            </div>
            <div>
              <label className="block mb-1 font-medium">Mensaje</label>
              <textarea className="w-full border rounded px-2 py-1" rows={3} placeholder="Escribe tu mensaje aquí..." />
            </div>
            <button type="button" className="bg-blue-500 text-white px-4 py-2 rounded cursor-not-allowed opacity-60" disabled>
              Enviar (próximamente)
            </button>
          </form>
        </div>
        <p>
          También puedes encontrarnos en nuestras redes sociales para recibir atención personalizada y enterarte de nuestras novedades.
        </p>
      </main>

    </>
  );
}
