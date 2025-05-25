import React, { useState, useEffect, useContext, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ReactPaginate from 'react-paginate';

// Importaciones de Material-UI
import Rating from '@mui/material/Rating';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography'; // Ahora lo usaremos
import StarIcon from '@mui/icons-material/Star';

import { getAllProductos } from '../services/productoService';
import { addCarritoItem } from '../services/carritoItemService';
import { crearResena, obtenerResenasPorProductoPaginadas } from '../services/resenaService';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import MySwal from '../utils/swal';

// Labels para el rating con hover (Material-UI)
const labels = {
  0.5: 'Terrible',
  1: 'Malo',
  1.5: 'Regular',
  2: 'Aceptable',
  2.5: 'Bueno',
  3: 'Muy Bueno',
  3.5: 'Genial',
  4: 'Excelente',
  4.5: 'Impresionante',
  5: 'Perfecto',
};

// Función helper para obtener el texto del label (Material-UI)
function getLabelText(value) {
  return `${value} Estrella${value !== 1 ? 's' : ''}, ${labels[value]}`;
}

export default function ProductDetail() {
  const { idProducto } = useParams();
  const [producto, setProducto] = useState(null);
  const [displayedResenas, setDisplayedResenas] = useState([]);
  const [form, setForm] = useState({ comentario: '', rating: 0 });
  const [cantidad, setCantidad] = useState(1);
  const [loading, setLoading] = useState(true);
  const [reviewsLoading, setReviewsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [reviewsPerPage] = useState(5);
  const [pageCount, setPageCount] = useState(0);
  const [hoverRating, setHoverRating] = useState(-1);

  const { carrito } = useContext(CartContext);
  const { userData } = useContext(AuthContext);
  const navigate = useNavigate();

  // Fetches product details
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const productos = await getAllProductos();
        const prod = Array.isArray(productos)
          ? productos.find((p) => p.idProducto === parseInt(idProducto, 10))
          : productos._embedded?.productoResponseList?.find((p) => p.idProducto === parseInt(idProducto, 10));
        if (!prod) throw new Error('Producto no encontrado');
        setProducto(prod);
      } catch (error) {
        console.error('Error al cargar producto:', error);
        MySwal.fire('Error', 'No se pudo cargar el producto.', 'error');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [idProducto]);

  // Fetches product reviews
  const fetchProductResenas = useCallback(async () => {
    try {
      setReviewsLoading(true);
      const resenasData = await obtenerResenasPorProductoPaginadas(
        parseInt(idProducto, 10),
        currentPage,
        reviewsPerPage
      );
      setDisplayedResenas(resenasData._embedded?.resenaResponseList || []);
      setPageCount(resenasData.page?.totalPages || 0);
    } catch (error) {
      console.error('Error al cargar reseñas del producto:', error);
      MySwal.fire('Error', 'No se pudieron cargar las reseñas del producto.', 'error');
      setDisplayedResenas([]);
      setPageCount(0);
    } finally {
      setReviewsLoading(false);
    }
  }, [idProducto, currentPage, reviewsPerPage]);

  useEffect(() => {
    if (idProducto) {
      fetchProductResenas();
    }
  }, [idProducto, currentPage, fetchProductResenas]);

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
      if (form.rating === 0) {
        MySwal.fire('Advertencia', 'Por favor, selecciona una calificación.', 'warning');
        return;
      }

      await crearResena({
        idProducto: producto.idProducto,
        comentario: form.comentario,
        rating: form.rating,
      });
      await MySwal.fire('Reseña enviada', 'Tu reseña ha sido publicada.', 'success');
      setForm({ comentario: '', rating: 0 });
      setHoverRating(-1);
      setCurrentPage(0);
      await fetchProductResenas();
    } catch (error) {
      console.error('Error al enviar reseña:', error);
      await MySwal.fire('Error', 'No se pudo enviar la reseña.', 'error');
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRatingChange = (event, newValue) => {
    setForm({ ...form, rating: newValue });
  };

  const handleIncrease = () => {
    setCantidad(prev => Math.min(prev + 1, 99));
  };

  const handleDecrease = () => {
    setCantidad(prev => Math.max(prev - 1, 1));
  };

  const handlePageClick = (data) => {
    setCurrentPage(data.selected);
  };

  // Helper para mapear el RatingEnum del backend a un número para Material-UI (0-5)
  const getRatingNumber = useCallback((ratingEnum) => {
    const ratingsMap = {
      'ONE': 1,
      'TWO': 2,
      'THREE': 3,
      'FOUR': 4,
      'FIVE': 5,
    };
    return ratingsMap[ratingEnum] || parseFloat(ratingEnum) || 0;
  }, []);

  if (loading && !producto) {
    return (
      <Box className="container mx-auto py-10">
        <Typography variant="h6" component="p" className="text-center text-lg text-gray-500">
          Cargando producto...
        </Typography>
      </Box>
    );
  }

  if (!producto) {
    return (
      <Box className="container mx-auto py-10">
        <Typography variant="h6" component="p" className="text-center text-lg text-red-500">
          Producto no encontrado
        </Typography>
      </Box>
    );
  }

  return (
    <Box className="container mx-auto px-4 py-8">
      <button
        className="mb-6 text-indigo-600 hover:underline flex items-center gap-2"
        onClick={() => navigate(-1)}
      >
        <i className="fas fa-arrow-left"></i> Volver
      </button>

      <Box className="flex flex-col md:flex-row gap-8 bg-white shadow-lg rounded-xl p-8">
        <Box className="flex-1 flex justify-center items-center">
          <img
            src={producto.imagen}
            alt={producto.nombre}
            className="rounded-lg object-cover max-h-96 w-full max-w-xs shadow"
            loading="lazy"
          />
        </Box>
        <Box className="flex-1 flex flex-col justify-between">
          <Box>
            <Typography variant="h4" component="h1" className="text-3xl font-bold text-indigo-800 mb-2">
              {producto.nombre}
            </Typography>
            <Typography variant="body1" component="p" className="text-gray-600 mb-4">
              {producto.descripcion}
            </Typography>
            <Typography variant="h5" component="p" className="text-2xl font-bold text-indigo-600 mb-4">
              ${producto.precio.toFixed(2)}
            </Typography>
            <Box className="flex items-center gap-4 mb-4">
              <Typography variant="subtitle1" component="span" className="text-gray-700 font-medium">
                Stock:
              </Typography>
              <Typography variant="body1" component="span" className="text-gray-900">
                {producto.cantidad}
              </Typography>
              <Typography variant="caption" component="span" className="ml-4 px-3 py-1 rounded-full bg-indigo-100 text-indigo-700 font-semibold">
                {producto.nombreTipo}
              </Typography>
            </Box>
            <Box className="flex items-center gap-4 mb-6">
              <Typography variant="subtitle1" component="span" className="text-gray-700 font-medium">
                Cantidad:
              </Typography>
              <button
                type="button"
                className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
                onClick={handleDecrease}
                disabled={cantidad <= 1}
              >
                -
              </button>
              <Typography variant="body1" component="span" className="font-semibold">
                {cantidad}
              </Typography>
              <button
                type="button"
                className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
                onClick={handleIncrease}
                disabled={cantidad >= 99}
              >
                +
              </button>
            </Box>
            <button
              className="w-full py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition mb-4"
              onClick={handleAddToCart}
              disabled={!carrito}
            >
              Añadir al carrito
            </button>
          </Box>
        </Box>
      </Box>

      {/* Reseñas */}
      <Box component="section" className="mt-12">
        <Typography variant="h5" component="h2" className="text-2xl font-bold text-indigo-700 mb-4 flex items-center gap-2">
          <StarIcon sx={{ color: 'text.yellow-400', mr: 1 }} /> {/* Usa StarIcon de MUI */}
          Reseñas
        </Typography>
        <Box component="form" onSubmit={handleResenaSubmit} className="bg-indigo-50 rounded-lg p-6 mb-8 shadow flex flex-col gap-4">
          <textarea
            name="comentario"
            value={form.comentario}
            onChange={handleChange}
            placeholder="Escribe tu reseña..."
            required
            rows="3"
            className="w-full border border-indigo-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          />
          <Box className="flex items-center gap-4">
            <Typography variant="subtitle1" component="span" className="text-gray-700 font-medium">
              Calificación:
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Rating
                name="product-review-rating"
                value={form.rating}
                precision={0.5}
                getLabelText={getLabelText}
                onChange={handleRatingChange}
                onChangeActive={(event, newHover) => {
                  setHoverRating(newHover);
                }}
                emptyIcon={<StarIcon style={{ opacity: 0.55 }} fontSize="inherit" />}
                readOnly={!userData}
              />
              {userData && form.rating !== null && (
                <Box sx={{ ml: 2, color: 'text.secondary' }}>
                  <Typography variant="body2" component="span">
                    {labels[hoverRating !== -1 ? hoverRating : form.rating]}
                  </Typography>
                </Box>
              )}
            </Box>
            {!userData && (
              <Typography variant="caption" component="span" className="text-sm text-gray-500 ml-2">
                Inicia sesión para dejar una reseña.
              </Typography>
            )}
          </Box>
          <button
            type="submit"
            className="w-full py-2 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition"
            disabled={!userData || form.rating === 0 || form.comentario.trim() === ''}
          >
            Publicar reseña
          </button>
        </Box>
        <Box className="bg-white rounded-lg shadow p-6">
          {reviewsLoading ? (
            <Typography variant="body1" component="p" className="text-center text-gray-500">
              Cargando reseñas...
            </Typography>
          ) : displayedResenas.length === 0 ? (
            <Typography variant="body1" component="p" className="text-center text-gray-500">
              Sé el primero en dejar una reseña.
            </Typography>
          ) : (
            <Box component="ul" className="space-y-6">
              {displayedResenas.map((resena, index) => (
                <Box component="li" key={index} className="border-b pb-4 last:border-b-0 last:pb-0">
                  <Box className="flex items-center gap-3 mb-1">
                    <Typography variant="subtitle1" component="span" className="font-semibold text-indigo-700">
                      {resena.username || 'Anónimo'}
                    </Typography>
                    <Box className="flex flex-row items-center">
                      <Rating
                        name={`read-only-rating-${resena.idResena}`}
                        value={getRatingNumber(resena.rating)}
                        readOnly
                        precision={0.5}
                        emptyIcon={<StarIcon style={{ opacity: 0.55 }} fontSize="inherit" />}
                      />
                    </Box>
                  </Box>
                  <Typography variant="body2" component="p" className="text-gray-700">
                    {resena.comentario}
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
          {pageCount > 1 && (
            <Box className="mt-8 flex justify-center">
              <ReactPaginate
                previousLabel={'Anterior'}
                nextLabel={'Siguiente'}
                breakLabel={'...'}
                pageCount={pageCount}
                marginPagesDisplayed={2}
                pageRangeDisplayed={3}
                onPageChange={handlePageClick}
                containerClassName={'flex gap-2'}
                pageClassName={'px-3 py-1 rounded border border-gray-300'}
                activeClassName={'bg-indigo-600 text-white'}
                previousClassName={'px-3 py-1 rounded border border-gray-300'}
                nextClassName={'px-3 py-1 rounded border border-gray-300'}
                breakClassName={'px-3 py-1'}
                forcePage={currentPage}
              />
            </Box>
          )}
        </Box>
      </Box>
    </Box>
  );
}