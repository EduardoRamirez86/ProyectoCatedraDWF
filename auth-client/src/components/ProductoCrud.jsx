import React, { useState, useEffect } from 'react';
import {
  getAllProductos,
  createProducto,
  updateProducto,
  deleteProducto,
  getAllTiposProductos,
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
  idTipoProducto: '',
});

const ProductoCrud = () => {
  const [productos, setProductos] = useState([]);
  const [tiposProductos, setTiposProductos] = useState([]);
  const [form, setForm] = useState(initialFormState());
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const tiposData = await getAllTiposProductos();
        console.log('Tipos de productos:', tiposData);
        setTiposProductos(tiposData);

        const productosData = await getAllProductos();
        console.log('Productos:', productosData);
        const productosConTipo = productosData.map(producto => ({
          ...producto,
          nombreTipo: tiposData.find(tp => tp.idTipoProducto === producto.idTipoProducto)?.tipo || '—',
        }));
        setProductos(productosConTipo);
      } catch (error) {
        console.error(error);
        await MySwal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo cargar los datos.',
          timer: 2000,
          showConfirmButton: false,
        });
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleInputChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const validateForm = () => {
    if (!form.nombre || !form.descripcion) {
      MySwal.fire({
        icon: 'warning',
        title: 'Campos requeridos',
        text: 'Nombre y descripción son obligatorios.',
      });
      return false;
    }
    if (!form.idTipoProducto || isNaN(+form.idTipoProducto)) {
      MySwal.fire({
        icon: 'warning',
        title: 'Tipo de producto',
        text: 'Seleccione un tipo de producto válido.',
      });
      return false;
    }
    return true;
  };

  const buildPayload = () => ({
    ...form,
    precio: form.precio ? parseFloat(form.precio).toString() : '0',
    costo: form.costo ? parseFloat(form.costo).toString() : '0',
    cantidad: parseInt(form.cantidad, 10) || 0,
    cantidadPuntos: parseInt(form.cantidadPuntos, 10) || 0,
    idTipoProducto: parseInt(form.idTipoProducto, 10),
  });

  const handleSubmit = async e => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      if (isEditing) {
        const updated = await updateProducto(form.idProducto, buildPayload());
        await MySwal.fire({
          icon: 'success',
          title: '¡Producto actualizado!',
          text: `El producto "${updated.nombre}" se actualizó correctamente.`,
          timer: 2000,
          showConfirmButton: false,
        });
      } else {
        const created = await createProducto(buildPayload());
        await MySwal.fire({
          icon: 'success',
          title: '¡Producto creado!',
          text: `El producto "${created.nombre}" se creó correctamente.`,
          timer: 2000,
          showConfirmButton: false,
        });
      }
      resetForm();
      // Refrescar productos después de crear/actualizar
      const tiposData = await getAllTiposProductos();
      setTiposProductos(tiposData);
      const productosData = await getAllProductos();
      const productosConTipo = productosData.map(producto => ({
        ...producto,
        nombreTipo: tiposData.find(tp => tp.idTipoProducto === producto.idTipoProducto)?.tipo || '—',
      }));
      setProductos(productosConTipo);
    } catch (error) {
      console.error(error);
      await MySwal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo guardar el producto.',
      });
    }
  };

  const handleEdit = p => {
    setForm({
      idProducto: p.idProducto,
      nombre: p.nombre,
      descripcion: p.descripcion,
      precio: p.precio ? p.precio.toString() : '',
      costo: p.costo ? p.costo.toString() : '',
      cantidad: p.cantidad?.toString() || '',
      imagen: p.imagen || '',
      cantidadPuntos: p.cantidadPuntos?.toString() || '',
      idTipoProducto: p.idTipoProducto?.toString() || '',
    });
    setIsEditing(true);
  };

  const handleDelete = async id => {
    const result = await MySwal.fire({
      icon: 'warning',
      title: '¿Eliminar producto?',
      text: 'Esta acción no se puede deshacer.',
      showCancelButton: true,
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
    });

    if (result.isConfirmed) {
      try {
        await deleteProducto(id);
        await MySwal.fire({
          icon: 'success',
          title: '¡Producto eliminado!',
          text: 'El producto ha sido eliminado.',
          timer: 2000,
          showConfirmButton: false,
        });
        // Refrescar productos después de eliminar
        const tiposData = await getAllTiposProductos();
        setTiposProductos(tiposData);
        const productosData = await getAllProductos();
        const productosConTipo = productosData.map(producto => ({
          ...producto,
          nombreTipo: tiposData.find(tp => tp.idTipoProducto === producto.idTipoProducto)?.tipo || '—',
        }));
        setProductos(productosConTipo);
      } catch (error) {
        console.error(error);
        await MySwal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo eliminar el producto.',
        });
      }
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
          step="0.01"
          placeholder="Precio"
          value={form.precio}
          onChange={handleInputChange}
        />
        <input
          name="costo"
          type="number"
          step="0.01"
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
              <th>ID</th>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Precio</th>
              <th>Costo</th>
              <th>Cantidad</th>
              <th>Puntos</th>
              <th>Tipo</th>
              <th>Acciones</th>
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