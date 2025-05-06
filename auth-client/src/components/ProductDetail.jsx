import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Rating } from 'react-simple-star-rating'; // O usa tu propio componente StarRating
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
  const [cantidad, setCantidad] = useState(1);
  const [loading, setLoading] = useState(true);
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
      } finally {
        setLoading(false);
      }
    };
    fetchProductoYResenas();
  }, [idProducto]);

  const handleAddToCart = async () => {
    try {
      if (!carrito?.idCarrito) {
        await MySwal.fire('Error', 'Carrito no disponible.', 'error');
        return;
      }
      await addCarritoItem({ 
        idCarrito: carrito.idCarrito, 
        idProducto: producto.idProducto, 
        cantidad 
      });
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

  const handleIncrease = () => {
    setCantidad(prev => Math.min(prev + 1, 99));
  };

  const handleDecrease = () => {
    setCantidad(prev => Math.max(prev - 1, 1));
  };

  if (loading) {
    return (
      <div className="container">
        <div className="loading-message">Cargando producto...</div>
      </div>
    );
  }

  if (!producto) {
    return (
      <div className="container">
        <div className="error-message">Producto no encontrado</div>
      </div>
    );
  }

  return (
    <div className="product-detail-container surface">
      <button className="back-btn" onClick={() => navigate(-1)}>
        ← Volver
      </button>

      <div className="product-detail">
        <img 
          src={producto.imagen} 
          alt={producto.nombre} 
          className="product-detail-image"
          loading="lazy"
        />

        <div className="product-info">
          <h1>{producto.nombre}</h1>
          <p className="product-description">{producto.descripcion}</p>
          <p className="product-price">${producto.precio.toFixed(2)}</p>

          <div className="quantity-container">
            <button 
              type="button" 
              className="quantity-btn" 
              onClick={handleDecrease}
              disabled={cantidad <= 1}
            >
              -
            </button>
            <span className="quantity-display">{cantidad}</span>
            <button 
              type="button" 
              className="quantity-btn" 
              onClick={handleIncrease}
              disabled={cantidad >= 99}
            >
              +
            </button>
          </div>

          <button 
            className="add-to-cart-btn"
            onClick={handleAddToCart}
            disabled={!carrito}
          >
            Añadir al carrito
          </button>
        </div>
      </div>

      <section className="reviews-section">
        <h2>Reseñas</h2>

        <form onSubmit={handleResenaSubmit} className="resena-form">
          <textarea
            name="comentario"
            value={form.comentario}
            onChange={handleChange}
            placeholder="Escribe tu reseña..."
            required
            rows="4"
          />

          <div className="rating-container">
            <Rating
              onClick={handleRatingChange}
              initialValue={form.rating}
              size={28}
              allowHalfIcon
              transition
              fillColor={userData ? 'var(--primary-500)' : 'var(--surface-400)'}
              emptyColor="var(--surface-200)"
              readonly={!userData}
            />
          </div>

          <button 
            type="submit" 
            className="add-to-cart-btn"
            disabled={!userData}
          >
            Publicar reseña
          </button>
        </form>

        <div className="reviews-list">
          {resenas.length === 0 ? (
            <p className="no-reviews">No hay reseñas disponibles</p>
          ) : (
            resenas.map((resena, index) => {
              const ratingString = resena.rating;
              let ratingValue = 0;

              // Mapeo de ratingString a ratingValue
              switch (ratingString) {
                case 'ONE':
                  ratingValue = 1;
                  break;
                case 'TWO':
                  ratingValue = 2;
                  break;
                case 'THREE':
                  ratingValue = 3;
                  break;
                case 'FOUR':
                  ratingValue = 4;
                  break;
                case 'FIVE':
                  ratingValue = 5;
                  break;
                default:
                  ratingValue = 0;
                  break;
              }

              return (
                <article key={index} className="review-item">
                  <div className="review-header">
                    <span className="review-author">{resena.username || 'Anónimo'}</span>
                    <div className="review-rating">
                      <Rating
                        readonly
                        size={20}
                        initialValue={ratingValue}
                        allowHalfIcon
                        fillColor="var(--primary-500)"
                        emptyColor="var(--surface-300)"
                      />
                    </div>
                  </div>
                  <p className="review-content">{resena.comentario}</p>
                </article>
              );
            })
          )}
        </div>
      </section>
    </div>
  );
}
