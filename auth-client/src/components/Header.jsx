import React, { useContext, useEffect, useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { AuthContext } from "../context/AuthContext";
import { CartContext } from "../context/CartContext";
import { getUserNotifications, markNotificationRead } from "../services/notificationService";
import { Bell, User } from "react-feather";
import '../style/Header.css';

export default function Header() {
  const { token, userData, logout } = useContext(AuthContext);
  const { carrito } = useContext(CartContext);
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [notifOpen, setNotifOpen] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
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
      navigate("/user");
    } else if (action === "admin") {
      navigate("/admin");
    }
  };

  const isUser = userData?.roles?.includes('ROLE_USER');

  return (
    <motion.header
      className="header"
      initial={{ y: -60, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.6 }}
    >
      <div className="header__brand">
        <Link to="/">MiApp</Link>
      </div>
      <nav className="header__nav">
        {!token ? (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        ) : (
          <>
            <div className="notification-wrapper" ref={notifRef}>
              <button
                className="notification-btn"
                onClick={() => setNotifOpen(o => !o)}
              >
                <Bell size={20} />
                {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
              </button>
              {notifOpen && (
                <div className="notification-dropdown">
                  <div className="notif-tabs">
                    <button
                      className={tab === "no-leidas" ? "active" : ""}
                      onClick={() => setTab("no-leidas")}
                    >
                      No leídas
                    </button>
                    <button
                      className={tab === "todas" ? "active" : ""}
                      onClick={() => setTab("todas")}
                    >
                      Todas
                    </button>
                  </div>
                  <div className="notif-list">
                    {filtered.length === 0 ? (
                      <p className="no-notifs">No hay notificaciones</p>
                    ) : (
                      filtered.map(n => (
                        <div
                          key={n.id}
                          className={`notif-item ${
                            n.estado === "ENVIADA" ? "unread" : "read"
                          }`}
                          onClick={() => handleMarkRead(n.id)}
                        >
                          <p>{n.mensaje}</p>
                          <small>
                            {new Date(n.fechaEnvio).toLocaleString()}
                          </small>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            {isUser && (
              <button
                className="cart-icon"
                onClick={() => navigate('/user/cart')}
                title="Ver Carrito"
              >
                🛒
                {carrito?.items?.length > 0 && (
                  <span className="cart-count">{carrito.items.length}</span>
                )}
              </button>
            )}

            <div className="user-menu-wrapper" ref={menuRef}>
              <button
                className="user-menu-btn"
                onClick={() => setMenuOpen(m => !m)}
              >
                <User size={24} />
              </button>
              {menuOpen && (
                <ul className="user-menu-dropdown">
                  {userData.roles.includes("ROLE_USER") && (
                    <li onClick={() => handleAction("user")}>Mi Perfil</li>
                  )}
                  {userData.roles.includes("ROLE_ADMIN") && (
                    <li onClick={() => handleAction("admin")}>Admin Panel</li>
                  )}
                  <li onClick={() => handleAction("logout")}>Cerrar Sesión</li>
                </ul>
              )}
            </div>
          </>
        )}
      </nav>
    </motion.header>
  );
}