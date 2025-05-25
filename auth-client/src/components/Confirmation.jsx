// Confirmation.jsx
import React, { useEffect, useState } from 'react';
import { getPedidoById } from '../services/pedidoService';

export default function Confirmation() {
  const [pedido, setPedido] = useState(null);
  useEffect(() => {
    const id = sessionStorage.getItem('orderNumber');
    getPedidoById(id).then(setPedido);
  }, []);

  if (!pedido) return <p>Cargando...</p>;
  return (
    <div>
      <h1>¡Gracias por tu compra! Pedido #{pedido.idPedido}</h1>
      <h2>Se enviará a:</h2>
      <p><strong>{pedido.direccion.alias}</strong></p>
      <p>{pedido.direccion.calle}, {pedido.direccion.ciudad}</p>
      <p>{pedido.direccion.departamento}</p>
      {/* Si quieres, puedes mostrar un mapita con lat/lng */}
    </div>
  );
}

