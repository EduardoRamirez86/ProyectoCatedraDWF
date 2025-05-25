import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function Careers() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Empleos</h1>
        <p className="mb-4">
          ¿Te gustaría formar parte de un equipo innovador y apasionado por el ecommerce? En Tienda Ecommerce buscamos personas talentosas y comprometidas para crecer juntos.
        </p>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Vacantes Actuales</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Desarrollador Frontend</li>
            <li>Especialista en Logística</li>
            <li>Ejecutivo de Atención al Cliente</li>
            <li>Analista de Marketing Digital</li>
          </ul>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">¿Por qué trabajar con nosotros?</h2>
          <ul className="list-disc list-inside space-y-1">
            <li>Ambiente de trabajo colaborativo y dinámico</li>
            <li>Oportunidades de crecimiento profesional</li>
            <li>Capacitación continua</li>
            <li>Beneficios competitivos</li>
            <li>Proyectos innovadores</li>
          </ul>
        </section>
        <section className="mb-6">
          <h2 className="text-xl font-semibold mb-2">Proceso de Selección</h2>
          <ol className="list-decimal list-inside space-y-1">
            <li>Envía tu CV y carta de presentación a <a href="mailto:empleos@tiendaecommerce.com" className="text-blue-500">empleos@tiendaecommerce.com</a></li>
            <li>Revisión de perfiles y preselección</li>
            <li>Entrevista inicial (virtual o presencial)</li>
            <li>Prueba técnica o de habilidades (según el puesto)</li>
            <li>Entrevista final y oferta</li>
          </ol>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2">¿Interesado?</h2>
          <p>
            Si quieres unirte a nuestro equipo, envíanos tu información y cuéntanos por qué eres el candidato ideal. ¡Esperamos conocerte pronto!
          </p>
        </section>
      </main>
      <Footer />
    </>
  );
}