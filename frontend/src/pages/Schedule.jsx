import React, { useState, useMemo } from 'react';
import { scheduleService } from '../services/api';
import styles from './Schedule.module.css';

const Schedule = () => {
    const [schedule, setSchedule] = useState([]);
    const [availabilities, setAvailabilities] = useState([]);
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(false);

    // Hardcoded logic for demo purposes if not found in student
    // Assuming user might not have set sleep explicitly, default to 7 hours
    // Assuming wake up time at 06:00
    const WAKE_UP_TIME = 6;

    const studentId = localStorage.getItem('studentId');

    const generateSchedule = async () => {
        setLoading(true);
        try {
            // 1. Generate/Fetch Study Schedule
            const schedResponse = await scheduleService.generate(studentId);
            setSchedule(schedResponse.data || []);

            // 2. Fetch Availability
            const availResponse = await availabilityService.getByStudent(studentId);
            setAvailabilities(availResponse.data || []);

            // 3. Fetch Student Details (for sleep hours, etc)
            const studentResponse = await studentService.getById(studentId);
            setStudent(studentResponse.data);

        } catch (error) {
            console.error('Error loading schedule data:', error);
        } finally {
            setLoading(false);
        }
    };

    // Helper to get days of the week starting from today
    const weekDays = useMemo(() => {
        const start = new Date();
        const days = [];
        for (let i = 0; i < 7; i++) {
            const d = new Date(start);
            d.setDate(start.getDate() + i);
            days.push(d);
        }
        return days;
    }, [schedule]);

    // Generate time slots (00:00 to 23:00)
    const timeSlots = Array.from({ length: 24 }, (_, i) => i);

    // --- LOGIC TO SYNTHESIZE BLOCKS ---

    const getDayBlocks = (date) => {
        const blocks = [];
        const dateStr = date.toLocaleDateString();
        const dayOfWeekStr = date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();

        // 1. Sleep Blocks
        // Strategy: Sleep is from (24 - avg_sleep + 6) -> 06:00? 
        // Simpler: Sleep is 10PM to 6AM (8 hours) if not specified.
        // Let's use student.average_sleep_hours. 
        // If 8 hours, and wake up at 6, then sleep starts at 22:00.
        // Sleep Block 1: 00:00 - 06:00 (Morning Sleep)
        // Sleep Block 2: (24 - (avg - 6)) -> 24:00 (Night Sleep)

        let sleepHours = 8;
        if (student && student.averageSleepHours) {
            sleepHours = student.averageSleepHours;
        }

        // Morning Sleep (Midnight to WakeUp)
        // Assuming WakeUp is constant at 06:00 for simplicity
        const wakeUpHour = WAKE_UP_TIME;

        // Start of day sleep
        blocks.push({
            id: `sleep-morning-${dateStr}`,
            type: 'sleep',
            title: 'Sleep',
            startTime: new Date(date).setHours(0, 0, 0, 0),
            endTime: new Date(date).setHours(wakeUpHour, 0, 0, 0),
            color: 'sleepColor'
        });

        // Night Sleep
        // remaining sleep = sleepHours - wakeUpHour (if sleep > 6). 
        // usually sleep is continuous. Let's assume bed time = wakeUp + (24 - sleep)
        // BedTime = 06:00 - sleepHours (in modulo 24).
        // Example: 8 hours sleep. 6am wake up. Bed time = 22:00.
        let bedTimeHour = wakeUpHour - sleepHours;
        if (bedTimeHour < 0) bedTimeHour += 24;

        blocks.push({
            id: `sleep-night-${dateStr}`,
            type: 'sleep',
            title: 'Sleep',
            startTime: new Date(date).setHours(bedTimeHour, 0, 0, 0), // e.g. 22:00
            endTime: new Date(date).setHours(23, 59, 59, 999), // End of day
            color: 'sleepColor'
        });


        // 2. Study Sessions (from API)
        const daySessions = schedule.filter(s => new Date(s.startTime).toLocaleDateString() === dateStr);
        daySessions.forEach(s => {
            blocks.push({
                id: s.std_session_id,
                type: 'study',
                title: s.title,
                startTime: new Date(s.startTime).getTime(),
                endTime: new Date(s.endTime).getTime(),
                // Use existing color logic or type
                color: 'studyColor'
            });
        });


        // 3. Tuition / Busy / School (Availability Gaps)
        // Availability defines when I am FREE.
        // So, GAPS in Availability (between WakeUp and BedTime) = BUSY (School/Tuition).

        // Find availability for this day of week
        const dayAvail = availabilities.filter(a => a.dayOfWeek === dayOfWeekStr);

        // We only care about time between WakeUp (06:00) and BedTime. everything else is sleep.
        // Sort availabilities by start time
        // NOTE: availability times are strings "HH:mm:ss"

        // Create a timeline from WakeUp to BedTime, fill with "Busy", then punch holes for "Available"
        // Actually, better: Assume Busy, then Overlay Available? 
        // No, we want to render BLOCKS.
        // If Avail is 16:00-18:00. And Wake-Bed is 06:00-22:00.
        // Busy blocks: 06:00-16:00, 18:00-22:00.

        const dayStartMs = new Date(date).setHours(wakeUpHour, 0, 0, 0); // 06:00
        const dayEndMs = new Date(date).setHours(bedTimeHour, 0, 0, 0); // 22:00 (if bed time is late)

        // If bedTime is early morning (shift worker), this logic breaks, assuming standard student day.

        let currentPointer = dayStartMs;

        // Parse availabilities to comparable MS
        const sortedAvails = dayAvail.map(a => {
            const [h, m] = a.startTime.split(':');
            const [eh, em] = a.endTime.split(':');
            const startMs = new Date(date).setHours(parseInt(h), parseInt(m), 0, 0);
            const endMs = new Date(date).setHours(parseInt(eh), parseInt(em), 0, 0);
            return { startMs, endMs };
        }).sort((a, b) => a.startMs - b.startMs);

        sortedAvails.forEach(slot => {
            // Gap between currentPointer and slot.start is BUSY
            if (slot.startMs > currentPointer) {
                // There is a busy gap
                blocks.push({
                    id: `busy-${currentPointer}`,
                    type: 'busy',
                    title: 'School / Tuition',
                    startTime: currentPointer,
                    endTime: slot.startMs,
                    color: 'busyColor'
                });
            }
            // Move pointer to end of this availability
            // The availability itself is "Free time" (potential study), but if no study computed, it's just "Free"
            // We can visualize "Free" explicitly or just leave blank. 
            // User asked for "Tuition time", which implies the busy parts.
            currentPointer = Math.max(currentPointer, slot.endMs);
        });

        // Final gap after last availability until bed time
        if (currentPointer < dayEndMs) {
            blocks.push({
                id: `busy-end-${currentPointer}`,
                type: 'busy',
                title: 'School / Tuition / Other',
                startTime: currentPointer,
                endTime: dayEndMs,
                color: 'busyColor'
            });
        }

        return blocks;
    };


    const getSessionStyle = (block) => {
        const start = new Date(block.startTime);
        const end = new Date(block.endTime);

        // Calculate top position (minutes from midnight)
        const startMinutes = start.getHours() * 60 + start.getMinutes();
        const durationMinutes = (end - start) / (1000 * 60);

        // 1 hour = 60px.
        return {
            top: `${startMinutes}px`,
            height: `${durationMinutes}px`,
        };
    };

    const getBlockColorClass = (block) => {
        if (block.type === 'sleep') return styles.sleepBlock;
        if (block.type === 'busy') return styles.busyBlock;
        // Study blocks: use existing hash
        return getSubjectColorClass(block.title);
    };

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

            {loading && <div className={styles.loading}>Generating your personalized plan...</div>}

            {!loading && schedule.length === 0 && availabilities.length === 0 ? (
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
                            const blocks = getDayBlocks(day);
                            return (
                                <div key={i} className={styles.dayColumn}>
                                    {blocks.map((block) => (
                                        <div
                                            key={block.id}
                                            className={`${styles.sessionBlock} ${getBlockColorClass(block)}`}
                                            style={getSessionStyle(block)}
                                            title={`${block.title} (${new Date(block.startTime).toLocaleTimeString()} - ${new Date(block.endTime).toLocaleTimeString()})`}
                                        >
                                            <div className={styles.sessionTitle}>{block.title}</div>
                                            <div className={styles.sessionTime}>
                                                {new Date(block.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} -
                                                {new Date(block.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
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
