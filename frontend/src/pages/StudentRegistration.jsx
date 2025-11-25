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

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <h1 className={styles.title}>Welcome to StudyPlanner</h1>
                <p className={styles.subtitle}>Let's personalize your study schedule</p>

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

                    <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                        Get Started
                    </button>
                </form>
            </div>
        </div>
    );
};

export default StudentRegistration;
