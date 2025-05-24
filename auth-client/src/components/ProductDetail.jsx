import React, { useState, useEffect, useContext, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Rating } from 'react-simple-star-rating';
import ReactPaginate from 'react-paginate';

import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
// Importa la nueva función para obtener reseñas paginadas por producto
import { crearResena, obtenerResenasPorProductoPaginadas } from '../services/resenaService';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import MySwal from '../utils/swal';
import '../style/ProductDetail.modules.css';

export default function ProductDetail() {
  const { idProducto } = useParams();
  const [producto, setProducto] = useState(null);
  // displayedResenas ahora se poblará directamente de la respuesta paginada del backend
  const [displayedResenas, setDisplayedResenas] = useState([]);
  const [form, setForm] = useState({ comentario: '', rating: 0 });
  const [cantidad, setCantidad] = useState(1);
  const [loading, setLoading] = useState(true);
  const [reviewsLoading, setReviewsLoading] = useState(true); // Nuevo estado para la carga de reseñas

  // Estados para la paginación (ahora controlados por el backend)
  const [currentPage, setCurrentPage] = useState(0); // Página actual, inicia en 0
  const [reviewsPerPage] = useState(5); // Reseñas a mostrar por página (tamaño de página del backend)
  const [pageCount, setPageCount] = useState(0); // Total de páginas para las reseñas del producto, viene del backend

  const { carrito } = useContext(CartContext);
  const { userData } = useContext(AuthContext);
  const navigate = useNavigate();

  // Esta función ahora solo llama al servicio de backend para obtener reseñas paginadas por producto
  const fetchProductResenas = useCallback(async () => {
    try {
      setReviewsLoading(true); // Inicia la carga de reseñas

      const resenasData = await obtenerResenasPorProductoPaginadas(
        parseInt(idProducto, 10),
        currentPage,
        reviewsPerPage
      );

      // Los datos vienen directamente del PagedModel de HATEOAS
      setDisplayedResenas(resenasData._embedded?.resenaResponseList || []);
      setPageCount(resenasData.page?.totalPages || 0);
    } catch (error) {
      console.error('Error al cargar reseñas del producto:', error);
      MySwal.fire('Error', 'No se pudieron cargar las reseñas del producto.', 'error');
      setDisplayedResenas([]);
      setPageCount(0);
    } finally {
      setReviewsLoading(false); // Finaliza la carga de reseñas
    }
  }, [idProducto, currentPage, reviewsPerPage]); // Dependencias de useCallback

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true); // Inicia la carga general (producto y reseñas)
        const productos = await getAllProductos();
        const prod = productos.find((p) => p.idProducto === parseInt(idProducto, 10));
        if (!prod) {
          throw new Error('Producto no encontrado');
        }
        setProducto(prod);
      } catch (error) {
        console.error('Error al cargar producto:', error);
        MySwal.fire('Error', 'No se pudo cargar el producto.', 'error');
      } finally {
        setLoading(false); // Finaliza la carga general del producto
      }
    };
    fetchData();
  }, [idProducto]);

  // Nuevo useEffect para manejar la carga de reseñas, separado del producto para mayor claridad
  useEffect(() => {
    if (idProducto) { // Solo si tenemos un idProducto válido
      fetchProductResenas();
    }
  }, [idProducto, currentPage, fetchProductResenas]); // Recarga reseñas cuando cambian idProducto o currentPage

  const handleAddToCart = async () => {
    try {
      if (!carrito?.idCarrito) {
        await MySwal.fire('Error', 'Carrito no disponible.', 'error');
        return;
      }
      await addCarritoItem({
        idCarrito: carrito.idCarrito,
        idProducto: producto.idProducto,
        cantidad,
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
      if (!producto?.idProducto) throw new Error('Producto no disponible para reseña');

      await crearResena({
        idProducto: producto.idProducto,
        comentario: form.comentario,
        rating: form.rating,
      });
      await MySwal.fire('Reseña enviada', 'Tu reseña ha sido publicada.', 'success');
      setForm({ comentario: '', rating: 0 });
      // Después de crear una reseña, vuelve a cargar las reseñas desde la primera página
      setCurrentPage(0);
      await fetchProductResenas(); // Llama a la función para refrescar la lista de reseñas
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

  const handlePageClick = (data) => {
    // data.selected es el índice de la página (0-based)
    setCurrentPage(data.selected);
  };

  if (loading && !producto) {
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
              allowHalfIcon={false}
              transition
              fillColor={userData ? 'var(--primary-500)' : 'var(--surface-400)'}
              emptyColor="var(--surface-200)"
              readonly={!userData}
            />
            {!userData && <p className="auth-message-rating">Inicia sesión para dejar una reseña.</p>}
          </div>

          <button type="submit" className="add-to-cart-btn" disabled={!userData || form.rating === 0 || form.comentario.trim() === ''}>
            Publicar reseña
          </button>
        </form>

        <div className="reviews-list">
          {reviewsLoading ? ( // Usa el nuevo estado de carga para reseñas
            <p className="loading-message">Cargando reseñas...</p>
          ) : displayedResenas.length === 0 ? (
            <p className="no-reviews">Sé el primero en dejar una reseña.</p>
          ) : (
            displayedResenas.map((resena, index) => {
              const ratings = {
                ONE: 1,
                TWO: 2,
                THREE: 3,
                FOUR: 4,
                FIVE: 5,
              };
              const ratingValue = ratings[resena.rating] || 0;

              return (
                <article key={index} className="review-item">
                  <div className="review-header">
                    <span className="review-author">{resena.username || 'Anónimo'}</span>
                    <div className="review-rating">
                      <Rating
                        readonly
                        size={20}
                        initialValue={ratingValue}
                        allowHalfIcon={false}
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

        {/* Componente de paginación */}
        {pageCount > 1 && (
          <ReactPaginate
            previousLabel={'Anterior'}
            nextLabel={'Siguiente'}
            breakLabel={'...'}
            pageCount={pageCount}
            marginPagesDisplayed={2}
            pageRangeDisplayed={3}
            onPageChange={handlePageClick}
            containerClassName={'pagination'}
            activeClassName={'active'}
            forcePage={currentPage} // Fuerza a ReactPaginate a usar la página actual
          />
        )}
      </section>
    </div>
  );
}