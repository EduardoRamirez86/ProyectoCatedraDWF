import React, { useContext, useEffect, useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { CartContext } from "../context/CartContext";
import { getUserNotifications, markNotificationRead } from "../services/notificationService";

export default function Header() {
  const { token, userData, logout } = useContext(AuthContext);
  const { carrito } = useContext(CartContext);
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [notifOpen, setNotifOpen] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [tab, setTab] = useState("no-leidas");
  const notifRef = useRef();
  const menuRef = useRef();
  const userId = userData?.userId;

  useEffect(() => {
    if (token && userId) {
      getUserNotifications(userId).then(setNotifications).catch(console.error);
    }
  }, [token, userId]);

  useEffect(() => {
    const handleClickOutside = e => {
      if (notifRef.current && !notifRef.current.contains(e.target)) {
        setNotifOpen(false);
      }
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const unreadCount = notifications.filter(n => n.estado === "ENVIADA").length;
  const filtered = tab === "todas"
    ? notifications
    : notifications.filter(n => n.estado === "ENVIADA");

  const handleMarkRead = async id => {
    try {
      await markNotificationRead(id);
      setNotifications(prev =>
        prev.map(n => n.id === id ? { ...n, estado: "LEIDA" } : n)
      );
    } catch (e) {
      console.error(e);
    }
  };

  const handleAction = action => {
    setMenuOpen(false);
    if (action === "logout") {
      logout();
      navigate("/login", { replace: true });
    } else if (action === "user") {
      navigate("/profile"); // Cambiado de "/user" a "/profile"
    } else if (action === "admin") {
      navigate("/admin");
    }
  };

  const isUser = userData?.roles?.includes('ROLE_USER');
  const isAdmin = userData?.roles?.includes('ROLE_ADMIN');

  const handleLogoClick = () => {
    if (isUser) {
      navigate('/user');
    } else if (isAdmin) {
      navigate('/admin');
    } else {
      navigate('/');
    }
  };

  // Oculta iconos en mobile, los muestra en el menú hamburguesa
  return (
    <header
      className="sticky top-0 z-50 bg-white shadow-sm"
      style={{
        color: "#fff",
        boxShadow: "0 2px 10px rgba(0,0,0,0.1)"
      }}
    >
      <div className="container mx-auto px-4 py-3 flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <button
            onClick={handleLogoClick}
            className="flex items-center space-x-2 font-bold text-xl text-gray-800 bg-transparent border-none cursor-pointer"
            style={{ background: "none" }}
          >
            <i className="fas fa-bolt text-indigo-600 text-2xl"></i>
            <h1>
              Tienda<span className="text-indigo-600">Ecommerce</span>
            </h1>
          </button>
        </div>

        {/* Iconos solo en desktop */}
        <div className="hidden md:flex items-center space-x-4">
          {!token ? (
            <>
              <Link to="/login" className="text-gray-700 hover:text-indigo-600 font-medium">Login</Link>
              <Link to="/register" className="text-gray-700 hover:text-indigo-600 font-medium">Register</Link>
            </>
          ) : (
            <>
              {/* Notificaciones */}
              
              <div className="relative" ref={notifRef}>
                <button
                  className="relative bg-transparent border-none cursor-pointer text-gray-700 hover:text-indigo-600"
                  onClick={() => setNotifOpen(o => !o)}
                  style={{ background: "none" }}
                >
                  <i className="fas fa-bell text-xl"></i>
                  {unreadCount > 0 && (
                    <span className="absolute -top-2 -right-2 bg-indigo-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">{unreadCount}</span>
                  )}
                </button>
                {notifOpen && (
                  <div className="absolute right-0 mt-2 w-72 bg-white border border-gray-200 rounded shadow-lg z-50">
                    <div className="flex border-b">
                      <button
                        className={`flex-1 py-2 font-bold ${tab === "no-leidas" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-gray-700"}`}
                        style={{ background: "none", border: "none", cursor: "pointer" }}
                        onClick={() => setTab("no-leidas")}
                      >
                        No leídas
                      </button>
                      <button
                        className={`flex-1 py-2 font-bold ${tab === "todas" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-gray-700"}`}
                        style={{ background: "none", border: "none", cursor: "pointer" }}
                        onClick={() => setTab("todas")}
                      >
                        Todas
                      </button>
                    </div>
                    <div className="max-h-60 overflow-y-auto p-2">
                      {filtered.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">No hay notificaciones</p>
                      ) : (
                        filtered.map(n => (
                          <div
                            key={n.id}
                            className={`p-2 rounded mb-1 cursor-pointer ${n.estado === "ENVIADA" ? "bg-indigo-50 font-bold" : "bg-gray-50"}`}
                            onClick={() => handleMarkRead(n.id)}
                          >
                            <p className="text-sm text-gray-800">{n.mensaje}</p>
                            <small className="text-xs text-gray-400">
                              {new Date(n.fechaEnvio).toLocaleString()}
                            </small>
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                )}
              </div>

              {/* Carrito */}
              {isUser && (
                <button
                  className="relative bg-transparent border-none cursor-pointer"
                  onClick={() => navigate('/user/cart')}
                  title="Ver Carrito"
                  style={{ background: "none" }}
                >
                  <i className="fas fa-shopping-cart text-gray-700 hover:text-indigo-600 text-xl"></i>
                  {carrito?.items?.length > 0 && (
                    <span className="absolute -top-2 -right-2 bg-indigo-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">{carrito.items.length}</span>
                  )}
                </button>
              )}

              {/* Menú usuario */}
              <div className="relative" ref={menuRef}>
                <button
                  className="bg-transparent border-none cursor-pointer"
                  onClick={() => setMenuOpen(m => !m)}
                  style={{ background: "none" }}
                >
                  <i className="fas fa-user text-gray-700 text-xl"></i>
                </button>
                {menuOpen && (
                  <ul className="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded shadow-lg z-50 list-none p-0">
                    {userData.roles.includes("ROLE_USER") && (
                      <li className="py-2 px-4 hover:bg-indigo-50 cursor-pointer text-gray-900 font-medium" onClick={() => handleAction("user")}>Mi Perfil</li>
                    )}
                    {userData.roles.includes("ROLE_ADMIN") && (
                      <li className="py-2 px-4 hover:bg-indigo-50 cursor-pointer text-gray-900 font-medium" onClick={() => handleAction("admin")}>Admin Panel</li>
                    )}
                    <li className="py-2 px-4 hover:bg-indigo-50 cursor-pointer text-gray-900 font-medium" onClick={() => handleAction("logout")}>Cerrar Sesión</li>
                  </ul>
                )}
              </div>
            </>
          )}

          {/* Menú hamburguesa móvil */}
          <button className="md:hidden bg-transparent border-none cursor-pointer" onClick={() => setMobileMenuOpen(o => !o)} style={{ background: "none" }}>
            <i className="fas fa-bars text-gray-700 text-xl"></i>
          </button>
        </div>

        {/* Solo botón hamburguesa en mobile */}
        <div className="flex md:hidden items-center">
          <button className="md:hidden bg-transparent border-none cursor-pointer" onClick={() => setMobileMenuOpen(o => !o)} style={{ background: "none" }}>
            <i className="fas fa-bars text-gray-700 text-xl"></i>
          </button>
        </div>
      </div>

      {/* Menú móvil */}
      {mobileMenuOpen && (
        <nav className="md:hidden bg-white border-t border-gray-200 px-4 py-3">
          <div className="flex flex-col space-y-2">
            {!token ? (
              <>
                <Link to="/login" className="text-gray-700 hover:text-indigo-600 font-medium">Login</Link>
                <Link to="/register" className="text-gray-700 hover:text-indigo-600 font-medium">Register</Link>
              </>
            ) : (
              <>
                {/* Notificaciones en menú móvil */}
                <button
                  className="flex items-center space-x-2 text-gray-700 hover:text-indigo-600 font-medium text-left bg-transparent border-none"
                  style={{ background: "none" }}
                  onClick={() => {
                    setMobileMenuOpen(false);
                    setNotifOpen(true);
                  }}
                >
                  <i className="fas fa-bell"></i>
                  <span>Notificaciones</span>
                  {unreadCount > 0 && (
                    <span className="ml-2 bg-indigo-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">{unreadCount}</span>
                  )}
                </button>
                {/* Carrito en menú móvil */}
                {isUser && (
                  <button
                    className="flex items-center space-x-2 text-gray-700 hover:text-indigo-600 font-medium text-left bg-transparent border-none"
                    style={{ background: "none" }}
                    onClick={() => {
                      setMobileMenuOpen(false);
                      navigate('/user/cart');
                    }}
                  >
                    <i className="fas fa-shopping-cart"></i>
                    <span>Carrito</span>
                    {carrito?.items?.length > 0 && (
                      <span className="ml-2 bg-indigo-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">{carrito.items.length}</span>
                    )}
                  </button>
                )}
                {/* Usuario en menú móvil */}
                {userData.roles.includes("ROLE_USER") && (
                  <button className="flex items-center space-x-2 text-gray-700 hover:text-indigo-600 font-medium text-left bg-transparent border-none" style={{ background: "none" }} onClick={() => { setMobileMenuOpen(false); handleAction("user"); }}>
                    <i className="fas fa-user"></i>
                    <span>Mi Perfil</span>
                  </button>
                )}
                {userData.roles.includes("ROLE_ADMIN") && (
                  <button className="flex items-center space-x-2 text-gray-700 hover:text-indigo-600 font-medium text-left bg-transparent border-none" style={{ background: "none" }} onClick={() => { setMobileMenuOpen(false); handleAction("admin"); }}>
                    <i className="fas fa-user"></i>
                    <span>Admin Panel</span>
                  </button>
                )}
                <button className="flex items-center space-x-2 text-gray-700 hover:text-indigo-600 font-medium text-left bg-transparent border-none" style={{ background: "none" }} onClick={() => { setMobileMenuOpen(false); handleAction("logout"); }}>
                  <i className="fas fa-sign-out-alt"></i>
                  <span>Cerrar Sesión</span>
                </button>
              </>
            )}
          </div>
        </nav>
      )}
      {/* Notificaciones dropdown en mobile */}
      {notifOpen && (
        <div className="md:hidden absolute right-4 mt-2 w-72 bg-white border border-gray-200 rounded shadow-lg z-50">
          <div className="flex border-b">
            <button
              className={`flex-1 py-2 font-bold ${tab === "no-leidas" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-gray-700"}`}
              style={{ background: "none", border: "none", cursor: "pointer" }}
              onClick={() => setTab("no-leidas")}
            >
              No leídas
            </button>
            <button
              className={`flex-1 py-2 font-bold ${tab === "todas" ? "text-indigo-600 border-b-2 border-indigo-600" : "text-gray-700"}`}
              style={{ background: "none", border: "none", cursor: "pointer" }}
              onClick={() => setTab("todas")}
            >
              Todas
            </button>
          </div>
          <div className="max-h-60 overflow-y-auto p-2">
            {filtered.length === 0 ? (
              <p className="text-gray-500 text-center py-4">No hay notificaciones</p>
            ) : (
              filtered.map(n => (
                <div
                  key={n.id}
                  className={`p-2 rounded mb-1 cursor-pointer ${n.estado === "ENVIADA" ? "bg-indigo-50 font-bold" : "bg-gray-50"}`}
                  onClick={() => handleMarkRead(n.id)}
                >
                  <p className="text-sm text-gray-800">{n.mensaje}</p>
                  <small className="text-xs text-gray-400">
                    {new Date(n.fechaEnvio).toLocaleString()}
                  </small>
                </div>
              ))
            )}
          </div>
          <button className="w-full py-2 text-center text-indigo-600 bg-transparent border-none" style={{ background: "none" }} onClick={() => setNotifOpen(false)}>Cerrar</button>
        </div>
      )}
    </header>
  );
}