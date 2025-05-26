import React from 'react';

export default function ReturnsRefund() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Devoluciones y Reembolsos</h1>
        <p className="mb-4 text-gray-700">
          Queremos que estés satisfecho con tu compra. Si necesitas devolver un producto, sigue estos pasos:
        </p>
        <ol className="list-decimal list-inside mb-6 space-y-2 text-gray-700">
          <li>
            <strong>Solicita la devolución:</strong> Escríbenos a{' '}
            <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-600 hover:underline">
              soporte@tiendaecommerce.com
            </a>{' '}
            indicando tu número de pedido y motivo de la devolución.
          </li>
          <li>
            <strong>Prepara el producto:</strong> El producto debe estar sin uso, en su empaque original y con todos los accesorios.
          </li>
          <li>
            <strong>Envío de devolución:</strong> Te indicaremos la dirección a la que debes enviar el producto. Los costos de envío por devolución corren por cuenta del cliente, salvo productos defectuosos.
          </li>
          <li>
            <strong>Procesamiento del reembolso:</strong> Una vez recibido y revisado el producto, procesaremos el reembolso en un plazo de 5–7 días hábiles.
          </li>
        </ol>
        <ul className="list-disc list-inside mb-6 space-y-2 text-gray-700">
          <li>
            <strong>Plazo para devoluciones:</strong> 30 días desde la fecha de compra.
          </li>
          <li>
            <strong>Excepciones:</strong> No se aceptan devoluciones de productos en oferta, personalizados o usados.
          </li>
          <li>
            <strong>Reembolsos parciales:</strong> Si el producto presenta daños por mal uso, el reembolso puede ser parcial o rechazado.
          </li>
        </ul>
        <p className="text-gray-700">
          Si tienes dudas sobre el proceso, contáctanos y con gusto te ayudaremos.
        </p>
      </main>
    </div>
  );
}