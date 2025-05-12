// src/components/Checkout.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { checkoutPedido } from '../services/pedidoService';
import { getByUserDirecciones, saveDireccion } from '../services/direccionService';
import MySwal from '../utils/swal';
import { secureGetItem } from '../utils/secureStorage';
import AddressPicker from './AddressPicker';
import '../style/Checkout.css';

const paymentOptions = [
  { label: 'Tarjeta de Crédito',         value: 'TARJETA_CREDITO' },
  { label: 'PayPal',                     value: 'PAYPAL' },
  { label: 'Efectivo',                   value: 'EFECTIVO' },
  { label: 'Transferencia Bancaria',     value: 'TRANSFERENCIA_BANCARIA' }
];

const fieldConfigs = {
  TARJETA_CREDITO: [
    { name: 'card',         label: 'Número de Tarjeta',   regex: /^\d{4} \d{4} \d{4} \d{4}$/ },
    { name: 'expiry',       label: 'Fecha de Expiración', regex: /^(0[1-9]|1[0-2])\/\d{2}$/ }
  ],
  PAYPAL: [
    { name: 'paypalAccount', label: 'Correo de PayPal',   regex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ }
  ],
  TRANSFERENCIA_BANCARIA: [
    { name: 'transferencia', label: 'Número de Cuenta',   regex: /^\d{20}$/ }
  ],
  EFECTIVO: []
};

