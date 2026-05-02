import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getDashboard, addEndpoint, deleteEndpoint } from '../services/api';

function DashboardPage({ setIsLoggedIn }) {
   
    const [endpoints, setEndpoints] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [form, setForm] = useState({
        name: '',
        url: '',
        checkIntervalSeconds: 30,
        tag: '',
        alertIntervalMinutes: 5,
    });
    const navigate = useNavigate();

    // ─── Fetch Dashboard Data ─────────────────────────────────
    const fetchDashboard = async () => {
        try {
            const response = await getDashboard();
            setEndpoints(response.data);
        } catch (err) {
            setError('Failed to load dashboard');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboard();
        // refresh every 30 seconds
        const interval = setInterval(fetchDashboard, 30000);
        return () => clearInterval(interval);
    }, []);

    // ─── Logout ───────────────────────────────────────────────
      const handleLogout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);             
        navigate('/login');
    };

    // ─── Add Endpoint ─────────────────────────────────────────
    const handleAddEndpoint = async (e) => {
        e.preventDefault();
        try {
            await addEndpoint(form);
            setShowForm(false);
            setForm({
                name: '',
                url: '',
                checkIntervalSeconds: 30,
                tag: '',
                alertIntervalMinutes: 5,
            });
            fetchDashboard();
        } catch (err) {
            setError('Failed to add endpoint');
        }
    };
    

    // ─── Delete Endpoint ──────────────────────────────────────
    const handleDelete = async (id) => {
        if (window.confirm('Delete this endpoint?')) {
            try {
                await deleteEndpoint(id);
                fetchDashboard();
            } catch (err) {
                setError('Failed to delete endpoint');
            }
        }
    };

    if (loading) return <div style={styles.center}>Loading...</div>;

    return (
        <div style={styles.container}>

            {/* ─── Navbar ─────────────────────────────────── */}
            <div style={styles.navbar}>
                <h1 style={styles.navTitle}>🔍 API Monitor</h1>
                <button style={styles.logoutBtn} onClick={handleLogout}>
                    Logout
                </button>
            </div>

            {/* ─── Main Content ────────────────────────────── */}
            <div style={styles.content}>

                {error && <p style={styles.error}>{error}</p>}

                {/* ─── Header Row ──────────────────────────── */}
                <div style={styles.headerRow}>
                    <h2 style={styles.sectionTitle}>
                        Your Endpoints ({endpoints.length})
                    </h2>
                    <button
                        style={styles.addBtn}
                        onClick={() => setShowForm(!showForm)}>
                        {showForm ? 'Cancel' : '+ Add Endpoint'}
                    </button>
                </div>

                {/* ─── Add Endpoint Form ───────────────────── */}
                {showForm && (
                    <div style={styles.formCard}>
                        <h3 style={styles.formTitle}>Add New Endpoint</h3>
                        <form onSubmit={handleAddEndpoint}>
                            <div style={styles.formGrid}>

                                <div style={styles.inputGroup}>
                                    <label style={styles.label}>Name</label>
                                    <input
                                        style={styles.input}
                                        placeholder="My API"
                                        value={form.name}
                                        onChange={(e) => setForm({
                                            ...form, name: e.target.value
                                        })}
                                        required
                                    />
                                </div>

                                <div style={styles.inputGroup}>
                                    <label style={styles.label}>URL</label>
                                    <input
                                        style={styles.input}
                                        placeholder="https://api.example.com"
                                        value={form.url}
                                        onChange={(e) => setForm({
                                            ...form, url: e.target.value
                                        })}
                                        required
                                    />
                                </div>

                                <div style={styles.inputGroup}>
                                    <label style={styles.label}>
                                        Check Interval (seconds)
                                    </label>
                                    <input
                                        style={styles.input}
                                        type="number"
                                        value={form.checkIntervalSeconds}
                                        onChange={(e) => setForm({
                                            ...form,
                                            checkIntervalSeconds: parseInt(e.target.value)
                                        })}
                                    />
                                </div>

                                <div style={styles.inputGroup}>
                                    <label style={styles.label}>Tag</label>
                                    <input
                                        style={styles.input}
                                        placeholder="prod / dev / staging"
                                        value={form.tag}
                                        onChange={(e) => setForm({
                                            ...form, tag: e.target.value
                                        })}
                                    />
                                </div>

                                <div style={styles.inputGroup}>
                                    <label style={styles.label}>
                                        Alert Interval (minutes)
                                    </label>
                                    <input
                                        style={styles.input}
                                        type="number"
                                        value={form.alertIntervalMinutes}
                                        onChange={(e) => setForm({
                                            ...form,
                                            alertIntervalMinutes: parseInt(e.target.value)
                                        })}
                                    />
                                </div>

                            </div>

                            <button style={styles.submitBtn} type="submit">
                                Add Endpoint
                            </button>
                        </form>
                    </div>
                )}



                {/* ─── Endpoint Cards ──────────────────────── */}
                {endpoints.length === 0 ? (
                    <div style={styles.emptyState}>
                        <p>No endpoints yet. Add one to start monitoring!</p>
                    </div>
                ) : (
                    <div style={styles.grid}>
                        {endpoints.map((endpoint) => (
                            <div key={endpoint.endpointId} style={styles.card}>

                                {/* Status Badge */}
                                <div style={styles.cardHeader}>
                                    <span style={
                                        endpoint.isUp
                                            ? styles.badgeUp
                                            : styles.badgeDown
                                    }>
                                        {endpoint.isUp ? '● UP' : '● DOWN'}
                                    </span>
                                    {endpoint.tag && (
                                        <span style={styles.tag}>
                                            {endpoint.tag}
                                        </span>
                                    )}
                                </div>

                                {/* Endpoint Info */}
                                <h3 style={styles.endpointName}>
                                    {endpoint.endpointName}
                                </h3>
                                <p style={styles.endpointUrl}>
                                    {endpoint.endpointUrl}
                                </p>

                                {/* Stats */}
                                <div style={styles.stats}>
                                    <span>
                                        ⚡ {endpoint.latestResponseTime}ms
                                    </span>
                                    <span>
                                        🕐 {endpoint.lastChecked
                                            ? new Date(endpoint.lastChecked)
                                                .toLocaleTimeString()
                                            : 'Not checked yet'}
                                    </span>
                                </div>

                                {/* Action Buttons */}
                                <div style={styles.actions}>
                                    <button
                                        style={styles.detailBtn}
                                        onClick={() => navigate(
                                            `/endpoints/${endpoint.endpointId}`
                                        )}>
                                        View Details
                                    </button>
                                    <button
                                        style={styles.deleteBtn}
                                        onClick={() =>
                                            handleDelete(endpoint.endpointId)
                                        }>
                                        Delete
                                    </button>
                                </div>

                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

// ─── Styles ───────────────────────────────────────────────────
const styles = {
    container: {
        minHeight: '100vh',
        backgroundColor: '#f0f2f5',
    },
    navbar: {
        backgroundColor: '#1a1a2e',
        padding: '15px 30px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    navTitle: {
        color: 'white',
        margin: 0,
        fontSize: '22px',
    },
    logoutBtn: {
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        padding: '8px 16px',
        borderRadius: '8px',
        cursor: 'pointer',
        fontSize: '14px',
    },
    content: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '30px 20px',
    },
    headerRow: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
    },
    sectionTitle: {
        color: '#1a1a2e',
        margin: 0,
    },
    addBtn: {
        backgroundColor: '#4361ee',
        color: 'white',
        border: 'none',
        padding: '10px 20px',
        borderRadius: '8px',
        cursor: 'pointer',
        fontSize: '14px',
    },
    formCard: {
        backgroundColor: 'white',
        padding: '25px',
        borderRadius: '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.08)',
        marginBottom: '25px',
    },
    formTitle: {
        margin: '0 0 20px 0',
        color: '#1a1a2e',
    },
    formGrid: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '15px',
    },
    inputGroup: {
        display: 'flex',
        flexDirection: 'column',
    },
    label: {
        marginBottom: '5px',
        color: '#333',
        fontWeight: '500',
        fontSize: '14px',
    },
    input: {
        padding: '10px',
        borderRadius: '8px',
        border: '1px solid #ddd',
        fontSize: '14px',
    },
    submitBtn: {
        marginTop: '20px',
        backgroundColor: '#4361ee',
        color: 'white',
        border: 'none',
        padding: '12px 25px',
        borderRadius: '8px',
        cursor: 'pointer',
        fontSize: '14px',
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
        gap: '20px',
    },
    card: {
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.08)',
    },
    cardHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '12px',
    },
    badgeUp: {
        backgroundColor: '#d4edda',
        color: '#28a745',
        padding: '4px 10px',
        borderRadius: '20px',
        fontSize: '13px',
        fontWeight: '600',
    },
    badgeDown: {
        backgroundColor: '#f8d7da',
        color: '#dc3545',
        padding: '4px 10px',
        borderRadius: '20px',
        fontSize: '13px',
        fontWeight: '600',
    },
    tag: {
        backgroundColor: '#e8f4fd',
        color: '#4361ee',
        padding: '3px 10px',
        borderRadius: '20px',
        fontSize: '12px',
    },
    endpointName: {
        margin: '0 0 5px 0',
        color: '#1a1a2e',
        fontSize: '18px',
    },
    endpointUrl: {
        color: '#666',
        fontSize: '13px',
        margin: '0 0 15px 0',
        wordBreak: 'break-all',
    },
    stats: {
        display: 'flex',
        justifyContent: 'space-between',
        color: '#555',
        fontSize: '13px',
        marginBottom: '15px',
        backgroundColor: '#f8f9fa',
        padding: '8px 12px',
        borderRadius: '8px',
    },
    actions: {
        display: 'flex',
        gap: '10px',
    },
    detailBtn: {
        flex: 1,
        backgroundColor: '#4361ee',
        color: 'white',
        border: 'none',
        padding: '8px',
        borderRadius: '8px',
        cursor: 'pointer',
        fontSize: '13px',
    },
    deleteBtn: {
        flex: 1,
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        padding: '8px',
        borderRadius: '8px',
        cursor: 'pointer',
        fontSize: '13px',
    },
    emptyState: {
        textAlign: 'center',
        padding: '60px',
        color: '#666',
        backgroundColor: 'white',
        borderRadius: '12px',
    },
    error: {
        color: 'red',
        textAlign: 'center',
        marginBottom: '15px',
    },
    center: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        fontSize: '18px',
        color: '#666',
    },
};

export default DashboardPage;