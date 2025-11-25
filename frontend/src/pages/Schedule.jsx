import React, { useState } from 'react';
import { scheduleService } from '../services/api';
import styles from './Schedule.module.css';

const Schedule = () => {
    const [schedule, setSchedule] = useState([]);
    const [loading, setLoading] = useState(false);
    const studentId = localStorage.getItem('studentId');

    const generateSchedule = async () => {
        setLoading(true);
        try {
            const response = await scheduleService.generate(studentId);
            setSchedule(response.data);
        } catch (error) {
            console.error('Error generating schedule:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <div className={styles.header}>
                <h1 className="page-title" style={{ margin: 0 }}>Study Schedule</h1>
                <button
                    className="btn-primary"
                    onClick={generateSchedule}
                    disabled={loading}
                >
                    {loading ? 'Generating...' : 'Generate New Schedule'}
                </button>
            </div>

            {schedule.length === 0 ? (
                <div className={styles.emptyState}>
                    <p>No schedule generated yet. Click the button above to create your study plan.</p>
                </div>
            ) : (
                <div className={styles.timeline}>
                    {schedule.map((session) => (
                        <div key={session.id} className={styles.sessionCard}>
                            <div className={styles.timeColumn}>
                                <div className={styles.time}>{new Date(session.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                                <div className={styles.date}>{new Date(session.startTime).toLocaleDateString()}</div>
                            </div>
                            <div className={styles.contentColumn}>
                                <h3 className={styles.sessionTitle}>{session.title}</h3>
                                <div className={styles.sessionDetails}>
                                    Duration: {Math.round((new Date(session.endTime) - new Date(session.startTime)) / (1000 * 60))} mins
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Schedule;
