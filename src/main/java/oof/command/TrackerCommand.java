package oof.command;

import oof.Storage;
import oof.Ui;
import oof.exception.OofException;
import oof.model.module.SemesterList;
import oof.model.task.Assignment;
import oof.model.task.Task;
import oof.model.task.TaskList;
import oof.model.tracker.ModuleTracker;
import oof.model.tracker.ModuleTrackerList;
import oof.model.tracker.Tracker;
import oof.model.tracker.TrackerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TrackerCommand extends Command {

    String description;
    private static final long DEFAULT_TIMETAKEN = 0;
    private static final int ARGUMENT_FIRST = 0;
    private static final int ARGUMENT_SECOND = 1;
    private static final int ARGUMENT_THIRD = 2;
    private static final int SPLIT_INPUT = 3;
    private static final int SPLIT_DESCRIPTION = 2;
    private static final int EMPTY = 0;
    private static final int NOT_FOUND = -1;
    private static final String START_COMMAND = "/start";
    private static final String STOP_COMMAND = "/stop";
    private static final String PAUSE_COMMAND = "/pause";
    private static final String VIEW_COMMAND = "/view";

    public TrackerCommand(String description) {
        this.description = description;
    }

    /**
     * Invokes other Command subclasses based on the input given by the user.
     *
     * @param semesterList Instance of SemesterList that stores Semester objects.
     * @param taskList     Instance of TaskList that stores Task objects.
     * @param ui           Instance of Ui that is responsible for visual feedback.
     * @param storage      Instance of Storage that enables the reading and writing of Task
     *                     objects to hard disk.
     * @throws OofException Catches invalid commands given by user.
     */
    @Override
    public void execute(SemesterList semesterList, TaskList taskList, Ui ui, Storage storage) throws OofException {
        if (description.isEmpty()) {
            throw new OofException("Please enter your instructions!");
        }

        TrackerList trackerList = storage.readTrackerList();

        if (description.equals(VIEW_COMMAND)) {
            ModuleTrackerList moduleTrackerList = timeSpentByModule(trackerList);
            if (moduleTrackerList.getSize() == EMPTY) {
                throw new OofException("There are no completed Assignments!");
            }
            ModuleTrackerList sortedTL = sortAscending(moduleTrackerList);
            ui.printTrackerDiagram(sortedTL);
        } else {

            String[] input = description.split(" ", SPLIT_INPUT);
            if (input.length < SPLIT_INPUT) {
                throw new OofException("Invalid input!");
            }

            String instruction = input[ARGUMENT_FIRST].toLowerCase();
            String moduleCode = input[ARGUMENT_SECOND].toLowerCase();
            String moduleDescription = input[ARGUMENT_THIRD].toLowerCase();

            Tracker tracker = trackerList.findTrackerByDesc(moduleDescription, moduleCode);
            Assignment assignment = findAssignment(moduleDescription, moduleCode, taskList);
            if (assignment == null) {
                throw new OofException("Assignment does not exist!");
            }

            boolean isCompleted = isAssignmentCompleted(assignment);
            if (isCompleted) {
                throw new OofException("Assignment has already been completed.");

            }

            switch (instruction) {
            case START_COMMAND:
                if (tracker == null) {
                    tracker = addNewTracker(moduleDescription, moduleCode, taskList);
                    trackerList.addTracker(tracker);
                } else {
                    updateTrackerList(moduleDescription, moduleCode, trackerList);
                }
                storage.writeTrackerList(trackerList);
                ui.printStartAtCurrent(tracker);
                break;

            case STOP_COMMAND:
                if (!isStarted(tracker)) {
                    throw new OofException("Tracker for this Assignment has not started.");
                } else {
                    updateTimeTaken(tracker);
                    assignment.setStatus();
                    storage.writeTrackerList(trackerList);
                    storage.writeTaskList(taskList);
                    ui.printEndAtCurrent(tracker);
                }
                break;

            case PAUSE_COMMAND:
                if (!isStarted(tracker)) {
                    throw new OofException("Tracker for this Assignment has not started.");
                } else {
                    updateTimeTaken(tracker);
                    storage.writeTrackerList(trackerList);
                    ui.printPauseAtCurrent(tracker);
                }
                break;

            default:
                throw new OofException("Invalid Command!");
            }
        }
    }

    /**
     * Create a new Tracker object.
     *
     * @param moduleDescription     module description given by User.
     * @param moduleCode            module code given by User.
     * @param tasks                 TaskList object.
     * @return                      new Tracker object.
     */
    private Tracker addNewTracker(String moduleDescription, String moduleCode, TaskList tasks) {
        for (int i = 0; i < tasks.getSize(); i++) {
            Task t = tasks.getTask(i);
            if (t instanceof Assignment) {
                Date current = new Date();
                return new Tracker(moduleCode, moduleDescription, current, current, DEFAULT_TIMETAKEN);
            }
        }
        return null;
    }

    /**
     * Updated Assignment that have been tracked in the past.
     *
     * @param description   module description given by User.
     * @param moduleCode    module code given by user.
     * @param trackerList   TrackerList of Tracker objects.
     */
    private void updateTrackerList(String description, String moduleCode, TrackerList trackerList) {
        for (int i = 0; i < trackerList.getSize(); i++) {
            Tracker tracker = trackerList.getTracker(i);
            String currentDesc = tracker.getDescription();
            String currentModuleCode = tracker.getModuleCode().toLowerCase();

            if (description.equals(currentDesc) && moduleCode.equals(currentModuleCode)) {
                tracker.setLastUpdated(new Date());
                tracker.setStartDate(new Date());
                break;
            }
        }
    }

    /**
     * Update Tracker object TimeTaken property.
     *
     * @param tracker   Tracker object.
     */
    private void updateTimeTaken(Tracker tracker) {
        long totalTime = tracker.getTimeTaken();
        Date now = new Date();
        Date startDate = tracker.getStartDate();
        totalTime += Integer.parseInt(tracker.getDateDiff(startDate));
        tracker.updateTracker(totalTime, now);
    }

    /**
     * Check if tracker has been started.
     *
     * @param tracker   Tracker object.
     * @return          if Tracker object has a start date.
     */
    private boolean isStarted(Tracker tracker) {
        return tracker.getStartDate() != null;
    }

    /**
     * Check if Assignment is done.
     *
     * @param assignment    Assignment object.
     * @return              Assignment object status.
     */
    private boolean isAssignmentCompleted(Assignment assignment) {
        return assignment.getStatus();
    }

    /**
     * Find Assignment object in TaskList where descriptions match.
     *
     * @param moduleDescription     description of Assignment.
     * @param moduleCode            module code of Assignment.
     * @param taskList              TaskList of Task objects.
     * @return                      Assignment object that matches in both moduleDescription and moduleCode.
     */
    private Assignment findAssignment(String moduleDescription, String moduleCode, TaskList taskList) {
        for (int i = 0; i < taskList.getSize(); i++) {
            Task task = taskList.getTask(i);
            if (task instanceof Assignment) {
                Assignment assignment = (Assignment) task;
                String[] description = assignment.getDescription().toLowerCase().split(" ", SPLIT_DESCRIPTION);
                String currentDescription = description[1];
                String currentModuleCode = assignment.getModuleCode().toLowerCase();
                if (moduleDescription.equals(currentDescription) && moduleCode.equals(currentModuleCode)) {
                    return assignment;
                }
            }
        }
        return null;
    }

    /**
     * Calculate total time spent on various modules.
     * @param trackerList     list of Tracker objects.
     * @return          TrackerList of Tracker objects.
     */
    private ModuleTrackerList timeSpentByModule(TrackerList trackerList) {
        ModuleTrackerList moduleTrackerList = new ModuleTrackerList();
        Tracker tracker;

        for (int i = 0; i < trackerList.getSize(); i++) {
            tracker = trackerList.getTracker(i);
            String moduleCode = tracker.getModuleCode();
            ModuleTracker moduleTracker = updateModuleTrackerList(moduleTrackerList, tracker, moduleCode);
            moduleTrackerList.addModuleTracker(moduleTracker);
        }
        return moduleTrackerList;
    }

    /**
     * Update ModuleTracker object.
     * @param moduleTrackerList     ModuleTrackerList object.
     * @param tracker               Tracker object.
     * @param moduleCode            String containing module code.
     * @return                      created or updated ModuleTracker object.
     */
    private ModuleTracker updateModuleTrackerList(ModuleTrackerList moduleTrackerList,
                                                  Tracker tracker, String moduleCode) {
        ModuleTracker moduleTracker;
        long timeTaken = tracker.getTimeTaken();
        int i = matchModuleTracker(moduleTrackerList, moduleCode);

        if (tracker.getStartDate() != null) {
            long totalTime = tracker.getTimeTaken();
            Date startDate = tracker.getStartDate();
            totalTime += Integer.parseInt(tracker.getDateDiff(startDate));
            tracker.setTimeTaken(totalTime);
            timeTaken = tracker.getTimeTaken();
        }

        if (moduleTrackerList.getSize() == EMPTY || i == NOT_FOUND) {
            moduleTracker = new ModuleTracker(moduleCode,timeTaken);
        } else {
            moduleTracker = moduleTrackerList.getModuleTracker(i);
            long totalTime = moduleTracker.getTotalTimeTaken();
            totalTime += timeTaken;
            moduleTracker.setTotalTimeTaken(totalTime);
        }
        return moduleTracker;
    }

    /**
     * Check if ModuleTracker objects have the same ModuleCode.
     * @param moduleTrackerList     ModuleTrackerList of ModuleTracker objects.
     * @param moduleCode            Module code in process.
     * @return                      index of ModuleTracker object found in ModuleTrackerList that matches ModuleCode.
     */
    private int matchModuleTracker(ModuleTrackerList moduleTrackerList, String moduleCode) {
        for (int i = 0; i < moduleTrackerList.getSize(); i++) {
            ModuleTracker moduleTracker = moduleTrackerList.getModuleTracker(i);
            String savedModule = moduleTracker.getModuleCode();
            if (moduleCode.equals(savedModule)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * Sort trackerList by timeTaken in ascending order.
     * @param moduleTrackerList   ArrayList of Tracker objects.
     * @return              sorted TrackerList object.
     */
    private ModuleTrackerList sortAscending(ModuleTrackerList moduleTrackerList) {
        ArrayList<ModuleTracker> list = new ArrayList<>();
        for (int i = 0; i < moduleTrackerList.getSize(); i++) {
            ModuleTracker mt = moduleTrackerList.getModuleTracker(i);
            list.add(mt);
        }
        Collections.sort(list, moduleTrackerList.timeTimeComparator);
        ModuleTrackerList sortedTL = new ModuleTrackerList();
        for (ModuleTracker moduleTracker : list) {
            sortedTL.addModuleTracker(moduleTracker);
        }
        return sortedTL;
    }
}