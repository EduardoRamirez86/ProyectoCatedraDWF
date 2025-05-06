import React, { createContext, useState, useEffect } from 'react';
import { secureGetItem, secureSetItem, secureRemoveItem } from '../utils/secureStorage';

const UserContext = createContext(null);

export const UserProvider = ({ children }) => {
  const [userId, setUserId] = useState(() => {
    const storedUserId = secureGetItem('userId');
    return storedUserId ? parseInt(storedUserId, 10) : null;
  });

  const [carritoId, setCarritoId] = useState(() => {
    const storedCarritoId = secureGetItem('carritoId');
    return storedCarritoId ? parseInt(storedCarritoId, 10) : null;
  });

  useEffect(() => {
    if (userId) {
      secureSetItem('userId', userId.toString());
    } else {
      secureRemoveItem('userId');
    }
  }, [userId]);

  useEffect(() => {
    if (carritoId) {
      secureSetItem('carritoId', carritoId.toString());
    } else {
      secureRemoveItem('carritoId');
    }
  }, [carritoId]);

  return (
    <UserContext.Provider value={{ userId, setUserId, carritoId, setCarritoId }}>
      {children}
    </UserContext.Provider>
  );
};

export default UserContext;