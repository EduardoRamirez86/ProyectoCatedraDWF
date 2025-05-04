import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Rating } from 'react-simple-star-rating';
import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import { crearResena, obtenerResenasPorProducto } from '../services/resenaService';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import MySwal from '../utils/swal';
import '../style/ProductDetail.css';

export default function ProductDetail() {
  const { idProducto } = useParams();
  const [producto, setProducto] = useState(null);
  const [resenas, setResenas] = useState([]);
  const [form, setForm] = useState({ comentario: '', rating: 0 });
  const { carrito } = useContext(CartContext);
  const { userData } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProductoYResenas = async () => {
      try {
        const productos = await getAllProductos();
        const prod = productos.find((p) => p.idProducto === parseInt(idProducto, 10));
        if (!prod) throw new Error('Producto no encontrado');
        setProducto(prod);

        const resenasData = await obtenerResenasPorProducto(idProducto);
        setResenas(resenasData);
      } catch (error) {
        console.error('Error al cargar producto o reseñas:', error);
        MySwal.fire('Error', 'No se pudo cargar el producto o las reseñas.', 'error');
      }
    };
    fetchProductoYResenas();
  }, [idProducto]);

  const handleAddToCart = async () => {
    try {
      if (!carrito) {
        await MySwal.fire('Error', 'Carrito no disponible.', 'error');
        return;
      }
      await addCarritoItem({ idCarrito: carrito.idCarrito, idProducto: producto.idProducto, cantidad: 1 });
      await MySwal.fire('Agregado', `"${producto.nombre}" añadido al carrito.`, 'success');
    } catch (error) {
      console.error('Error al añadir al carrito:', error);
      await MySwal.fire('Error', 'No se pudo añadir al carrito.', 'error');
    }
  };

  const handleResenaSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!userData) throw new Error('Usuario no autenticado');
      await crearResena({
        idProducto: producto.idProducto,
        comentario: form.comentario,
        rating: form.rating,
      });
      await MySwal.fire('Reseña enviada', 'Tu reseña ha sido publicada.', 'success');
      setForm({ comentario: '', rating: 0 });
      const resenasData = await obtenerResenasPorProducto(idProducto);
      setResenas(resenasData);
    } catch (error) {
      console.error('Error al enviar reseña:', error);
      await MySwal.fire('Error', 'No se pudo enviar la reseña.', 'error');
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRatingChange = (newRating) => {
    setForm({ ...form, rating: newRating });
  };

  if (!producto) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="product-detail-container">
      <button className="back-btn" onClick={() => navigate('/user')}>
        ← Volver
      </button>
      <div className="product-detail">
        <img src={producto.imagen} alt={producto.nombre} className="product-detail-image" />
        <div className="product-info">
          <h2>{producto.nombre}</h2>
          <p className="product-description">{producto.descripcion}</p>
          <p className="product-price">${producto.precio.toFixed(2)}</p>
          <button className="add-to-cart-btn" onClick={handleAddToCart}>
            Añadir al carrito
          </button>
        </div>
      </div>

      <div className="resena-section">
        <h3>Reseñas</h3>
        <form onSubmit={handleResenaSubmit} className="resena-form">
          <textarea
            name="comentario"
            value={form.comentario}
            onChange={handleChange}
            placeholder="Escribe tu comentario..."
            required
          />
          <div className="rating-container">
            <Rating
              onClick={handleRatingChange}
              ratingValue={form.rating}
              size={24}
              allowHalfIcon
            />
          </div>
          <button type="submit">Enviar reseña</button>
        </form>

        <div className="resenas-list">
          {resenas.length === 0 ? (
            <p>No hay reseñas para este producto.</p>
          ) : (
            resenas.map((resena, index) => (
              <div key={index} className="resena-item">
                <p><strong>Usuario:</strong> {resena.username || 'Anónimo'}</p>
                <p><strong>Rating:</strong> {resena.rating} estrellas</p>
                <p>{resena.comentario}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}