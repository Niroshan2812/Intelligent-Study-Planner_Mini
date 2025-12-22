package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.Availability;
import com.intelligent.intelligentstdyplanner.Model.Exam;
import com.intelligent.intelligentstdyplanner.Model.StudySession;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
    In here Using Choco-solver library ti mathematically figure out the best time study session
    Assumptions -
        Defile Date using slots
        Define time as a 30 min blocks
 */
@Service
public class ConstraintService {

    private static final int SLOT_MINUTES = 30;

    public List<StudySession> generateSchedule(List<Exam> exams, List<Availability> availabilities,
            Map<Long, Float> predictedHours) {
        // preferences enforced
        System.out.println("Attempting to schedule WITH user preferences...");
        List<StudySession> sessions = generateScheduleInternal(exams, availabilities, predictedHours, true);

        if (!sessions.isEmpty()) {
            return sessions;
        }

        // if fail
        System.out.println("Could not satisfy preferences. Falling back to flexible schedule.");
        return generateScheduleInternal(exams, availabilities, predictedHours, false);
    }

    private List<StudySession> generateScheduleInternal(List<Exam> exams, List<Availability> availabilities,
            Map<Long, Float> predictedHours, boolean enforcePreferences) {
        Model model = new Model("Study Schedule");

        // round is set as 30 min
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduleStart = roundToNextSlot(now);

        // Find max deadline
        LocalDateTime maxDeadline = exams.stream()
                .map(Exam::getDeadline)
                .max(LocalDateTime::compareTo)
                .orElse(now.plusDays(7));

        // Calculate total slots
        long totalSlots = Duration.between(scheduleStart, maxDeadline).toMinutes() / SLOT_MINUTES;
        if (totalSlots <= 0)
            return Collections.emptyList();
        // System.out.println("Total slots: " + totalSlots);
        // calculate total slots, 720 * 2 = 1440
        int horizon = (int) Math.min(totalSlots, 30 * 24 * 2);

        // 2. Pre-process Availability
        /*
         *cREATE BITSET -- long array contain 0 and 1
         * Set all are 0 as start
         */
        BitSet availableSlots = new BitSet(horizon);
        // iterate 0 to 1440
        for (int i = 0; i < horizon; i++) {
            // create abstract index i --> i = 0 mean 8.00 am like wise 30 min slots
            LocalDateTime slotTime = scheduleStart.plusMinutes((long) i * SLOT_MINUTES);
            // check student availability if yes -  flip 0 to 1
            if (isAvailable(slotTime, availabilities)) {
                availableSlots.set(i);
            }
        }

        // 3. Create Tasks
        List<Task> chocoTasks = new ArrayList<>();
        List<Subject> taskSubjects = new ArrayList<>();

        for (Exam exam : exams) {
            // take predicted hours from model
            Subject subject = exam.getSubject();
            float hoursNeeded = predictedHours.getOrDefault(subject.getSubject_id(), 0f);
            if (hoursNeeded <= 0)
                continue;

            // Convert hours to slots 30 min one
            int slotsNeeded = (int) Math.ceil(hoursNeeded * 60 / SLOT_MINUTES);

            // if its 10 hr we can t allocate hence,
            // Split into chunks if too large--------------------------------------------
            // set max as a 2 hr set
            int maxChunkSize = 4;
            int remainingSlots = slotsNeeded;

            int chunkIndex = 0;
            while (remainingSlots > 0) {
                int studyDuration = Math.min(remainingSlots, maxChunkSize);
                // Add 1 slot (30 mins) for break/buffer
                // need to allocate 30min brake after every session
                int totalDuration = studyDuration + 1;

                // Create Task  variable values needs dynamic
                // set start time domain- [0, horizon - totalDuration]
                IntVar start = model.intVar("start_" + subject.getName() + "_" + chunkIndex, 0,
                        horizon - totalDuration);
                IntVar end = model.intVar("end_" + subject.getName() + "_" + chunkIndex, 0, horizon);
                // fix now set duration with break
                IntVar dur = model.intVar(totalDuration);
                // pckge this 3 into choco-solver
                Task task = new Task(start, dur, end);
                chocoTasks.add(task);
                taskSubjects.add(subject);

                // Apply Preference Mask if needed
                BitSet effectiveSlots = (BitSet) availableSlots.clone();
                if (enforcePreferences && subject.getPreferredTimeOfDay() != null &&
                        subject.getPreferredTimeOfDay() != com.intelligent.intelligentstdyplanner.Model.TimePreference.ANY) {
                    maskSlotsWithPreference(effectiveSlots, scheduleStart, horizon, subject.getPreferredTimeOfDay());
                }

                // check bitset to find every srart time i when a block of totalDuration
                // if class stat 9 so solver cant pick 9 as start time
                int[] validStarts = getValidStartIndices(effectiveSlots, horizon, totalDuration);
                if (validStarts.length == 0) {
                    System.out.println("No valid slots found for " + subject.getName() + " chunk " + chunkIndex);
                    // For now, proceeding but this task implies constraint failure if not solvable
                } else {
                    model.member(start, validStarts).post();
                }

                // study session need to finished must be before deadline
                long deadlineSlots = Duration.between(scheduleStart, exam.getDeadline()).toMinutes() / SLOT_MINUTES;
                model.arithm(end, "<=", (int) deadlineSlots).post();

                remainingSlots -= studyDuration;
                chunkIndex++;
            }
        }
        // if exam is no or no hr need
        if (chocoTasks.isEmpty())
            return Collections.emptyList();

        // No Overlap
        Task[] tasksArray = chocoTasks.toArray(new Task[0]);
        // Use cumulative for this
        IntVar[] heights = new IntVar[chocoTasks.size()];
        // recource usage,
        for (int i = 0; i < chocoTasks.size(); i++)
            heights[i] = model.intVar(1);
        // one user cna handle 1 task at a time
        IntVar capacity = model.intVar(1);
        // height of all the activity not exceed the capacity
        model.cumulative(tasksArray, heights, capacity).post();

        // Solve
        // filtering and check all the rules
        Solver solver = model.getSolver();
        if (solver.solve()) {
            // Extract solution
            List<StudySession> sessions = new ArrayList<>();
            for (int i = 0; i < chocoTasks.size(); i++) {
                Task t = chocoTasks.get(i);
                Subject s = taskSubjects.get(i);

                int startSlot = t.getStart().getValue();
                // The task duration includes the break, so we subtract 1 slot for the actual
                // study session
                int totalDurationSlots = t.getDuration().getValue();
                int studyDurationSlots = totalDurationSlots - 1;

                LocalDateTime start = scheduleStart.plusMinutes((long) startSlot * SLOT_MINUTES);
                LocalDateTime end = start.plusMinutes((long) studyDurationSlots * SLOT_MINUTES);

                StudySession session = new StudySession();
                session.setSubject(s);
                session.setStartTime(start);
                session.setEndTime(end);
                session.setTitle("Study " + s.getName());

                sessions.add(session);
            }
            // Merge contiguous sessions for same subject
            return mergeSessions(sessions);
        } else {
            System.out.println("No solution found for schedule!");
            return Collections.emptyList();
        }
    }

