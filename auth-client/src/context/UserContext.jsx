import React, { createContext, useState, useEffect } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [userId, setUserId] = useState(() => parseInt(localStorage.getItem('userId'), 10) || null);
  const [carritoId, setCarritoId] = useState(() => parseInt(localStorage.getItem('carritoId'), 10) || null);

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
