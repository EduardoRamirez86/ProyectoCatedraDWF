import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';

export default function ReturnsRefund() {
  return (
    <>
      <Header />
      <main className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-semibold mb-4">Devoluciones y Reembolsos</h1>
        <p className="mb-4">
          Queremos que estés satisfecho con tu compra. Si necesitas devolver un producto, sigue estos pasos:
        </p>
        <ol className="list-decimal list-inside mb-4 space-y-2">
          <li>
            <strong>Solicita la devolución:</strong> Escríbenos a <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-500">soporte@tiendaecommerce.com</a> indicando tu número de pedido y motivo de la devolución.
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
        <ul className="list-disc list-inside mb-4 space-y-2">
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
        <p>
          Si tienes dudas sobre el proceso, contáctanos y con gusto te ayudaremos.
        </p>
      </main>
      <Footer />
    </>
  );
}