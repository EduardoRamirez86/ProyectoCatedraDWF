import React from 'react';

export default function FAQ() {
  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen flex items-center justify-center p-4">
      <main className="w-full max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Preguntas Frecuentes</h1>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Pedidos y Compras</h2>
          <ul className="list-disc list-inside space-y-2 text-gray-700">
            <li>
              <strong>¿Cómo puedo hacer un pedido?</strong> Para realizar un pedido, selecciona el producto, elige la cantidad y haz clic en "Agregar al carrito". Luego, sigue el proceso de pago.
            </li>
            <li>
              <strong>¿Puedo modificar o cancelar mi pedido?</strong> Puedes modificar o cancelar tu pedido antes de que sea enviado. Contáctanos lo antes posible para ayudarte.
            </li>
            <li>
              <strong>¿Qué métodos de pago aceptan?</strong> Aceptamos tarjetas de crédito, débito y pagos por transferencia bancaria.
            </li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Envíos</h2>
          <ul className="list-disc list-inside space-y-2 text-gray-700">
            <li>
              <strong>¿Cuánto tarda el envío?</strong> El envío estándar tarda de 3 a 5 días hábiles. El envío exprés tarda de 1 a 2 días hábiles.
            </li>
            <li>
              <strong>¿Puedo rastrear mi pedido?</strong> Sí, recibirás un correo con el número de seguimiento una vez que tu pedido sea enviado.
            </li>
            <li>
              <strong>¿Realizan envíos internacionales?</strong> Actualmente solo realizamos envíos dentro de El Salvador.
            </li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Devoluciones y Reembolsos</h2>
          <ul className="list-disc list-inside space-y-2 text-gray-700">
            <li>
              <strong>¿Puedo devolver un producto?</strong> Sí, tienes 30 días para solicitar una devolución si el producto está en condiciones originales.
            </li>
            <li>
              <strong>¿Cómo solicito un reembolso?</strong> Contáctanos con tu número de pedido y motivo de devolución. Procesaremos el reembolso en 5–7 días hábiles.
            </li>
            <li>
              <strong>¿Qué productos no tienen devolución?</strong> Productos en oferta o personalizados no aplican para devoluciones.
            </li>
          </ul>
        </section>
        <section>
          <h2 className="text-xl font-semibold mb-2 text-indigo-700">Soporte y Contacto</h2>
          <ul className="list-disc list-inside space-y-2 text-gray-700">
            <li>
              <strong>¿Cómo puedo contactar al soporte?</strong> Escríbenos a <a href="mailto:soporte@tiendaecommerce.com" className="text-blue-600 hover:underline">soporte@tiendaecommerce.com</a> o llama al <span className="text-gray-800">+503 1234-5678</span>.
            </li>
            <li>
              <strong>¿Tienen atención presencial?</strong> Sí, puedes visitarnos en nuestra tienda física en San Salvador.
            </li>
          </ul>
        </section>
      </main>
    </div>
  );
}