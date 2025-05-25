// src/context/UserProvider.jsx
import React, { useState, useEffect } from 'react';
import UserContext from './UserContext';

export default function UserProvider({ children }) {
  const [userId, setUserId] = useState(null);
  const [carritoId, setCarritoId] = useState(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    const storedCarritoId = localStorage.getItem('carritoId');
    if (storedUserId) setUserId(parseInt(storedUserId, 10));
    if (storedCarritoId) setCarritoId(parseInt(storedCarritoId, 10));
  }, []);

  return (
    <UserContext.Provider value={{ userId, setUserId, carritoId, setCarritoId }}>
      {children}
    </UserContext.Provider>
  );
}