export default function Checkout() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    nombre: '', tipoPago: '', cuponCodigo: '',
    direccionId: '', alias: '', calle: '', ciudad: '', departamento: '',
    latitud: null, longitud: null,
    card: '', expiry: '', paypalAccount: '', transferencia: ''
  });
  const [errors, setErrors] = useState({});
  const [hasCupon, setHasCupon] = useState(false);
  const [loading, setLoading] = useState(false);
  const [direcciones, setDirecciones] = useState([]);
  const [addingNew, setAddingNew] = useState(false);

  useEffect(() => {
    const uid = parseInt(secureGetItem('userId'), 10);
    if (uid) {
      getByUserDirecciones(uid).then(setDirecciones).catch(console.error);
    }
  }, []);

  const handleChange = e => {
    let { name, value } = e.target;
    if (name === 'card') {
      value = value.replace(/\D/g, '').slice(0,16).replace(/(\d{4})(?=\d)/g,'$1 ');
    }
    if (name === 'expiry') {
      value = value.replace(/\D/g,'').slice(0,4).replace(/(\d{2})(\d)/,'$1/$2');
    }
    setForm(f => ({ ...f, [name]: value }));
    setErrors(e => ({ ...e, [name]: '' }));
  };

  const validateForm = () => {
    const err = {};
    let ok = true;
    if (!form.nombre.trim())            { err.nombre = 'Requerido'; ok = false; }
    if (!form.tipoPago)                 { err.tipoPago = 'Requerido'; ok = false; }
    if (!addingNew && !form.direccionId){ err.direccionId = 'Seleccione o cree'; ok = false; }
    if (addingNew && (!form.latitud || !form.longitud)) {
      err.latitud = 'Elija ubicación'; ok = false;
    }
    ;(fieldConfigs[form.tipoPago]||[]).forEach(fld => {
      if (!fld.regex.test(form[fld.name]||'')) {
        err[fld.name] = `Formato inválido`; ok = false;
      }
    });
    if (hasCupon && !form.cuponCodigo.trim()) {
      err.cuponCodigo = 'Requerido'; ok = false;
    }
    setErrors(err);
    return ok;
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!validateForm()) return;
    setLoading(true);
    try {
      const idCarrito = parseInt(secureGetItem('carritoId'), 10);
      if (!idCarrito) throw new Error('Carrito no disponible');

      // Si crea nueva dirección, primero guardarla
      let dirId = form.direccionId;
      if (addingNew) {
        const uid = parseInt(secureGetItem('userId'), 10);
        const nueva = {
          alias: form.alias,
          calle: form.calle,
          ciudad: form.ciudad,
          departamento: form.departamento,
          latitud: form.latitud,
          longitud: form.longitud
        };
        const respDir = await saveDireccion(nueva, uid);
        dirId = respDir.idDireccion;
      }

      const payload = {
        idCarrito,
        tipoPago: form.tipoPago,
        cuponCodigo: hasCupon ? form.cuponCodigo.trim() : null,
        direccionId: dirId
      };
      const resp = await checkoutPedido(payload);
      sessionStorage.setItem('orderNumber', resp.idPedido);
      sessionStorage.setItem('orderTotal', resp.total);
      await MySwal.fire('¡Compra Exitosa!', `Pedido #${resp.idPedido}`, 'success');
      navigate('/confirmation');
    } catch(err) {
      console.error(err);
      await MySwal.fire('Error en la compra', err.message || 'Fallo al procesar', 'error');
    } finally {
      setLoading(false);
    }
  };

  const renderPaymentFields = () =>
    (fieldConfigs[form.tipoPago] || []).map(fld => (
      <div key={fld.name} className="input-group">
        <input
          id={fld.name}
          name={fld.name}
          value={form[fld.name]}
          onChange={handleChange}
          className={`floating-input ${errors[fld.name]?'error':''}`}
          placeholder=" "
        />
        <label htmlFor={fld.name} className="floating-label">{fld.label}</label>
        {errors[fld.name] && <span className="error-message">{errors[fld.name]}</span>}
      </div>
    ));

  return (
    <div className="checkout-container">
      <h1>Finalizar Compra</h1>
      <form onSubmit={handleSubmit}>

        {/* Personal */}
        <div className="form-section">
          <h2>Información Personal</h2>
          <div className="input-group">
            <input
              id="nombre"
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              className={`floating-input ${errors.nombre?'error':''}`}
              placeholder=" "
            />
            <label htmlFor="nombre" className="floating-label">Nombre Completo</label>
            {errors.nombre && <span className="error-message">{errors.nombre}</span>}
          </div>
        </div>

        {/* Dirección */}
        <div className="form-section">
          <h2>Dirección de Envío</h2>
          <div className="input-group">
            <select
              name="direccionId"
              value={form.direccionId}
              onChange={e => {
                const v = e.target.value;
                setAddingNew(v === 'nueva');
                setForm(f => ({ ...f, direccionId: v }));
              }}
              className={`floating-input ${errors.direccionId?'error':''}`}
            >
              <option value="">–Seleccione–</option>
              {direcciones.map(d => (
                <option key={d.idDireccion} value={d.idDireccion}>
                  {d.alias} — {d.calle}, {d.ciudad}
                </option>
              ))}
              <option value="nueva">+ Nueva Dirección</option>
            </select>
            <label className="floating-label">Dirección</label>
            {errors.direccionId && <span className="error-message">{errors.direccionId}</span>}
          </div>

          {addingNew && (
            <>
              <div className="input-group">
                <input name="alias" value={form.alias} onChange={handleChange}
                  className={`floating-input ${errors.alias?'error':''}`} placeholder=" " />
                <label className="floating-label">Alias (Casa, Trabajo)</label>
                {errors.alias && <span className="error-message">{errors.alias}</span>}
              </div>
              <div className="input-group">
                <input name="calle" value={form.calle} onChange={handleChange}
                  className={`floating-input ${errors.calle?'error':''}`} placeholder=" " />
                <label className="floating-label">Calle y Número</label>
                {errors.calle && <span className="error-message">{errors.calle}</span>}
              </div>
              <div className="input-group">
                <input name="ciudad" value={form.ciudad} onChange={handleChange}
                  className={`floating-input ${errors.ciudad?'error':''}`} placeholder=" " />
                <label className="floating-label">Ciudad</label>
                {errors.ciudad && <span className="error-message">{errors.ciudad}</span>}
              </div>
              <div className="input-group">
                <input name="departamento" value={form.departamento} onChange={handleChange}
                  className={`floating-input ${errors.departamento?'error':''}`} placeholder=" " />
                <label className="floating-label">Departamento</label>
                {errors.departamento && <span className="error-message">{errors.departamento}</span>}
              </div>
              <div className="map-section">
                <p>Seleccione ubicación en el mapa:</p>
                <AddressPicker onSelect={({ latitud, longitud }) => {
                  setForm(f => ({ ...f, latitud, longitud }));
                  setErrors(e => ({ ...e, latitud:'', longitud:'' }));
                }} />
                {(errors.latitud || errors.longitud) && (
                  <span className="error-message">
                    {errors.latitud || errors.longitud}
                  </span>
                )}
              </div>
            </>
          )}
        </div>

        {/* Pago */}
        <div className="form-section">
          <h2>Método de Pago</h2>
          <div className="input-group">
            <select name="tipoPago" value={form.tipoPago}
              onChange={e => setForm(f => ({ ...f, tipoPago: e.target.value }))}
              className={`floating-input ${errors.tipoPago?'error':''}`}>
              <option value="">–Seleccione–</option>
              {paymentOptions.map(o => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
            <label className="floating-label">Método de Pago</label>
            {errors.tipoPago && <span className="error-message">{errors.tipoPago}</span>}
          </div>
          {renderPaymentFields()}
        </div>

        {/* Cupón */}
        <div className="form-section">
          <label className="coupon-toggle">
            <input type="checkbox" checked={hasCupon} onChange={() => setHasCupon(h => !h)} />
            ¿Tiene cupón?
          </label>
          {hasCupon && (
            <div className="input-group">
              <input name="cuponCodigo" value={form.cuponCodigo} onChange={handleChange}
                className={`floating-input ${errors.cuponCodigo?'error':''}`} placeholder=" " />
              <label className="floating-label">Código de Cupón</label>
              {errors.cuponCodigo && <span className="error-message">{errors.cuponCodigo}</span>}
            </div>
          )}
        </div>

        <button type="submit" className="submit-button" disabled={loading}>
          {loading ? 'Procesando...' : 'Confirmar y Pagar'}
        </button>
      </form>
    </div>
  );
}
