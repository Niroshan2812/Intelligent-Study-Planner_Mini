import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { studentService } from '../services/api';
import styles from './StudentRegistration.module.css';

const StudentRegistration = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        englishFluency: 3,
        tuitionHoursWeekly: 0,
        commuteFatigue: 3,
    });
    const [existingStudents, setExistingStudents] = useState([]);
    const [selectedStudentId, setSelectedStudentId] = useState('');
    const [viewMode, setViewMode] = useState('loading'); // 'loading', 'login', 'register'

    React.useEffect(() => {
        // Clear session when landing on login/register page
        localStorage.removeItem('studentId');

        const fetchStudents = async () => {
            try {
                const response = await studentService.getAll();
                setExistingStudents(response.data);
                if (response.data.length > 0) {
                    setViewMode('login');
                } else {
                    setViewMode('register');
                }
            } catch (error) {
                console.error('Error fetching students:', error);
                setViewMode('register'); // Fallback
            }
        };
        fetchStudents();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await studentService.create(formData);
            localStorage.setItem('studentId', response.data.id);
            navigate('/dashboard');
        } catch (error) {
            console.error('Error creating student:', error);
        }
    };

    const handleLogin = (e) => {
        e.preventDefault();
        if (selectedStudentId) {
            localStorage.setItem('studentId', selectedStudentId);
            navigate('/dashboard');
        }
    };

    if (viewMode === 'loading') {
        return <div className={styles.container}>Loading...</div>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h1 className={styles.title}>Welcome to StudyPlanner</h1>
                <p className={styles.subtitle}>Let's personalize your study schedule</p>

                {viewMode === 'login' && (
                    <div className={styles.loginSection}>
                        <h3>Select Your Profile</h3>
                        <form onSubmit={handleLogin} className={styles.form}>
                            <div className={styles.formGroup}>
                                <select
                                    className="input-field"
                                    value={selectedStudentId}
                                    onChange={(e) => setSelectedStudentId(e.target.value)}
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', border: '1px solid #ddd' }}
                                >
                                    <option value="">-- Select a user --</option>
                                    {existingStudents.map((student) => (
                                        <option key={student.id} value={student.id}>
                                            {student.name} (ID: {student.id})
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <button
                                type="submit"
                                className="btn-primary"
                                style={{ width: '100%', backgroundColor: '#4a90e2', marginBottom: '1rem' }}
                                disabled={!selectedStudentId}
                            >
                                Login
                            </button>
                        </form>

                        <div style={{ textAlign: 'center', marginTop: '1rem', borderTop: '1px solid #eee', paddingTop: '1rem' }}>
                            <p style={{ marginBottom: '0.5rem', color: '#666' }}>New here?</p>
                            <button
                                onClick={() => setViewMode('register')}
                                className="btn-secondary"
                                style={{
                                    background: 'none',
                                    border: '1px solid #4a90e2',
                                    color: '#4a90e2',
                                    padding: '0.5rem 1rem',
                                    borderRadius: '4px',
                                    cursor: 'pointer'
                                }}
                            >
                                Create New Profile
                            </button>
                        </div>
                    </div>
                )}

                {viewMode === 'register' && (
                    <div className={styles.registerSection}>
                        <h3>Create New Profile</h3>
                        <form onSubmit={handleSubmit} className={styles.form}>
                            <div className={styles.formGroup}>
                                <label>Full Name</label>
                                <input
                                    type="text"
                                    className="input-field"
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    required
                                />
                            </div>

                            <div className={styles.formGroup}>
                                <label>English Fluency (1-5)</label>
                                <input
                                    type="range"
                                    min="1"
                                    max="5"
                                    value={formData.englishFluency}
                                    onChange={(e) => setFormData({ ...formData, englishFluency: parseFloat(e.target.value) })}
                                />
                                <div className={styles.rangeValue}>{formData.englishFluency}</div>
                            </div>

                            <div className={styles.formGroup}>
                                <label>Weekly Tuition Hours</label>
                                <input
                                    type="number"
                                    className="input-field"
                                    value={formData.tuitionHoursWeekly}
                                    onChange={(e) => setFormData({ ...formData, tuitionHoursWeekly: parseFloat(e.target.value) })}
                                />
                            </div>

                            <div className={styles.formGroup}>
                                <label>Commute Fatigue (1-5)</label>
                                <input
                                    type="range"
                                    min="1"
                                    max="5"
                                    value={formData.commuteFatigue}
                                    onChange={(e) => setFormData({ ...formData, commuteFatigue: parseFloat(e.target.value) })}
                                />
                                <div className={styles.rangeValue}>{formData.commuteFatigue}</div>
                            </div>

                            <button type="submit" className="btn-primary" style={{ width: '100%', marginBottom: '1rem' }}>
                                Get Started
                            </button>
                        </form>

                        {existingStudents.length > 0 && (
                            <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                                <button
                                    onClick={() => setViewMode('login')}
                                    style={{
                                        background: 'none',
                                        border: 'none',
                                        color: '#666',
                                        textDecoration: 'underline',
                                        cursor: 'pointer'
                                    }}
                                >
                                    Back to Login
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default StudentRegistration;
