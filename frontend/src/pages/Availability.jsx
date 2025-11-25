import React, { useEffect, useState } from 'react';
import { availabilityService } from '../services/api';
import styles from './Availability.module.css';

const Availability = () => {
    const [availabilities, setAvailabilities] = useState([]);
    const [formData, setFormData] = useState({
        dayOfWeek: 'MONDAY',
        startTime: '',
        endTime: '',
    });
    const studentId = localStorage.getItem('studentId');

    const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

    useEffect(() => {
        if (studentId) loadAvailability();
    }, [studentId]);

    const loadAvailability = async () => {
        try {
            const response = await availabilityService.getByStudent(studentId);
            setAvailabilities(response.data);
        } catch (error) {
            console.error('Error loading availability:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await availabilityService.create({ ...formData, studentId });
            setFormData({ ...formData, startTime: '', endTime: '' });
            loadAvailability();
        } catch (error) {
            console.error('Error creating availability:', error);
        }
    };

    return (
        <div>
            <h1 className="page-title">Availability</h1>

            <div className={styles.grid}>
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Add Free Slot</h3>
                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className={styles.formGroup}>
                            <label>Day of Week</label>
                            <select
                                className="input-field"
                                value={formData.dayOfWeek}
                                onChange={(e) => setFormData({ ...formData, dayOfWeek: e.target.value })}
                            >
                                {days.map(day => (
                                    <option key={day} value={day}>{day}</option>
                                ))}
                            </select>
                        </div>
                        <div className={styles.row}>
                            <div className={styles.formGroup}>
                                <label>Start Time</label>
                                <input
                                    type="time"
                                    className="input-field"
                                    value={formData.startTime}
                                    onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                                    required
                                />
                            </div>
                            <div className={styles.formGroup}>
                                <label>End Time</label>
                                <input
                                    type="time"
                                    className="input-field"
                                    value={formData.endTime}
                                    onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                                    required
                                />
                            </div>
                        </div>
                        <button type="submit" className="btn-primary">Add Slot</button>
                    </form>
                </div>

                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <h3 className={styles.cardTitle}>Your Schedule</h3>
                    <div className={styles.list}>
                        {availabilities.map((slot) => (
                            <div key={slot.id} className={styles.listItem}>
                                <div className={styles.itemName}>{slot.dayOfWeek}</div>
                                <div className={styles.itemDetails}>
                                    {slot.startTime} - {slot.endTime}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Availability;
