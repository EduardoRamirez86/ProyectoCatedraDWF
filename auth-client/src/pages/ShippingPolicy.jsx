import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function ShippingPolicy() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Política de Envíos</h1>
        <p className="mb-4">
          En Tienda Ecommerce nos comprometemos a entregar tus productos de manera rápida y segura. Consulta los detalles de nuestra política de envíos:
        </p>
        <ul className="list-disc list-inside mb-4 space-y-2">
          <li>
            <strong>Opciones de envío:</strong> Ofrecemos envío estándar (3–5 días hábiles) y exprés (1–2 días hábiles) dentro de El Salvador.
          </li>
          <li>
            <strong>Zonas de cobertura:</strong> Realizamos entregas en todo el territorio nacional. Algunas zonas rurales pueden requerir tiempo adicional.
          </li>
          <li>
            <strong>Costos:</strong> El costo de envío varía según el peso, tamaño del paquete y destino. El monto se calcula automáticamente al finalizar tu compra.
          </li>
          <li>
            <strong>Seguimiento:</strong> Recibirás un número de seguimiento por correo electrónico una vez que tu pedido sea despachado.
          </li>
          <li>
            <strong>Restricciones:</strong> No realizamos envíos a apartados postales ni fuera de El Salvador.
          </li>
        </ul>
        <p className="mb-2">
          <strong>Recomendaciones:</strong> Verifica que la dirección de entrega esté completa y correcta para evitar retrasos. Si tienes dudas sobre tu envío, contáctanos a <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-500">soporte@tiendaecommerce.com</a>.
        </p>
        <p>
          Nos esforzamos por cumplir los plazos de entrega, pero pueden presentarse demoras por causas ajenas a nuestra empresa (clima, tráfico, etc.).
        </p>
      </main>
      <Footer />
    </>
  );
}