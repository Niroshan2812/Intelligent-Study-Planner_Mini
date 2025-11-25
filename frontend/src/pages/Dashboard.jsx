import React, { useEffect, useState } from 'react';
import { examService } from '../services/api';
import styles from './Dashboard.module.css';

const Dashboard = () => {
    const [exams, setExams] = useState([]);
    const studentId = localStorage.getItem('studentId');

    useEffect(() => {
        if (studentId) {
            loadExams();
        }
    }, [studentId]);

    const loadExams = async () => {
        try {
            const response = await examService.getByStudent(studentId);
            setExams(response.data);
        } catch (error) {
            console.error('Error loading exams:', error);
        }
    };

    return (
        <div>
            <h1 className="page-title">Dashboard</h1>

            <div className={styles.grid}>
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Upcoming Exams</h3>
                    {exams.length === 0 ? (
                        <p className={styles.emptyText}>No upcoming exams found.</p>
                    ) : (
                        <div className={styles.list}>
                            {exams.map((exam) => (
                                <div key={exam.id} className={styles.listItem}>
                                    <div>
                                        <div className={styles.itemName}>{exam.name}</div>
                                        <div className={styles.itemSub}>{new Date(exam.deadline).toLocaleDateString()}</div>
                                    </div>
                                    <div className={styles.badge}>
                                        {Math.ceil((new Date(exam.deadline) - new Date()) / (1000 * 60 * 60 * 24))} days left
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Quick Actions</h3>
                    <div className={styles.actions}>
                        <button className="btn-primary" onClick={() => window.location.href = '/subjects'}>Add Subject</button>
                        <button className="btn-primary" onClick={() => window.location.href = '/exams'}>Add Exam</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
