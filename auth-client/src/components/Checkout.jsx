import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { checkoutPedido } from '../services/pedidoService';
import MySwal from '../utils/swal';
import '../style/Checkout.css';

const paymentOptions = [
  { label: 'Tarjeta de Crédito', value: 'TARJETA_CREDITO' },
  { label: 'PayPal', value: 'PAYPAL' },
  { label: 'Efectivo', value: 'EFECTIVO' },
  { label: 'Transferencia Bancaria', value: 'TRANSFERENCIA_BANCARIA' }
];

const fieldConfigs = {
  TARJETA_CREDITO: [
    { 
      name: 'card', 
      label: 'Número de Tarjeta',
      placeholder: '1234 5678 9012 3456', 
      regex: /^\d{4} \d{4} \d{4} \d{4}$/
    },
    { 
      name: 'expiry', 
      label: 'Fecha de Expiración',
      placeholder: 'MM/AA', 
      regex: /^(0[1-9]|1[0-2])\/\d{2}$/
    }
  ],
  PAYPAL: [
    { 
      name: 'paypalAccount', 
      label: 'Correo de PayPal',
      placeholder: 'email@ejemplo.com', 
      regex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    }
  ],
  TRANSFERENCIA_BANCARIA: [
    { 
      name: 'transferencia', 
      label: 'Número de Cuenta',
      placeholder: '00012345678901234567', 
      regex: /^\d{20}$/
    }
  ],
  EFECTIVO: []
};

export default function Checkout() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ 
    nombre: '', 
    tipoPago: '', 
    cuponCodigo: '',
    card: '',
    expiry: '',
    paypalAccount: '',
    transferencia: ''
  });
  
  const [errors, setErrors] = useState({});
  const [hasCupon, setHasCupon] = useState(false);

  const handleChange = e => {
    const { name, value: rawValue } = e.target;
    let value = rawValue;

    switch(name) {
      case 'card':
        value = rawValue.replace(/\D/g, '')
          .substring(0, 16)
          .replace(/(\d{4})(?=\d)/g, '$1 ');
        break;
      
      case 'expiry':
        value = rawValue.replace(/\D/g, '')
          .substring(0, 4)
          .replace(/(\d{2})(\d)/, '$1/$2');
        break;
      
      default:
        value = rawValue;
        break;
    }

    setForm(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validateForm = () => {
    const newErrors = {};
    let isValid = true;

    if (!form.nombre.trim()) {
      newErrors.nombre = 'Nombre completo requerido';
      isValid = false;
    }

    if (!form.tipoPago) {
      newErrors.tipoPago = 'Método de pago requerido';
      isValid = false;
    }

    const currentPaymentFields = fieldConfigs[form.tipoPago] || [];
    currentPaymentFields.forEach(field => {
      if (!field.regex.test(form[field.name])) {
        newErrors[field.name] = `Formato inválido para ${field.label.toLowerCase()}`;
        isValid = false;
      }
    });

    if (hasCupon && !form.cuponCodigo.trim()) {
      newErrors.cuponCodigo = 'Código de cupón requerido';
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleSubmit = async e => {
    e.preventDefault();
    
    if (!validateForm()) return;

    try {
      const idCarrito = parseInt(localStorage.getItem('carritoId'), 10);
      const pedidoData = {
        idCarrito,
        tipoPago: form.tipoPago,
        cuponCodigo: hasCupon ? form.cuponCodigo.trim() : null
      };

      const response = await checkoutPedido(pedidoData);
      
      sessionStorage.setItem('orderNumber', response.idPedido);
      sessionStorage.setItem('orderTotal', response.total);
      
      MySwal.fire({
        icon: 'success',
        title: '¡Compra Exitosa!',
        text: `Pedido #${response.idPedido} procesado correctamente`
      }).then(() => navigate('/confirmation'));
      
    } catch (error) {
      MySwal.fire({
        icon: 'error',
        title: 'Error en la compra',
        text: error.response?.data?.message || 'Error al procesar el pedido'
      });
    }
  };

  const renderPaymentFields = () => {
    if (!form.tipoPago || !fieldConfigs[form.tipoPago]) return null;
    
    return fieldConfigs[form.tipoPago].map(field => (
      <div key={field.name} className="input-group">
        <input
          id={field.name}
          name={field.name}
          value={form[field.name]}
          onChange={handleChange}
          className={`floating-input ${errors[field.name] ? 'error' : ''}`}
          placeholder=" "
        />
        <label htmlFor={field.name} className="floating-label">
          {field.label}
        </label>
        {errors[field.name] && (
          <span className="error-message">{errors[field.name]}</span>
        )}
      </div>
    ));
  };

  return (
    <div className="checkout-container">
      <h1>Finalizar Compra</h1>
      
      <form onSubmit={handleSubmit}>
        {/* Sección de información personal */}
        <div className="form-section">
          <h2>Información Personal</h2>
          <div className="input-group">
            <input
              id="nombre"
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              className={`floating-input ${errors.nombre ? 'error' : ''}`}
              placeholder=" "
            />
            <label htmlFor="nombre" className="floating-label">
              Nombre Completo
            </label>
            {errors.nombre && (
              <span className="error-message">{errors.nombre}</span>
            )}
          </div>
        </div>

        {/* Sección de método de pago */}
        <div className="form-section">
          <h2>Método de Pago</h2>
          <div className="input-group">
            <select
              id="tipoPago"
              name="tipoPago"
              value={form.tipoPago}
              onChange={e => setForm(prev => ({ ...prev, tipoPago: e.target.value }))}
              className={`floating-input ${errors.tipoPago ? 'error' : ''}`}
            >
              <option value=""></option>
              {paymentOptions.map(option => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            <label htmlFor="tipoPago" className="floating-label">
              Método de Pago
            </label>
            {errors.tipoPago && (
              <span className="error-message">{errors.tipoPago}</span>
            )}
          </div>

          {renderPaymentFields()}
        </div>

        {/* Sección de cupones */}
        <div className="form-section">
          <div className="coupon-section">
            <label className="coupon-toggle">
              <input
                type="checkbox"
                checked={hasCupon}
                onChange={() => setHasCupon(!hasCupon)}
              />
              ¿Tiene un cupón de descuento?
            </label>
            
            {hasCupon && (
              <div className="input-group">
                <input
                  id="cuponCodigo"
                  name="cuponCodigo"
                  value={form.cuponCodigo}
                  onChange={handleChange}
                  className={`floating-input ${errors.cuponCodigo ? 'error' : ''}`}
                  placeholder=" "
                />
                <label htmlFor="cuponCodigo" className="floating-label">
                  Código de Cupón
                </label>
                {errors.cuponCodigo && (
                  <span className="error-message">{errors.cuponCodigo}</span>
                )}
              </div>
            )}
          </div>
        </div>

        <button type="submit" className="submit-button">
          Confirmar y Pagar
        </button>
      </form>
    </div>
  );
}