import React, { useState, useEffect, useCallback } from 'react';
import {
  getAllProductosPaged,
  getAllTiposProductos,
  createProducto,
  updateProducto,
  deleteProducto
} from '../services/productoService';
import MySwal from '../utils/swal';
import '../style/Crud.css';

const initialFormState = () => ({
  idProducto: null,
  nombre: '',
  descripcion: '',
  precio: '',
  costo: '',
  cantidad: '',
  imagen: '',
  cantidadPuntos: '',
  idTipoProducto: ''
});

export default function ProductoCrudPaged() {
  const [productos, setProductos] = useState([]);
  const [tiposProductos, setTiposProductos] = useState([]);
  const [form, setForm] = useState(initialFormState());
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const size = 10;

  const loadTipos = useCallback(async () => {
    try {
      const tipos = await getAllTiposProductos();
      setTiposProductos(tipos);
    } catch (e) {
      console.error(e);
      MySwal.fire('Error', 'No se pudieron cargar los tipos de producto', 'error');
    }
  }, []);

  const loadProductos = useCallback(async (pg = 0) => {
    setLoading(true);
    try {
      const res = await getAllProductosPaged(pg, size);
      setProductos(res.items);
      setPage(res.page);
      setTotalPages(res.totalPages);
    } catch (e) {
      console.error(e);
      MySwal.fire('Error', 'No se pudieron cargar los productos', 'error');
    } finally {
      setLoading(false);
    }
  }, [size]);

  useEffect(() => {
    loadTipos();
    loadProductos(0);
  }, [loadTipos, loadProductos]);

  const handleInputChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const validateForm = () => {
    if (!form.nombre || !form.descripcion) {
      MySwal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Nombre y descripción son obligatorios.' });
      return false;
    }
    if (!form.idTipoProducto) {
      MySwal.fire({ icon: 'warning', title: 'Tipo de producto', text: 'Seleccione un tipo válido.' });
      return false;
    }
    return true;
  };

  const buildPayload = () => ({
    ...form,
    precio: form.precio ? form.precio.toString() : '0',
    costo: form.costo ? form.costo.toString() : '0',
    cantidad: parseInt(form.cantidad, 10) || 0,
    cantidadPuntos: parseInt(form.cantidadPuntos, 10) || 0,
    idTipoProducto: parseInt(form.idTipoProducto, 10)
  });

  const resetForm = () => {
    setForm(initialFormState());
    setIsEditing(false);
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!validateForm()) return;
    setLoading(true);
    try {
      if (isEditing) {
        await updateProducto(form.idProducto, buildPayload());
        MySwal.fire('¡Producto actualizado!', '', 'success');
      } else {
        await createProducto(buildPayload());
        MySwal.fire('¡Producto creado!', '', 'success');
      }
      resetForm();
      loadProductos(page);
    } catch (error) {
      console.error(error);
      MySwal.fire('Error', 'No se pudo guardar el producto.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = p => {
    setForm({
      idProducto: p.idProducto,
      nombre: p.nombre,
      descripcion: p.descripcion,
      precio: p.precio.toString(),
      costo: p.costo.toString(),
      cantidad: p.cantidad.toString(),
      imagen: p.imagen || '',
      cantidadPuntos: p.cantidadPuntos.toString(),
      idTipoProducto: p.idTipoProducto.toString()
    });
    setIsEditing(true);
  };

  const handleDelete = async id => {
    const { isConfirmed } = await MySwal.fire({
      icon: 'warning', title: '¿Eliminar?', showCancelButton: true
    });
    if (!isConfirmed) return;
    setLoading(true);
    try {
      await deleteProducto(id);
      MySwal.fire('¡Eliminado!', '', 'success');
      resetForm();
      loadProductos(page);
    } catch (e) {
      console.error(e);
      MySwal.fire('Error', 'No se pudo eliminar el producto.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-container">
      <h1>Gestión de Productos</h1>
      <form onSubmit={handleSubmit} className="form">
        {/* Formulario */}
        {['nombre','descripcion','precio','costo','cantidad','imagen','cantidadPuntos'].map(field => (
          <div className="input-group" key={field}>
            <label htmlFor={field}>{field.charAt(0).toUpperCase()+field.slice(1)}</label>
            <input
              id={field}
              name={field}
              value={form[field]}
              onChange={handleInputChange}
              className="input-field"
            />
          </div>
        ))}
        <div className="input-group">
          <label htmlFor="idTipoProducto">Tipo de Producto</label>
          <select
            id="idTipoProducto"
            name="idTipoProducto"
            value={form.idTipoProducto}
            onChange={handleInputChange}
            className="input-field"
          >
            <option value="">-- Seleccione --</option>
            {tiposProductos.map(tp => (
              <option key={tp.idTipoProducto} value={tp.idTipoProducto}>{tp.tipo}</option>
            ))}
          </select>
        </div>
        <div className="form-actions">
          <button type="submit" disabled={loading} className="submit-btn">
            {isEditing ? 'Actualizar' : 'Crear'}
          </button>
          {isEditing && <button type="button" onClick={resetForm} className="cancel-btn">Cancelar</button>}
        </div>
      </form>

      {/* Tabla paginada */}
      {loading ? <p>Cargando…</p> : (
      <>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th><th>Nombre</th><th>Precio</th><th>Cantidad</th><th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productos.map(p => (
              <tr key={p.idProducto}>
                <td>{p.idProducto}</td>
                <td>{p.nombre}</td>
                <td>${p.precio.toFixed(2)}</td>
                <td>{p.cantidad}</td>
                <td>
                  <button onClick={() => handleEdit(p)} disabled={loading}>✏️</button>
                  <button onClick={() => handleDelete(p.idProducto)} disabled={loading}>🗑️</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="pagination">
          <button onClick={() => loadProductos(page-1)} disabled={page===0}>« Anterior</button>
          <span>Página {page+1} de {totalPages}</span>
          <button onClick={() => loadProductos(page+1)} disabled={page+1>=totalPages}>Siguiente »</button>
        </div>
      </>)}
    </div>
  );
}