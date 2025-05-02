import React from 'react';
import ReactDOM from 'react-dom/client'; // Ensure you're using ReactDOM from 'react-dom/client'
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { UserProvider } from './context/UserContext'; // Ensure correct import

// Create the root and render the App component
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <AuthProvider>
      <UserProvider>
        <App />
      </UserProvider>
    </AuthProvider>
  </React.StrictMode>
);
