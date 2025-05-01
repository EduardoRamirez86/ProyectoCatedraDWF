import React, { useState, useEffect } from 'react';
import {
  getAllProductos,
  createProducto,
  updateProducto,
  deleteProducto,
  getAllTiposProductos,
} from '../services/productoService';

const ProductoCrud = () => {
  const [productos, setProductos]         = useState([]);
  const [tiposProductos, setTiposProductos] = useState([]);
  const [form, setForm]                   = useState({
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
  const [isEditing, setIsEditing]         = useState(false);
  const [loading, setLoading]             = useState(false);

  useEffect(() => {
    fetchProductos();
    fetchTiposProductos();
  }, []);

  const fetchProductos = async () => {
    setLoading(true);
    try {
      const data = await getAllProductos();
      setProductos(data);
    } catch (e) {
      console.error('Error al obtener productos:', e);
    }
    setLoading(false);
  };

  const fetchTiposProductos = async () => {
    try {
      const data = await getAllTiposProductos();
      setTiposProductos(data);
    } catch (e) {
      console.error('Error al obtener tipos de productos:', e);
    }
  };

  const handleInputChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
  
    // Validaciones mínimas
    if (!form.nombre || !form.descripcion) {
      return alert('Nombre y descripción son obligatorios');
    }
    if (!form.idTipoProducto || isNaN(+form.idTipoProducto)) {
      return alert('Seleccione un tipo de producto válido');
    }
  
    // Construcción del payload
    const payload = {
      ...form,
      precio:     parseFloat(form.precio)       || 0,
      costo:      parseFloat(form.costo)        || 0,
      cantidad:   parseInt(form.cantidad, 10)   || 0,
      cantidadPuntos: parseInt(form.cantidadPuntos, 10) || 0,
      idTipoProducto: parseInt(form.idTipoProducto, 10),
    };
  
    try {
      if (isEditing) {
        await updateProducto(form.id, payload);
      } else {
        await createProducto(payload);
      }
      resetForm();
      fetchProductos();
    } catch (error) {
      console.error('Error al guardar producto:', error.message);
      alert('Hubo un error al guardar el producto');
    }
  };
  

  const handleEdit = p => {
    setForm({
      idProducto:     p.idProducto,
      nombre:         p.nombre,
      descripcion:    p.descripcion,
      precio:         p.precio?.toString()      || '',
      costo:          p.costo?.toString()       || '',
      cantidad:       p.cantidad?.toString()    || '',
      imagen:         p.imagen      || '',
      cantidadPuntos: p.cantidadPuntos?.toString() || '',
      idTipoProducto: p.tipoProducto?.idTipoProducto?.toString() || '',
    });
    setIsEditing(true);
  };

  const handleDelete = async id => {
    if (!window.confirm('¿Eliminar este producto?')) return;
    try {
      await deleteProducto(id);
      fetchProductos();
    } catch (e) {
      console.error('Error al eliminar:', e);
    }
  };

  const resetForm = () => {
    setForm({
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
    setIsEditing(false);
  };

  return (
    <div>
      <h1>Gestión de Productos</h1>
      <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
        <input
          name="nombre"
          placeholder="Nombre*"
          value={form.nombre}
          onChange={handleInputChange}
          required
        />
        <input
          name="descripcion"
          placeholder="Descripción*"
          value={form.descripcion}
          onChange={handleInputChange}
          required
        />
        <input
          name="precio" type="number"
          placeholder="Precio"
          value={form.precio}
          onChange={handleInputChange}
        />
        <input
          name="costo" type="number"
          placeholder="Costo"
          value={form.costo}
          onChange={handleInputChange}
        />
        <input
          name="cantidad" type="number"
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
          name="cantidadPuntos" type="number"
          placeholder="Puntos"
          value={form.cantidadPuntos}
          onChange={handleInputChange}
        />

        <select
          name="idTipoProducto"
          value={form.idTipoProducto}
          onChange={handleInputChange}
          required
        >
          <option value="">-- Seleccione tipo --</option>
          {tiposProductos.map(tp => (
            <option
              key={tp.idTipoProducto}
              value={tp.idTipoProducto}
            >
              {tp.tipo}
            </option>
          ))}
        </select>

        <button type="submit">
          {isEditing ? 'Actualizar' : 'Crear'}
        </button>
        {isEditing && (
          <button type="button" onClick={resetForm}>
            Cancelar
          </button>
        )}
      </form>

      {loading ? (
        <p>Cargando…</p>
      ) : (
        <table border="1" cellPadding="6">
          <thead>
            <tr>
              <th>ID</th><th>Nombre</th><th>Desc.</th>
              <th>Precio</th><th>Costo</th><th>Cant.</th>
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
                {/* Aquí protegemos el acceso con ?. y fallback */}
                <td>{p.tipoProducto?.tipo ?? '—'}</td>
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
