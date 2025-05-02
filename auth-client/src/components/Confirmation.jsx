// src/components/Confirmation.jsx
import React from 'react';
import '../style/userPage.css';

export default function Confirmation() {
  const orderNumber = sessionStorage.getItem('orderNumber');
  const orderTotal = sessionStorage.getItem('orderTotal');

  return (
    <div className="confirmation">
      <h2>¡Compra Exitosa!</h2>
      <p>Número de Pedido: {orderNumber}</p>
      <p>Total: ${orderTotal}</p>
    </div>
  );
}
