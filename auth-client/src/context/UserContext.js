// src/context/UserContext.js
import React, { createContext, useState, useEffect } from 'react';

const UserContext = createContext(null);

export const UserProvider = ({ children }) => {
  const [userId, setUserId] = useState(() => {
    const storedUserId = localStorage.getItem('userId');
    return storedUserId ? parseInt(storedUserId, 10) : null;
  });

  const [carritoId, setCarritoId] = useState(() => {
    const storedCarritoId = localStorage.getItem('carritoId');
    return storedCarritoId ? parseInt(storedCarritoId, 10) : null;
  });

  useEffect(() => {
    if (userId) {
      localStorage.setItem('userId', userId);
    } else {
      localStorage.removeItem('userId');
    }
  }, [userId]);

  useEffect(() => {
    if (carritoId) {
      localStorage.setItem('carritoId', carritoId);
    } else {
      localStorage.removeItem('carritoId');
    }
  }, [carritoId]);

  return (
    <UserContext.Provider value={{ userId, setUserId, carritoId, setCarritoId }}>
      {children}
    </UserContext.Provider>
  );
};

export default UserContext;
