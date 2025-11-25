import React, { useEffect, useState } from 'react';
import { examService, subjectService } from '../services/api';
import styles from './Exams.module.css';

const Exams = () => {
    const [exams, setExams] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        deadline: '',
        subjectId: '',
    });
    const studentId = localStorage.getItem('studentId');

    useEffect(() => {
        if (studentId) {
            loadExams();
            loadSubjects();
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

    const loadSubjects = async () => {
        try {
            const response = await subjectService.getByStudent(studentId);
            setSubjects(response.data);
        } catch (error) {
            console.error('Error loading subjects:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await examService.create(formData);
            setFormData({ name: '', deadline: '', subjectId: '' });
            loadExams();
        } catch (error) {
            console.error('Error creating exam:', error);
        }
    };

    return (
        <div>
            <h1 className="page-title">Exams</h1>

            <div className={styles.grid}>
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Add New Exam</h3>
                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className={styles.formGroup}>
                            <label>Exam Name</label>
                            <input
                                type="text"
                                className="input-field"
                                value={formData.name}
                                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                required
                            />
                        </div>
                        <div className={styles.formGroup}>
                            <label>Subject</label>
                            <select
                                className="input-field"
                                value={formData.subjectId}
                                onChange={(e) => setFormData({ ...formData, subjectId: e.target.value })}
                                required
                            >
                                <option value="">Select Subject</option>
                                {subjects.map((s) => (
                                    <option key={s.id} value={s.id}>{s.name}</option>
                                ))}
                            </select>
                        </div>
                        <div className={styles.formGroup}>
                            <label>Deadline</label>
                            <input
                                type="datetime-local"
                                className="input-field"
                                value={formData.deadline}
                                onChange={(e) => setFormData({ ...formData, deadline: e.target.value })}
                                required
                            />
                        </div>
                        <button type="submit" className="btn-primary">Add Exam</button>
                    </form>
                </div>

                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Upcoming Exams</h3>
                    <div className={styles.list}>
                        {exams.map((exam) => (
                            <div key={exam.id} className={styles.listItem}>
                                <div className={styles.itemName}>{exam.name}</div>
                                <div className={styles.itemDetails}>
                                    <span>{new Date(exam.deadline).toLocaleString()}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Exams;
