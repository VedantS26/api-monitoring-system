import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getLogs, getUptime } from '../services/api';
import {
    LineChart, Line, XAxis, YAxis,
    CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts';

function EndpointDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [logs, setLogs] = useState([]);
    const [uptime, setUptime] = useState(null);
    const [loading, setLoading] = useState(true);
    const [days, setDays] = useState(7);

    // ─── Fetch Data ───────────────────────────────────────────
    const fetchData = async () => {
        try {
            const [logsRes, uptimeRes] = await Promise.all([
                getLogs(id),
                getUptime(id, days)
            ]);
            setLogs(logsRes.data);
            setUptime(uptimeRes.data);
        } catch (err) {
            console.error('Failed to fetch data:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [id, days]);

    // ─── Chart Data ───────────────────────────────────────────
    const chartData = logs
        .slice()
        .reverse()
        .map((log) => ({
            time: new Date(log.checkedAt).toLocaleTimeString(),
            responseTime: log.responseTimeMs,
            status: log.isUp ? 'UP' : 'DOWN',
        }));

    if (loading) return <div style={styles.center}>Loading...</div>;

    return (
        <div style={styles.container}>

            {/* ─── Navbar ──────────────────────────────────── */}
            <div style={styles.navbar}>
                <button
                    style={styles.backBtn}
                    onClick={() => navigate('/dashboard')}>
                    ← Back
                </button>
                <h1 style={styles.navTitle}>Endpoint Details</h1>
                <div />
            </div>

            <div style={styles.content}>

                {/* ─── Uptime Card ─────────────────────────── */}
                {uptime && (
                    <div style={styles.uptimeCard}>

                        <div style={styles.uptimeHeader}>
                            <h2 style={styles.uptimeTitle}>
                                Uptime Summary
                            </h2>
                            <select
                                style={styles.select}
                                value={days}
                                onChange={(e) =>
                                    setDays(parseInt(e.target.value))
                                }>
                                <option value={1}>Last 24 hours</option>
                                <option value={7}>Last 7 days</option>
                                <option value={30}>Last 30 days</option>
                            </select>
                        </div>

                        <div style={styles.uptimeStats}>
                            <div style={styles.uptimeStat}>
                                <span style={styles.uptimeValue}>
                                    {uptime.uptimePercentage.toFixed(2)}%
                                </span>
                                <span style={styles.uptimeLabel}>
                                    Uptime
                                </span>
                            </div>
                            <div style={styles.uptimeStat}>
                                <span style={styles.uptimeValue}>
                                    {uptime.totalChecks}
                                </span>
                                <span style={styles.uptimeLabel}>
                                    Total Checks
                                </span>
                            </div>
                            <div style={styles.uptimeStat}>
                                <span style={styles.uptimeValue}>
                                    {uptime.upChecks}
                                </span>
                                <span style={styles.uptimeLabel}>
                                    Successful
                                </span>
                            </div>
                            <div style={styles.uptimeStat}>
                                <span style={
                                    uptime.totalChecks - uptime.upChecks > 0
                                        ? styles.uptimeValueRed
                                        : styles.uptimeValue
                                }>
                                    {uptime.totalChecks - uptime.upChecks}
                                </span>
                                <span style={styles.uptimeLabel}>
                                    Failed
                                </span>
                            </div>
                        </div>

                    </div>
                )}

                {/* ─── Response Time Chart ─────────────────── */}
                <div style={styles.chartCard}>
                    <h2 style={styles.sectionTitle}>
                        Response Time (ms)
                    </h2>
                    {chartData.length > 0 ? (
                        <ResponsiveContainer width="100%" height={300}>
                            <LineChart data={chartData}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis
                                    dataKey="time"
                                    tick={{ fontSize: 11 }}
                                    interval="preserveStartEnd"
                                />
                                <YAxis
                                    tick={{ fontSize: 11 }}
                                    label={{
                                        value: 'ms',
                                        angle: -90,
                                        position: 'insideLeft'
                                    }}
                                />
                                <Tooltip
                                    formatter={(value) => [
                                        `${value}ms`, 'Response Time'
                                    ]}
                                />
                                <Line
                                    type="monotone"
                                    dataKey="responseTime"
                                    stroke="#4361ee"
                                    dot={false}
                                    strokeWidth={2}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    ) : (
                        <p style={styles.noData}>No data yet</p>
                    )}
                </div>

                {/* ─── Logs Table ──────────────────────────── */}
                <div style={styles.tableCard}>
                    <h2 style={styles.sectionTitle}>
                        Recent Logs ({logs.length})
                    </h2>
                    {logs.length > 0 ? (
                        <div style={styles.tableWrapper}>
                            <table style={styles.table}>
                                <thead>
                                    <tr style={styles.tableHeader}>
                                        <th style={styles.th}>Status</th>
                                        <th style={styles.th}>
                                            Status Code
                                        </th>
                                        <th style={styles.th}>
                                            Response Time
                                        </th>
                                        <th style={styles.th}>
                                            Checked At
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {logs.map((log) => (
                                        <tr
                                            key={log.id}
                                            style={styles.tableRow}>
                                            <td style={styles.td}>
                                                <span style={
                                                    log.isUp
                                                        ? styles.badgeUp
                                                        : styles.badgeDown
                                                }>
                                                    {log.isUp ? '● UP' : '● DOWN'}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                <span style={
                                                    log.statusCode < 400
                                                        ? styles.codeGreen
                                                        : styles.codeRed
                                                }>
                                                    {log.statusCode || 'N/A'}
                                                </span>
                                            </td>
                                            <td style={styles.td}>
                                                {log.responseTimeMs}ms
                                            </td>
                                            <td style={styles.td}>
                                                {new Date(log.checkedAt)
                                                    .toLocaleString()}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <p style={styles.noData}>No logs yet</p>
                    )}
                </div>

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
        fontSize: '20px',
    },
    backBtn: {
        backgroundColor: 'transparent',
        color: 'white',
        border: '1px solid white',
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
    uptimeCard: {
        backgroundColor: 'white',
        padding: '25px',
        borderRadius: '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.08)',
        marginBottom: '20px',
    },
    uptimeHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
    },
    uptimeTitle: {
        margin: 0,
        color: '#1a1a2e',
    },
    select: {
        padding: '8px 12px',
        borderRadius: '8px',
        border: '1px solid #ddd',
        fontSize: '14px',
        cursor: 'pointer',
    },
    uptimeStats: {
        display: 'grid',
        gridTemplateColumns: 'repeat(4, 1fr)',
        gap: '20px',
        textAlign: 'center',
    },
    uptimeStat: {
        display: 'flex',
        flexDirection: 'column',
        gap: '5px',
        backgroundColor: '#f8f9fa',
        padding: '15px',
        borderRadius: '10px',
    },
    uptimeValue: {
        fontSize: '28px',
        fontWeight: '700',
        color: '#28a745',
    },
    uptimeValueRed: {
        fontSize: '28px',
        fontWeight: '700',
        color: '#dc3545',
    },
    uptimeLabel: {
        fontSize: '13px',
        color: '#666',
    },
    chartCard: {
        backgroundColor: 'white',
        padding: '25px',
        borderRadius: '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.08)',
        marginBottom: '20px',
    },
    sectionTitle: {
        margin: '0 0 20px 0',
        color: '#1a1a2e',
    },
    tableCard: {
        backgroundColor: 'white',
        padding: '25px',
        borderRadius: '12px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.08)',
    },
    tableWrapper: {
        overflowX: 'auto',
    },
    table: {
        width: '100%',
        borderCollapse: 'collapse',
    },
    tableHeader: {
        backgroundColor: '#f8f9fa',
    },
    th: {
        padding: '12px 15px',
        textAlign: 'left',
        fontSize: '13px',
        color: '#666',
        fontWeight: '600',
        borderBottom: '2px solid #eee',
    },
    tableRow: {
        borderBottom: '1px solid #f0f0f0',
    },
    td: {
        padding: '12px 15px',
        fontSize: '14px',
        color: '#333',
    },
    badgeUp: {
        color: '#28a745',
        fontWeight: '600',
    },
    badgeDown: {
        color: '#dc3545',
        fontWeight: '600',
    },
    codeGreen: {
        color: '#28a745',
        fontWeight: '600',
    },
    codeRed: {
        color: '#dc3545',
        fontWeight: '600',
    },
    noData: {
        color: '#999',
        textAlign: 'center',
        padding: '20px',
    },
    center: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        fontSize: '18px',
        color: '#666',
    },
};

export default EndpointDetailPage;