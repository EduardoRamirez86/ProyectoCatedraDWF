import React from 'react';
export default function Careers() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Empleos</h1>
        <p className="mb-4 text-gray-700">
          ¿Te gustaría formar parte de un equipo innovador y apasionado por el ecommerce? En Tienda Ecommerce buscamos personas talentosas y comprometidas para crecer juntos.
        </p>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Vacantes Actuales</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Desarrollador Frontend</li>
            <li>Especialista en Logística</li>
            <li>Ejecutivo de Atención al Cliente</li>
            <li>Analista de Marketing Digital</li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">¿Por qué trabajar con nosotros?</h2>
          <ul className="list-disc list-inside space-y-1 text-gray-700">
            <li>Ambiente de trabajo colaborativo y dinámico</li>
            <li>Oportunidades de crecimiento profesional</li>
            <li>Capacitación continua</li>
            <li>Beneficios competitivos</li>
            <li>Proyectos innovadores</li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Proceso de Selección</h2>
          <ol className="list-decimal list-inside space-y-1 text-gray-700">
            <li>Envía tu CV y carta de presentación a <a href="mailto:empleos@tiendaecommerce.com" className="text-blue-600 hover:underline">empleos@tiendaecommerce.com</a></li>
            <li>Revisión de perfiles y preselección</li>
            <li>Entrevista inicial (virtual o presencial)</li>
            <li>Prueba técnica o de habilidades (según el puesto)</li>
            <li>Entrevista final y oferta</li>
          </ol>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">¿Interesado?</h2>
          <p className="text-gray-700">
            Si quieres unirte a nuestro equipo, envíanos tu información y cuéntanos por qué eres el candidato ideal. ¡Esperamos conocerte pronto!
          </p>
        </section>
      </main>
    </div>
  );
}