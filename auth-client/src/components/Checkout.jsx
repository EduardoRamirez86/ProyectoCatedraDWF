import React, { useState, useEffect, useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { checkoutPedido } from '../services/pedidoService';
import { getByUserDirecciones, saveDireccion } from '../services/direccionService';
import MySwal from '../utils/swal';
import { secureGetItem } from '../utils/secureStorage';
import AddressPicker from './AddressPicker';
import { CartContext } from '../context/CartContext';

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
  const { state } = useLocation();
  const navigate = useNavigate();
  const { envio = 5 } = useContext(CartContext);
  const total = state?.total || 0;
  const [hasCupon, setHasCupon] = useState(false);

  const roundToTwo = (num) => Math.round(num * 100) / 100;
  const subtotal = roundToTwo(total - envio);
  const discount = hasCupon ? roundToTwo(subtotal * 0.15) : 0;
  const totalWithDiscount = roundToTwo(subtotal - discount + envio);

  const [form, setForm] = useState({
    nombre: '', tipoPago: '', cuponCodigo: '',
    direccionId: '', alias: '', calle: '', ciudad: '', departamento: '',
    latitud: null, longitud: null,
    card: '', expiry: '', paypalAccount: '', transferencia: ''
  });
  const [errors, setErrors] = useState({});
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

  const handleSubmit = async (e) => {
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
          longitud: form.longitud,
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
        idDireccion: dirId,
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
      <div key={fld.name} className="mb-4">
        <label className="block text-sm font-medium mb-1">{fld.label}</label>
        <input
          id={fld.name}
          name={fld.name}
          value={form[fld.name]}
          onChange={handleChange}
          className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors[fld.name] ? 'border-red-400' : ''}`}
          placeholder={fld.label}
        />
        {errors[fld.name] && <span className="text-red-500 text-xs">{errors[fld.name]}</span>}
      </div>
    ));

  return (
    <div className="container mx-auto px-4 py-8">
      <button
        className="mb-6 text-indigo-600 hover:underline flex items-center gap-2"
        onClick={() => navigate(-1)}
      >
        <i className="fas fa-arrow-left"></i> Volver
      </button>
      <h1 className="text-3xl font-bold mb-8 text-indigo-700 flex items-center gap-2">
        <i className="fas fa-credit-card text-indigo-400"></i>
        Finalizar Compra
      </h1>
      <form onSubmit={handleSubmit} className="bg-white shadow-md rounded-xl p-8 max-w-2xl mx-auto">
        {/* Información Personal */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold mb-4 text-indigo-700">Información Personal</h2>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-1">Nombre Completo</label>
            <input
              id="nombre"
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors.nombre ? 'border-red-400' : ''}`}
              placeholder="Nombre completo"
            />
            {errors.nombre && <span className="text-red-500 text-xs">{errors.nombre}</span>}
          </div>
        </div>

        {/* Dirección de Envío */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold mb-4 text-indigo-700">Dirección de Envío</h2>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-1">Dirección</label>
            <select
              name="direccionId"
              value={form.direccionId}
              onChange={e => {
                const value = e.target.value;
                setAddingNew(value === 'nueva');
                setForm(f => ({ ...f, direccionId: value }));
                setErrors(e => ({ ...e, direccionId: '' }));
              }}
              className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors.direccionId ? 'border-red-400' : ''}`}
            >
              <option value="">–Seleccione–</option>
              {direcciones.map(d => (
                <option key={d.idDireccion} value={d.idDireccion}>
                  {d.alias} — {d.calle}, {d.ciudad}
                </option>
              ))}
              <option value="nueva">+ Nueva Dirección</option>
            </select>
            {errors.direccionId && <span className="text-red-500 text-xs">{errors.direccionId}</span>}
          </div>
          {addingNew && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {['alias', 'calle', 'ciudad', 'departamento'].map(field => (
                <div key={field}>
                  <label className="block text-sm font-medium mb-1 capitalize">{field}</label>
                  <input
                    name={field}
                    value={form[field]}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors[field] ? 'border-red-400' : ''}`}
                    placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
                  />
                  {errors[field] && <span className="text-red-500 text-xs">{errors[field]}</span>}
                </div>
              ))}
              <div className="md:col-span-2">
                <p className="text-sm text-gray-600 mb-2">Seleccione ubicación en el mapa:</p>
                <AddressPicker onSelect={({ latitud, longitud }) => {
                  setForm(f => ({ ...f, latitud, longitud }));
                  setErrors(e => ({ ...e, latitud: '', longitud: '' }));
                }} />
                {(errors.latitud || errors.longitud) && (
                  <span className="text-red-500 text-xs">
                    {errors.latitud || errors.longitud}
                  </span>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Pago */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold mb-4 text-indigo-700">Pago</h2>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-1">Método de Pago</label>
            <select
              name="tipoPago"
              value={form.tipoPago}
              onChange={handleChange}
              className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors.tipoPago ? 'border-red-400' : ''}`}
            >
              <option value="">–Seleccione método de pago–</option>
              {paymentOptions.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
            {errors.tipoPago && <span className="text-red-500 text-xs">{errors.tipoPago}</span>}
          </div>
          {renderPaymentFields()}
        </div>

        {/* Cupón */}
        <div className="mb-6">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={hasCupon}
              onChange={e => setHasCupon(e.target.checked)}
              className="form-checkbox"
            />
            ¿Tienes un cupón de descuento?
          </label>
          {hasCupon && (
            <div className="mt-2">
              <label className="block text-sm font-medium mb-1">Código de Cupón</label>
              <input
                name="cuponCodigo"
                value={form.cuponCodigo}
                onChange={handleChange}
                className={`w-full px-3 py-2 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 ${errors.cuponCodigo ? 'border-red-400' : ''}`}
                placeholder="Código de cupón"
              />
              {errors.cuponCodigo && <span className="text-red-500 text-xs">{errors.cuponCodigo}</span>}
            </div>
          )}
        </div>

        {/* Resumen */}
        <div className="mb-6 bg-indigo-50 rounded-lg p-4">
          <div className="flex justify-between mb-2">
            <span className="font-medium">Subtotal:</span>
            <span>${subtotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between mb-2">
            <span className="font-medium">Envío:</span>
            <span>${envio.toFixed(2)}</span>
          </div>
          {hasCupon && (
            <div className="flex justify-between mb-2">
              <span className="font-medium">Descuento (15%):</span>
              <span className="text-green-600">-${discount.toFixed(2)}</span>
            </div>
          )}
          <div className="flex justify-between text-lg font-bold text-indigo-700">
            <span>Total:</span>
            <span>${totalWithDiscount.toFixed(2)}</span>
          </div>
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition w-full md:w-auto"
          >
            {loading ? 'Procesando...' : 'Finalizar Compra'}
          </button>
        </div>
      </form>
    </div>
  );
}