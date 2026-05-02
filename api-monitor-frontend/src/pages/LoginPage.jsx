import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../services/api";

function LoginPage({ setIsLoggedIn }){
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await login(email, password);
            localStorage.setItem('token', response.data.token);
            setIsLoggedIn(true);          // ← add this
            navigate('/dashboard');
        } catch (err) {
            
            setError('Invalid email or password');
        } finally {
            setLoading(false);
        }
    };

    return(
        <div style={styles.container}>
            <div style={styles.card}>

                <h2 style ={styles.title}>API Monitor</h2>
                <p style={styles.subtitle}>Login to your account</p>

                {error && <p style={styles.error}>{error}</p>}

                <form onSubmit={handleLogin}>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}> Email</label>
                        <input 

                            style={styles.input}
                            type="email"
                            placeholder="test@gmail.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div> 

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Password</label>
                        <input
                            style={styles.input}
                            type="password"
                            placeholder="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
     
                    <button
                        style={loading ? styles.buttonDisabled : styles.button}
                        type="submit"
                        disabled={loading}>
                        {loading ? 'Logging in...' : 'Login'}
                    </button>


                </form>

                <p style={styles.registerText}>
                    Don't have an account? {' '}
                    <Link to="/register" style={styles.link}>
                        Register here
                    </Link>
                </p>


            </div>
        </div>
    );


}

// ─── Styles ─────────────────────────────────────────────────────

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f0f2f5',
    },
    card: {
        backgroundColor: 'white',
        padding: '40px',
        borderRadius:  '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        width: '400px',
    },
    title:{
        textAlign: 'center',
        color: '#1a1a2e',
        marginBottom: '5px',
        fontSize: '28px',
    },
    subtitle: {
        textAlign: 'center',
        color: '#666',
        marginBottom: '25px',
    },
    inputGroup:{
        marginBottom: '20px',
    },
    label: {
        display: 'block',
        marginBottom: '5px',
        color: '#333',
        fontWeight: '500',
    },
    input: {
        width: '100%',
        padding: '10px',
        borderRadius: '8px',
        border: '1px solid #ddd',
        fontSize: '14px',
        boxSizing: 'border-box',
    },   
    button:{
        widht: '100%',
        padding: '12px',
        backgroundColor: '#4361ee',
        color: 'white',
        border: 'none',
        borderRadius: '8px',
        fontSize: '16px',
        cursor: 'pointer',
        marginTop: '10px',
    },
    error: {
        color: 'red',
        textAlign: 'center',
        marginBottom: '15px',
        fontSize: '14px',
    },
    registerText: {
        textAlign: 'center',
        marginTop: '20px',
        color: '#666',
        fontSize: '14px',
    },
    link: {
        color: '#4361ee',
        textDecoration: 'none',
        fontWeight: '500',
    },
};

export default LoginPage;
    
    
    

    