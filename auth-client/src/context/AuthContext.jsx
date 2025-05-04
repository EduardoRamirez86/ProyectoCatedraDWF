import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext({
  token: null,
  userData: null,
  login: () => {},
  logout: () => {},
});

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    return localStorage.getItem('token');
  });
  
  const [userData, setUserData] = useState(() => {
    const storedToken = localStorage.getItem('token');
    return storedToken ? jwtDecode(storedToken) : null;
  });

  useEffect(() => {
    const handleStorageUpdate = () => {
      const newToken = localStorage.getItem('token');
      if (newToken !== token) {
        setToken(newToken);
        setUserData(newToken ? jwtDecode(newToken) : null);
      }
    };
    
    window.addEventListener('storage', handleStorageUpdate);
    return () => window.removeEventListener('storage', handleStorageUpdate);
  }, [token]);

  const login = (newToken) => {
    localStorage.setItem('token', newToken);
    const decoded = jwtDecode(newToken);
    setToken(newToken);
    setUserData(decoded);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUserData(null);
  };

  return (
    <AuthContext.Provider value={{ token, userData, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}