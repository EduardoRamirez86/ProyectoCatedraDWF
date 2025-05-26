import React, { useContext } from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  Outlet,
  useLocation,
} from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Header from './components/Header';
import Footer from './components/Footer';
import Landing from './components/Landing';
import Login from './pages/Login';
import Register from './pages/Register';
import UserPage from './pages/UserPage';
import AdminPage from './pages/AdminPage';
import ProductDetail from './components/ProductDetail';
import Checkout from './components/Checkout';
import Cart from './components/Cart';
import Profile from './pages/Profile';
import OrderHistory from './components/OrderHistory';
import PointsHistory from './components/PointsHistory';
import UserOrders from './components/UserOrders';

// Páginas de Atención al Cliente y Acerca de
import ContactUs from './pages/ContactUs';
import FAQ from './pages/FAQ';
import ShippingPolicy from './pages/ShippingPolicy';
import ReturnsRefund from './pages/ReturnsRefund';
import AboutUs from './pages/AboutUs';
import Careers from './pages/Careers';
import Terms from './pages/Terms';
import Privacy from './pages/Privacy';
import Blog from './pages/Blog';

//Ver profile


function PrivateRoute({ requiredRole, children }) {
  const { token, userData } = useContext(AuthContext);
  if (!token) return <Navigate to="/login" replace />;
  const roles = Array.isArray(userData?.roles)
    ? userData.roles
    : [userData?.roles];

  // Permitir acceso a /admin tanto a ROLE_ADMIN como a ROLE_EMPLOYEE
  if (requiredRole === "ROLE_ADMIN") {
    if (roles.includes("ROLE_ADMIN") || roles.includes("ROLE_EMPLOYEE")) {
      return children;
    }
    return <Navigate to="/" replace />;
  }

  // Acceso normal para otros roles
  return roles.includes(requiredRole) ? children : <Navigate to="/" replace />;
}

function Layout() {
  const location = useLocation();
  const hideHeaderFooter = location.pathname === '/';
  return (
    <>
      {!hideHeaderFooter && <Header />}
      <main style={{ minHeight: 'calc(100vh - 160px)' }}>
        <Outlet />
      </main>
      {!hideHeaderFooter && <Footer />}
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Routes>
            {/* Landing sin Header/Footer */}
            <Route path="/" element={<Landing />} />

            {/* Rutas con Layout */}
            <Route element={<Layout />}>
              {/* Perfil y subrutas */}
              <Route path="/profile" element={<Profile />}>
                <Route index element={<></>} />
                <Route path="user-orders" element={<UserOrders />} />
                <Route path="puntos" element={<PointsHistory />} />
                <Route path="pedidos" element={<OrderHistory />} />
              </Route>

              {/* Atención al Cliente */}
              <Route path="/contact" element={<ContactUs />} />
              <Route path="/faq" element={<FAQ />} />
              <Route path="/shipping-policy" element={<ShippingPolicy />} />
              <Route path="/returns" element={<ReturnsRefund />} />

              {/* Acerca de */}
              <Route path="/about" element={<AboutUs />} />
              <Route path="/careers" element={<Careers />} />
              <Route path="/terms" element={<Terms />} />
              <Route path="/privacy" element={<Privacy />} />
              <Route path="/blog" element={<Blog />} />

              {/* Autenticación */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Privadas Usuario */}
              <Route
                path="/user"
                element={
                  <PrivateRoute requiredRole="ROLE_USER">
                    <UserPage />
                  </PrivateRoute>
                }
              />
              <Route
                path="/user/cart"
                element={
                  <PrivateRoute requiredRole="ROLE_USER">
                    <Cart />
                  </PrivateRoute>
                }
              />
              <Route
                path="/user/checkout"
                element={
                  <PrivateRoute requiredRole="ROLE_USER">
                    <Checkout />
                  </PrivateRoute>
                }
              />
              <Route
                path="/producto/:idProducto"
                element={
                  <PrivateRoute requiredRole="ROLE_USER">
                    <ProductDetail />
                  </PrivateRoute>
                }
              />

              {/* Privada Admin */}
              <Route
                path="/admin"
                element={
                  <PrivateRoute requiredRole="ROLE_ADMIN">
                    <AdminPage />
                  </PrivateRoute>
                }
              />

              {/* Catch-all */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  );
}