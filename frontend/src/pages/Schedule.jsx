import React, { useState, useMemo } from 'react';
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

    // Helper to get days of the week starting from today or the first scheduled item
    const weekDays = useMemo(() => {
        const start = new Date();
        // If we have a schedule, maybe start from the first session? 
        // For now, let's just show the next 7 days starting from today
        const days = [];
        for (let i = 0; i < 7; i++) {
            const d = new Date(start);
            d.setDate(start.getDate() + i);
            days.push(d);
        }
        return days;
    }, [schedule]);

    // Group sessions by day string (YYYY-MM-DD)
    const sessionsByDay = useMemo(() => {
        const groups = {};
        schedule.forEach(session => {
            const date = new Date(session.startTime);
            const key = date.toLocaleDateString(); // Local date string as key
            if (!groups[key]) groups[key] = [];
            groups[key].push(session);
        });
        return groups;
    }, [schedule]);

    // Generate time slots (00:00 to 23:00)
    const timeSlots = Array.from({ length: 24 }, (_, i) => i);

    const getSessionStyle = (session, index) => {
        const start = new Date(session.startTime);
        const end = new Date(session.endTime);

        // Calculate top position (minutes from midnight)
        const startMinutes = start.getHours() * 60 + start.getMinutes();
        const durationMinutes = (end - start) / (1000 * 60);

        // 1 hour = 60px height
        const top = startMinutes; // 1px per minute for simplicity? Or scale it.
        // Let's say 1 hour = 60px. So 1 minute = 1px.

        return {
            top: `${top}px`,
            height: `${durationMinutes}px`,
        };
    };

    // Assign colors based on subject name hash or index
    const getSubjectColorClass = (subjectName) => {
        let hash = 0;
        for (let i = 0; i < subjectName.length; i++) {
            hash = subjectName.charCodeAt(i) + ((hash << 5) - hash);
        }
        const index = Math.abs(hash) % 5;
        return styles[`color${index}`];
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
                <div className={styles.calendarContainer}>
                    {/* Header Row: Days */}
                    <div className={styles.weekHeader}>
                        <div className={styles.timeHeaderPlaceholder}></div>
                        {weekDays.map((day, i) => (
                            <div key={i} className={styles.dayHeader}>
                                <span className={styles.dayName}>{day.toLocaleDateString('en-US', { weekday: 'short' })}</span>
                                <span className={styles.dayDate}>{day.getDate()}</span>
                            </div>
                        ))}
                    </div>

                    {/* Grid Body */}
                    <div className={styles.gridBody}>
                        {/* Time Column */}
                        <div className={styles.timeColumn}>
                            {timeSlots.map(hour => (
                                <div key={hour} className={styles.timeSlot}>
                                    {hour.toString().padStart(2, '0')}:00
                                </div>
                            ))}
                        </div>

                        {/* Day Columns */}
                        {weekDays.map((day, i) => {
                            const dayKey = day.toLocaleDateString();
                            const daySessions = sessionsByDay[dayKey] || [];

                            return (
                                <div key={i} className={styles.dayColumn}>
                                    {/* Render background grid lines for hours? Optional, handled by timeSlot::after */}

                                    {daySessions.map((session, idx) => (
                                        <div
                                            key={session.std_session_id || idx}
                                            className={`${styles.sessionBlock} ${getSubjectColorClass(session.title)}`}
                                            style={getSessionStyle(session, idx)}
                                            title={`${session.title} (${new Date(session.startTime).toLocaleTimeString()} - ${new Date(session.endTime).toLocaleTimeString()})`}
                                        >
                                            <div className={styles.sessionTitle}>{session.title}</div>
                                            <div className={styles.sessionTime}>
                                                {new Date(session.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} -
                                                {new Date(session.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Schedule;
