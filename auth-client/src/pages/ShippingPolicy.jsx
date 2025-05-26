import React from 'react';

export default function ShippingPolicy() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Política de Envíos</h1>
        <p className="mb-4 text-gray-700">
          En Tienda Ecommerce nos comprometemos a entregar tus productos de manera rápida y segura. Consulta los detalles de nuestra política de envíos:
        </p>
        <ul className="list-disc list-inside mb-6 space-y-2 text-gray-700">
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
        <p className="mb-2 text-gray-700">
          <strong>Recomendaciones:</strong> Verifica que la dirección de entrega esté completa y correcta para evitar retrasos. Si tienes dudas sobre tu envío, contáctanos a{' '}
          <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-600 hover:underline">soporte@tiendaecommerce.com</a>.
        </p>
        <p className="text-gray-700">
          Nos esforzamos por cumplir los plazos de entrega, pero pueden presentarse demoras por causas ajenas a nuestra empresa (clima, tráfico, etc.).
        </p>
      </main>
    </div>
  );
}