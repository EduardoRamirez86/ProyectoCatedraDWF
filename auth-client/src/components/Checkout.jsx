import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { checkoutPedido } from '../services/pedidoService';
import { getByUserDirecciones, saveDireccion } from '../services/direccionService';
import MySwal from '../utils/swal';
import { secureGetItem } from '../utils/secureStorage';
import AddressPicker from './AddressPicker';
import '../style/Checkout.css';

const paymentOptions = [
  { label: 'Tarjeta de Crédito', value: 'TARJETA_CREDITO' },
  { label: 'PayPal', value: 'PAYPAL' },
  { label: 'Efectivo', value: 'EFECTIVO' },
  { label: 'Transferencia Bancaria', value: 'TRANSFERENCIA_BANCARIA' }
];

const fieldConfigs = {
  TARJETA_CREDITO: [
    { name: 'card', label: 'Número de Tarjeta', regex: /^\d{4} \d{4} \d{4} \d{4}$/ },
    { name: 'expiry', label: 'Fecha de Expiración', regex: /^(0[1-9]|1[0-2])\/\d{2}$/ }
  ],
  PAYPAL: [
    { name: 'paypalAccount', label: 'Correo de PayPal', regex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ }
  ],
  TRANSFERENCIA_BANCARIA: [
    { name: 'transferencia', label: 'Número de Cuenta', regex: /^\d{20}$/ }
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
      getByUserDirecciones(uid)
        .then(setDirecciones)
        .catch(console.error);
    }
  }, []);

  const handleChange = e => {
    let { name, value } = e.target;
    if (name === 'card') {
      value = value.replace(/\D/g, '').slice(0, 16).replace(/(\d{4})(?=\d)/g, '$1 ');
    }
    if (name === 'expiry') {
      value = value.replace(/\D/g, '').slice(0, 4).replace(/(\d{2})(\d)/, '$1/$2');
    }
    setForm(f => ({ ...f, [name]: value }));
    setErrors(e => ({ ...e, [name]: '' }));
  };

  const validateForm = () => {
    const err = {};
    let ok = true;

    if (!form.nombre.trim()) { err.nombre = 'Requerido'; ok = false; }
    if (!form.tipoPago) { err.tipoPago = 'Requerido'; ok = false; }

    if (addingNew) {
      if (!form.alias.trim()) { err.alias = 'Alias requerido'; ok = false; }
      if (!form.calle.trim()) { err.calle = 'Calle requerida'; ok = false; }
      if (!form.ciudad.trim()) { err.ciudad = 'Ciudad requerida'; ok = false; }
      if (!form.departamento.trim()) { err.departamento = 'Departamento requerido'; ok = false; }
      if (!form.latitud || !form.longitud) {
        err.latitud = 'Debe seleccionar la ubicación en el mapa';
        ok = false;
      }
    } else {
      if (!form.direccionId || form.direccionId === 'nueva') {
        err.direccionId = 'Debe seleccionar o crear una dirección';
        ok = false;
      }
    }

    (fieldConfigs[form.tipoPago] || []).forEach(fld => {
      if (!fld.regex.test(form[fld.name] || '')) {
        err[fld.name] = 'Formato inválido';
        ok = false;
      }
    });

    if (hasCupon && !form.cuponCodigo.trim()) {
      err.cuponCodigo = 'Ingrese un cupón válido';
      ok = false;
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

      if (!dirId || dirId === 'nueva') {
        throw new Error('Debe seleccionar o crear una dirección válida');
      }

      const payload = {
        idCarrito,
        tipoPago: form.tipoPago,
        cuponCodigo: hasCupon ? form.cuponCodigo.trim() : null,
        idDireccion: dirId
      };

      const resp = await checkoutPedido(payload);
      sessionStorage.setItem('orderNumber', resp.idPedido);
      sessionStorage.setItem('orderTotal', resp.total);
      await MySwal.fire('¡Compra Exitosa!', `Pedido #${resp.idPedido}`, 'success');
      navigate('/confirmation');
    } catch (err) {
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
          className={`floating-input ${errors[fld.name] ? 'error' : ''}`}
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
            <label htmlFor="nombre" className="floating-label">Nombre Completo</label>
            {errors.nombre && <span className="error-message">{errors.nombre}</span>}
          </div>
        </div>

        <div className="form-section">
          <h2>Dirección de Envío</h2>
          <div className="input-group">
            <select
              name="direccionId"
              value={form.direccionId}
              onChange={e => {
                const value = e.target.value;
                setAddingNew(value === 'nueva');
                setForm(f => ({ ...f, direccionId: value }));
                setErrors(e => ({ ...e, direccionId: '' }));
              }}
              className={`floating-input ${errors.direccionId ? 'error' : ''}`}
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
              {['alias', 'calle', 'ciudad', 'departamento'].map(field => (
                <div key={field} className="input-group">
                  <input
                    name={field}
                    value={form[field]}
                    onChange={handleChange}
                    className={`floating-input ${errors[field] ? 'error' : ''}`}
                    placeholder=" "
                  />
                  <label className="floating-label">
                    {field.charAt(0).toUpperCase() + field.slice(1)}
                  </label>
                  {errors[field] && <span className="error-message">{errors[field]}</span>}
                </div>
              ))}
              <div className="map-section">
                <p>Seleccione ubicación en el mapa:</p>
                <AddressPicker onSelect={({ latitud, longitud }) => {
                  setForm(f => ({ ...f, latitud, longitud }));
                  setErrors(e => ({ ...e, latitud: '', longitud: '' }));
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

        <div className="form-section">
          <h2>Pago</h2>
          <div className="input-group">
            <select
              name="tipoPago"
              value={form.tipoPago}
              onChange={handleChange}
              className={`floating-input ${errors.tipoPago ? 'error' : ''}`}
            >
              <option value="">–Seleccione método de pago–</option>
              {paymentOptions.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
            <label className="floating-label">Método de Pago</label>
            {errors.tipoPago && <span className="error-message">{errors.tipoPago}</span>}
          </div>

          {renderPaymentFields()}
        </div>

        <div className="form-section">
          <label>
            <input
              type="checkbox"
              checked={hasCupon}
              onChange={e => setHasCupon(e.target.checked)}
            />
            ¿Tienes un cupón de descuento?
          </label>
          {hasCupon && (
            <div className="input-group">
              <input
                name="cuponCodigo"
                value={form.cuponCodigo}
                onChange={handleChange}
                className={`floating-input ${errors.cuponCodigo ? 'error' : ''}`}
                placeholder=" "
              />
              <label className="floating-label">Código de Cupón</label>
              {errors.cuponCodigo && <span className="error-message">{errors.cuponCodigo}</span>}
            </div>
          )}
        </div>

        <div className="form-section">
          <button type="submit" disabled={loading} className="submit-button">
            {loading ? 'Procesando...' : 'Finalizar Compra'}
          </button>
        </div>
      </form>
    </div>
  );
}