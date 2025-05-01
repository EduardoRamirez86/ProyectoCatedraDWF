// src/pages/AdminPage.jsx
import React, { useState, useEffect } from 'react';
import MySwal from '../utils/swal';
import {
  getAllRopa,
  getRopaById,
  createRopa,
  updateRopa,
  deleteRopa
} from '../services/ropaService';
import '../style/ropaCrud.css';

export default function RopaCrud() {
  const [ropaList, setRopaList] = useState([]);
  const [newRopa, setNewRopa] = useState({ nombre: '', precio: '' });
  const [selectedRopa, setSelectedRopa] = useState(null);
  const [editRopa, setEditRopa] = useState({ nombre: '', precio: '' });
  const [searchTerm, setSearchTerm] = useState('');
  const [sortOrder, setSortOrder] = useState('asc');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  useEffect(() => {
    fetchRopaList();
  }, []);

  const fetchRopaList = async () => {
    try {
      const data = await getAllRopa();
      setRopaList(data);
    } catch (e) {
      console.error(e);
    }
  };

  const handleViewDetails = async (idRopa) => {
    try {
      const ropa = await getRopaById(idRopa);
      setSelectedRopa(ropa);
      setEditRopa({ nombre: ropa.nombre, precio: ropa.precio });
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    if (selectedRopa) {
      console.log(`Prenda seleccionada: ID ${selectedRopa.idRopa}`);
    }
  }, [selectedRopa]);

  const handleEdit = async () => {
    if (!selectedRopa?.idRopa) return;
    try {
      const updated = await updateRopa(selectedRopa.idRopa, editRopa);
      setRopaList(prevList =>
        prevList.map(r => (r.idRopa === updated.idRopa ? updated : r))
      );
      setSelectedRopa(null);
      setEditRopa({ nombre: '', precio: '' });
      await MySwal.fire({
        icon: 'success',
        title: '¡Prenda actualizada!',
        text: `La prenda "${updated.nombre}" se actualizó correctamente.`,
        timer: 2000,
        showConfirmButton: false
      });
    } catch (e) {
      await MySwal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo actualizar la prenda.'
      });
    }
  };

  const handleDelete = async (idRopa) => {
    try {
      await deleteRopa(idRopa);
      setRopaList(prevList => prevList.filter(r => r.idRopa !== idRopa));
      await MySwal.fire({
        icon: 'success',
        title: '¡Prenda eliminada!',
        text: `La prenda ha sido eliminada.`,
        timer: 2000,
        showConfirmButton: false
      });
    } catch (e) {
      await MySwal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo eliminar la prenda.'
      });
    }
  };

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSort = () => {
    const sortedList = [...ropaList].sort((a, b) => {
      if (sortOrder === 'asc') {
        return a.nombre.localeCompare(b.nombre);
      } else {
        return b.nombre.localeCompare(a.nombre);
      }
    });
    setRopaList(sortedList);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  const filteredList = ropaList.filter(ropa =>
    ropa.nombre.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const paginatedList = filteredList.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const totalPages = Math.ceil(filteredList.length / itemsPerPage);

  const handleCreate = async () => {
    try {
      const created = await createRopa(newRopa);
      setRopaList(prevList => [...prevList, created]);
      setNewRopa({ nombre: '', precio: '' });
      await MySwal.fire({
        icon: 'success',
        title: '¡Prenda creada!',
        text: `La prenda "${created.nombre}" se agregó correctamente.`,
        timer: 2000,
        showConfirmButton: false
      });
    } catch (e) {
      await MySwal.fire({
        icon: 'error',
        title: 'Error',
        text: 'No se pudo crear la prenda.'
      });
    }
  };

  return (
    <div className="admin-container">
      <section>
        <h4>Crear Nueva Prenda</h4>
        <input
          placeholder="Nombre"
          value={newRopa.nombre}
          onChange={e => setNewRopa({ ...newRopa, nombre: e.target.value })}
        />
        <input
          placeholder="Precio"
          type="number"
          value={newRopa.precio}
          onChange={e => setNewRopa({ ...newRopa, precio: e.target.value })}
        />
        <button onClick={handleCreate}>Crear</button>
      </section>

      <section>
        <h4>Buscar y Ordenar</h4>
        <input
          placeholder="Buscar por nombre"
          value={searchTerm}
          onChange={handleSearch}
        />
        <button onClick={handleSort}>
          Ordenar {sortOrder === 'asc' ? 'Descendente' : 'Ascendente'}
        </button>
      </section>

      <section>
        <h4>Lista de Ropa</h4>
        <ul>
          {paginatedList.map(ropa => (
            <li key={ropa.idRopa}>
              {ropa.nombre} ${ropa.precio}
              <button onClick={() => handleViewDetails(ropa.idRopa)}>
                Ver detalles
              </button>
              <button onClick={() => handleDelete(ropa.idRopa)}>
                Eliminar
              </button>
            </li>
          ))}
        </ul>
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, index) => (
            <button
              key={index}
              onClick={() => setCurrentPage(index + 1)}
              className={currentPage === index + 1 ? 'active' : ''}
            >
              {index + 1}
            </button>
          ))}
        </div>
      </section>

      {selectedRopa && (
        <section className="edit-section">
          <h4>Editar Prenda (ID: {selectedRopa.idRopa})</h4>
          <div className="edit-form">
            <input
              placeholder="Nombre"
              value={editRopa.nombre}
              onChange={e => setEditRopa({ ...editRopa, nombre: e.target.value })}
            />
            <input
              placeholder="Precio"
              type="number"
              value={editRopa.precio}
              onChange={e => setEditRopa({ ...editRopa, precio: e.target.value })}
            />
            <button onClick={handleEdit}>Actualizar</button>
          </div>
        </section>
      )}
    </div>
  );
}
