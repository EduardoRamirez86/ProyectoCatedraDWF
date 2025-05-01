import React, { useState, useEffect } from 'react';
import {
  getAllProductos,
  createProducto,
  updateProducto,
  deleteProducto,
  getAllTiposProductos,
} from '../services/productoService';
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
  idTipoProducto: '',
});

const ProductoCrud = () => {
  const [productos, setProductos] = useState([]);
  const [tiposProductos, setTiposProductos] = useState([]);
  const [form, setForm] = useState(initialFormState());
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchProductos();
    fetchTiposProductos();
  }, []);

  const fetchProductos = async () => {
    setLoading(true);
    try {
      const data = await getAllProductos();
      setProductos(data);
    } catch (error) {
      console.error(error);
      alert('Error al obtener productos.');
    } finally {
      setLoading(false);
    }
  };

  const fetchTiposProductos = async () => {
    try {
      const data = await getAllTiposProductos();
      setTiposProductos(data);
    } catch (error) {
      console.error(error);
      alert('Error al obtener tipos de productos.');
    }
  };

  const handleInputChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const validateForm = () => {
    if (!form.nombre || !form.descripcion) {
      alert('Nombre y descripción son obligatorios.');
      return false;
    }
    if (!form.idTipoProducto || isNaN(+form.idTipoProducto)) {
      alert('Seleccione un tipo de producto válido.');
      return false;
    }
    return true;
  };

  const buildPayload = () => ({
    ...form,
    precio: parseFloat(form.precio) || 0,
    costo: parseFloat(form.costo) || 0,
    cantidad: parseInt(form.cantidad, 10) || 0,
    cantidadPuntos: parseInt(form.cantidadPuntos, 10) || 0,
    idTipoProducto: parseInt(form.idTipoProducto, 10),
  });

  const handleSubmit = async e => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      if (isEditing) {
        await updateProducto(form.idProducto, buildPayload());
        alert('Producto actualizado.');
      } else {
        await createProducto(buildPayload());
        alert('Producto creado.');
      }
      resetForm();
      fetchProductos();
    } catch (error) {
      console.error(error);
      alert('Hubo un error al guardar el producto.');
    }
  };

  const handleEdit = p => {
    setForm({
      idProducto: p.idProducto,
      nombre: p.nombre,
      descripcion: p.descripcion,
      precio: p.precio?.toString() || '',
      costo: p.costo?.toString() || '',
      cantidad: p.cantidad?.toString() || '',
      imagen: p.imagen || '',
      cantidadPuntos: p.cantidadPuntos?.toString() || '',
      idTipoProducto: p.tipoProducto?.idTipoProducto?.toString() || '',
    });
    setIsEditing(true);
  };

  const handleDelete = async id => {
    if (!window.confirm('¿Eliminar este producto?')) return;
    try {
      await deleteProducto(id);
      alert('Producto eliminado.');
      fetchProductos();
    } catch (error) {
      console.error(error);
      alert('Error al eliminar.');
    }
  };

  const resetForm = () => {
    setForm(initialFormState());
    setIsEditing(false);
  };

  return (
    <div className="admin-container">
      <h1>Gestión de Productos</h1>
      <form onSubmit={handleSubmit} className="form">
        <input
          name="nombre"
          placeholder="Nombre*"
          value={form.nombre}
          onChange={handleInputChange}
        />
        <input
          name="descripcion"
          placeholder="Descripción*"
          value={form.descripcion}
          onChange={handleInputChange}
        />
        <input
          name="precio"
          type="number"
          placeholder="Precio"
          value={form.precio}
          onChange={handleInputChange}
        />
        <input
          name="costo"
          type="number"
          placeholder="Costo"
          value={form.costo}
          onChange={handleInputChange}
        />
        <input
          name="cantidad"
          type="number"
          placeholder="Cantidad"
          value={form.cantidad}
          onChange={handleInputChange}
        />
        <input
          name="imagen"
          placeholder="URL Imagen"
          value={form.imagen}
          onChange={handleInputChange}
        />
        <input
          name="cantidadPuntos"
          type="number"
          placeholder="Puntos"
          value={form.cantidadPuntos}
          onChange={handleInputChange}
        />

        <select
          name="idTipoProducto"
          value={form.idTipoProducto}
          onChange={handleInputChange}
        >
          <option value="">-- Seleccione un tipo --</option>
          {tiposProductos.map(tp => (
            <option key={tp.idTipoProducto} value={tp.idTipoProducto}>
              {tp.tipo}
            </option>
          ))}
        </select>

        <div className="form-actions">
          <button type="submit">{isEditing ? 'Actualizar' : 'Crear'}</button>
          {isEditing && <button type="button" onClick={resetForm}>Cancelar</button>}
        </div>
      </form>

      {loading ? (
        <p>Cargando…</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th><th>Nombre</th><th>Descripción</th>
              <th>Precio</th><th>Costo</th><th>Cantidad</th>
              <th>Puntos</th><th>Tipo</th><th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productos.map(p => (
              <tr key={p.idProducto}>
                <td>{p.idProducto}</td>
                <td>{p.nombre}</td>
                <td>{p.descripcion}</td>
                <td>{p.precio}</td>
                <td>{p.costo}</td>
                <td>{p.cantidad}</td>
                <td>{p.cantidadPuntos}</td>
                <td>{p.nombreTipo || '—'}</td>
                <td>
                  <button onClick={() => handleEdit(p)}>✏️</button>
                  <button onClick={() => handleDelete(p.idProducto)}>🗑️</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ProductoCrud;
