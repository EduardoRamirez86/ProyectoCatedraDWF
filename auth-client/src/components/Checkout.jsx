// src/components/Checkout.jsx
import React, { useState } from 'react';
import { checkoutPedido } from '../services/pedidoService';
import MySwal from '../utils/swal';
import '../style/userPage.css';

export default function Checkout() {
  const [form, setForm] = useState({ name: '', dui: '', card: '', expiry: '', email: '' });
  const [errors, setErrors] = useState({});

  const regexes = {
    dui: /^\d{8}-\d{1}$/,  card: /^\d{4}-\d{4}-\d{4}-\d{4}$/,  expiry: /^(0[1-9]|1[0-2])\/\d{2}$/,  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  };

  const handleChange = e => {
    let { name, value } = e.target;
    // formatting
    if (name === 'dui') value = value.replace(/\D/g, '').replace(/(\d{8})(\d)/, '$1-$2');
    if (name === 'card') {
      value = value.replace(/\D/g, '').substring(0,16)
        .replace(/(\d{4})(?=\d)/g, '$1-');
    }
    if (name === 'expiry') value = value.replace(/\D/g, '').replace(/(\d{2})(\d)/, '$1/$2').substring(0,5);
    setForm({ ...form, [name]: value });
    setErrors({ ...errors, [name]: !regexes[name]?.test(value) });
  };

  const handleSubmit = async e => {
    e.preventDefault();
    const invalid = Object.keys(regexes).filter(k => !regexes[k].test(form[k]));
    if (invalid.length) {
      MySwal.fire('Error','Corrige los campos marcados','error');
      return;
    }
    const idCarrito = parseInt(localStorage.getItem('carritoId'),10);
    try {
      const resp = await checkoutPedido({ idCarrito, idFormaPago:1 });
      sessionStorage.setItem('orderNumber', resp.idPedido);
      sessionStorage.setItem('orderTotal', resp.total);
      setForm({ name:'', dui:'', card:'', expiry:'', email:'' });
      setErrors({});
    } catch {
      MySwal.fire('Error','No se pudo procesar el pedido','error');
    }
  };

  return (
    <form className="checkout-form" onSubmit={handleSubmit}>
      {['name','dui','card','expiry','email'].map(field => (
        <div key={field} className={`form-group ${errors[field] ? 'error' : ''}`}>          
          <label htmlFor={field}>{field.toUpperCase()}</label>
          <input id={field} name={field} value={form[field]} onChange={handleChange} required />
          {errors[field] && <span className="error-message">Valor inválido</span>}
        </div>
      ))}
      <button type="submit">Confirmar Compra</button>
    </form>
  );
}
