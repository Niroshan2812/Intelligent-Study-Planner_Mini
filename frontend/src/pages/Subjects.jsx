import React, { useEffect, useState } from 'react';
import { subjectService } from '../services/api';
import styles from './Subjects.module.css';

const Subjects = () => {
    const [subjects, setSubjects] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        difficaltyLevel: 1,
        currentScore: 0,
    });
    const studentId = localStorage.getItem('studentId');

    useEffect(() => {
        if (studentId) loadSubjects();
    }, [studentId]);

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
            await subjectService.create({ ...formData, studentId });
            setFormData({ name: '', difficaltyLevel: 1, currentScore: 0 });
            loadSubjects();
        } catch (error) {
            console.error('Error creating subject:', error);
        }
    };

    return (
        <div>
            <h1 className="page-title">Subjects</h1>

            <div className={styles.grid}>
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Add New Subject</h3>
                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className={styles.formGroup}>
                            <label>Subject Name</label>
                            <input
                                type="text"
                                className="input-field"
                                value={formData.name}
                                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                required
                            />
                        </div>
                        <div className={styles.formGroup}>
                            <label>Difficulty (1-5)</label>
                            <input
                                type="number"
                                min="1"
                                max="5"
                                className="input-field"
                                value={formData.difficaltyLevel}
                                onChange={(e) => setFormData({ ...formData, difficaltyLevel: parseInt(e.target.value) })}
                                required
                            />
                        </div>
                        <div className={styles.formGroup}>
                            <label>Current Score</label>
                            <input
                                type="number"
                                className="input-field"
                                value={formData.currentScore}
                                onChange={(e) => setFormData({ ...formData, currentScore: parseFloat(e.target.value) })}
                                required
                            />
                        </div>
                        <button type="submit" className="btn-primary">Add Subject</button>
                    </form>
                </div>

                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Your Subjects</h3>
                    <div className={styles.list}>
                        {subjects.map((subject) => (
                            <div key={subject.id} className={styles.listItem}>
                                <div className={styles.itemName}>{subject.name}</div>
                                <div className={styles.itemDetails}>
                                    <span>Difficulty: {subject.difficaltyLevel}</span>
                                    <span>Score: {subject.currentScore}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Subjects;
