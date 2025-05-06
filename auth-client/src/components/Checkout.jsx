import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { checkoutPedido } from '../services/pedidoService';
import MySwal from '../utils/swal';
import '../style/userPage.css';

const paymentOptions = [
  { label: 'Tarjeta de Crédito', value: 'TARJETA_CREDITO' },
  { label: 'PayPal', value: 'PAYPAL' },
  { label: 'Efectivo', value: 'EFECTIVO' },
  { label: 'Transferencia Bancaria', value: 'TRANSFERENCIA_BANCARIA' }
];

const fieldConfigs = {
  TARJETA_CREDITO: [
    { name: 'card', placeholder: '1234-5678-9012-3456', regex: /^\d{4}-\d{4}-\d{4}-\d{4}$/ },
    { name: 'expiry', placeholder: 'MM/AA', regex: /^(0[1-9]|1[0-2])\/\d{2}$/ }
  ],
  PAYPAL: [
    { name: 'paypalAccount', placeholder: 'email@ejemplo.com', regex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ }
  ],
  TRANSFERENCIA_BANCARIA: [
    { name: 'transferencia', placeholder: '00012345678901234567', regex: /^\d{20}$/ }
  ],
  EFECTIVO: []
};

export default function Checkout() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ nombre: '', tipoPago: '', cuponCodigo: '' });
  const [errors, setErrors] = useState({});

  const handleChange = e => {
    const { name, value: raw } = e.target;
    let value = raw;

    if (name === 'card') value = raw.replace(/\D/g, '').substring(0, 16).replace(/(\d{4})(?=\d)/g, '$1-');
    if (name === 'expiry') value = raw.replace(/\D/g, '').replace(/(\d{2})(\d)/, '$1/$2').substring(0, 5);

    setForm(prev => ({ ...prev, [name]: value }));

    const cfgs = fieldConfigs[form.tipoPago] || [];
    const cfg = cfgs.find(f => f.name === name);
    if (cfg) setErrors(prev => ({ ...prev, [name]: !cfg.regex.test(value) }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!form.nombre || !form.tipoPago) {
      MySwal.fire('Error', 'Ingresa nombre y forma de pago', 'error');
      return;
    }
    const cfgs = fieldConfigs[form.tipoPago] || [];
    for (let cfg of cfgs) {
      if (!cfg.regex.test(form[cfg.name] || '')) {
        MySwal.fire('Error', `Revisa el campo ${cfg.name}`, 'error');
        return;
      }
    }
    const idCarrito = parseInt(localStorage.getItem('carritoId'), 10);
    try {
      const resp = await checkoutPedido({
        idCarrito,
        tipoPago: form.tipoPago,
        cuponCodigo: form.cuponCodigo || null
      });
      sessionStorage.setItem('orderNumber', resp.idPedido);
      sessionStorage.setItem('orderTotal', resp.total);
      MySwal.fire('¡Éxito!', 'Tu pedido ha sido procesado', 'success')
        .then(() => navigate('/confirmation'));
    } catch (error) {
      MySwal.fire('Error', `No se pudo procesar el pedido: ${error.message}`, 'error');
    }
  };

  const renderFields = () => {
    if (!form.tipoPago) return null;
    return fieldConfigs[form.tipoPago].map(cfg => (
      <div key={cfg.name} className={`form-group ${errors[cfg.name] ? 'error' : ''}`}>
        <input
          name={cfg.name}
          value={form[cfg.name] || ''}
          onChange={handleChange}
          placeholder={cfg.placeholder}
          required
        />
        {errors[cfg.name] && <span className="error-message">Formato inválido</span>}
      </div>
    ));
  };

  return (
    <form className="checkout-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <input
          name="nombre"
          value={form.nombre}
          onChange={handleChange}
          placeholder="Nombre Completo"
          required
        />
      </div>

      <div className="form-group">
        <select
          name="tipoPago"
          value={form.tipoPago}
          onChange={e => {
            setForm({ nombre: form.nombre, tipoPago: e.target.value, cuponCodigo: form.cuponCodigo });
            setErrors({});
          }}
          required
        >
          <option value="">Selecciona forma de pago</option>
          {paymentOptions.map(opt => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>

      {renderFields()}

      <div className="form-group">
        <input
          name="cuponCodigo"
          value={form.cuponCodigo}
          onChange={handleChange}
          placeholder="Código de Cupón"
        />
      </div>

      <button type="submit">Confirmar Compra</button>
    </form>
  );
}