    private LocalDateTime roundToNextSlot(LocalDateTime time) {
        int minute = time.getMinute();
        if (minute > 30) {
            return time.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        } else if (minute > 0) {
            return time.withMinute(30).withSecond(0).withNano(0);
        }
        return time.withSecond(0).withNano(0);
    }

    private boolean isAvailable(LocalDateTime time, List<Availability> availabilities) {
        // Check if 'time' falls within any availability window
        for (Availability av : availabilities) {
            if (av.getDayOfWeek() == time.getDayOfWeek()) {
                LocalTime avStart = av.getStartTime();
                LocalTime avEnd = av.getEndTime();
                LocalTime slotStart = time.toLocalTime();
                LocalTime slotEnd = slotStart.plusMinutes(SLOT_MINUTES);

                // Handle day overflow for slotEnd
                if (!slotStart.isBefore(avStart) && !slotEnd.isAfter(avEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] getValidStartIndices(BitSet availableSlots, int horizon, int duration) {
        // Find all indices i such that slots [i, i+duration-1] are all set in
        // ----> availableSlots
        List<Integer> valid = new ArrayList<>();
        for (int i = 0; i <= horizon - duration; i++) {
            boolean fits = true;
            for (int j = 0; j < duration; j++) {
                if (!availableSlots.get(i + j)) {
                    fits = false;
                    break;
                }
            }
            if (fits) {
                valid.add(i);
            }
        }
        return valid.stream().mapToInt(Integer::intValue).toArray();
    }

    private List<StudySession> mergeSessions(List<StudySession> sessions) {
        if (sessions.isEmpty())
            return sessions;

        sessions.sort(Comparator.comparing(StudySession::getStartTime));

        List<StudySession> merged = new ArrayList<>();
        StudySession current = sessions.get(0);

        for (int i = 1; i < sessions.size(); i++) {
            StudySession next = sessions.get(i);

            if (current.getSubject().getSubject_id().equals(next.getSubject().getSubject_id()) &&
                    current.getEndTime().isEqual(next.getStartTime())) {
                // Merge
                current.setEndTime(next.getEndTime());
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private void maskSlotsWithPreference(BitSet slots, LocalDateTime start, int horizon,
            com.intelligent.intelligentstdyplanner.Model.TimePreference pref) {
        for (int i = 0; i < horizon; i++) {
            if (slots.get(i)) {
                LocalDateTime slotTime = start.plusMinutes((long) i * SLOT_MINUTES);
                if (!isPreferredTime(slotTime, pref)) {
                    slots.clear(i);
                }
            }
        }
    }

    private boolean isPreferredTime(LocalDateTime time,
            com.intelligent.intelligentstdyplanner.Model.TimePreference pref) {
        int hour = time.getHour();
        switch (pref) {
            case MORNING:
                return hour >= 5 && hour < 12;
            case AFTERNOON:
                return hour >= 12 && hour < 17;
            case EVENING:
                return hour >= 17 && hour < 21;
            case NIGHT:
                return hour >= 21 || hour < 5;
            default:
                return true;
        }
    }
}
