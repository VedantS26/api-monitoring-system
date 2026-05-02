import { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import EndpointDetailPage from './pages/EndpointDetailPage';

function App() {

    // ✅ use state so it updates when token changes
    const [isLoggedIn, setIsLoggedIn] = useState(
        !!localStorage.getItem('token')
    );

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={
                    isLoggedIn
                        ? <Navigate to="/dashboard" />
                        : <Navigate to="/login" />
                } />
                <Route path="/login" element={
                    <LoginPage setIsLoggedIn={setIsLoggedIn} />
                } />
                <Route path="/register" element={
                    <RegisterPage setIsLoggedIn={setIsLoggedIn} />
                } />
                <Route path="/dashboard" element={
                    isLoggedIn
                        ? <DashboardPage setIsLoggedIn={setIsLoggedIn} />
                        : <Navigate to="/login" />
                } />
                <Route path="/endpoints/:id" element={
                    isLoggedIn
                        ? <EndpointDetailPage />
                        : <Navigate to="/login" />
                } />
            </Routes>
        </BrowserRouter>
    );
}

export default App;