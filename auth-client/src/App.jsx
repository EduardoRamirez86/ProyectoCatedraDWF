import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
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
import Cart from './components/Cart'; // Add this import

function PrivateRoute({ requiredRole, children }) {
  const { token, userData } = useContext(AuthContext);
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  // userData.roles puede ser string o array
  const roles = Array.isArray(userData?.roles) ? userData.roles : [userData?.roles];
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
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route element={<Layout />}>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
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
                path="/admin"
                element={
                  <PrivateRoute requiredRole="ROLE_ADMIN">
                    <AdminPage />
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
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  );
}