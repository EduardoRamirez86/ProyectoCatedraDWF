import React, { createContext, useState, useEffect, useContext } from 'react';
import {jwtDecode} from 'jwt-decode';
import { secureGetItem, secureSetItem, secureRemoveItem } from '../utils/secureStorage';
import UserContext from '../context/UserContext';

export const AuthContext = createContext({
  token: null,
  userData: null,
  login: () => {},
  logout: () => {},
});

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => secureGetItem('token') || null);
  const [userData, setUserData] = useState(() => {
    const storedToken = secureGetItem('token');
    return storedToken ? jwtDecode(storedToken) : null;
  });

  // Solo extraemos la función que necesitamos y que es referencialmente estable
  const { setUserId } = useContext(UserContext) || {};

  useEffect(() => {
    const handleStorageUpdate = () => {
      const newToken = secureGetItem('token');
      if (newToken !== token) {
        setToken(newToken);
        const decoded = newToken ? jwtDecode(newToken) : null;
        setUserData(decoded);

        if (decoded?.userId) {
          setUserId(decoded.userId);
          secureSetItem('userId', decoded.userId.toString());
        }
      }
    };

    window.addEventListener('storage', handleStorageUpdate);
    return () => window.removeEventListener('storage', handleStorageUpdate);
  }, [token, setUserId]); // <-- aquí termina el hook sin nada más debajo

  const login = (newToken) => {
    secureSetItem('token', newToken);
    const decoded = jwtDecode(newToken);
    setToken(newToken);
    setUserData(decoded);
    if (decoded.userId) {
      setUserId(decoded.userId);
      secureSetItem('userId', decoded.userId.toString());
    }
  };

  const logout = () => {
    secureRemoveItem('token');
    setToken(null);
    setUserData(null);
    setUserId(null);
    secureRemoveItem('userId');
  };

  return (
    <AuthContext.Provider value={{ token, userData, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
