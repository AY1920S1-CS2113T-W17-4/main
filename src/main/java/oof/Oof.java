package oof;

import oof.model.module.Assessment;
import oof.model.module.Lesson;
import oof.model.module.Module;
import oof.model.module.Semester;
import oof.model.module.SemesterList;
import oof.model.task.TaskList;
import oof.command.Command;
import oof.exception.OofException;

import java.io.IOException;

/**
 * Represents a Personal Assistant bot. An Oof object corresponds to three other classes,
 * namely called Storage, Ui and TaskList.
 */
public class Oof {

    private Storage storage;
    private TaskList tasks;
    private SemesterList semesterList;
    private Semester semester;
    private Module module;
    private Lesson lesson;
    private Assessment assessment;
    private Ui ui;
    private Reminder reminder;

    /**
     * Constructor for Oof for instantiation of other classes Ui, Storage and TaskList.
     */
    public Oof() {
        ui = new Ui();
        storage = new Storage();
        reminder = new Reminder();
        try {
            semesterList = new SemesterList(storage.readSemesterList());
            semester = new Semester(storage.readModuleList());
            module = new Module(storage.readLessonList(), storage.readAssessmentList());
        } catch (IOException | OofException e) {
            semesterList = new SemesterList();
            semester = new Semester();
            module = new Module();
        }
        try {
            tasks = new TaskList(storage.readTaskList());
        } catch (IOException | OofException e) {
            tasks = new TaskList();
        }
    }

    public TaskList getArr() {
        return tasks;
    }

    /**
     * Executes command entered by user.
     *
     * @param line Command to be tested.
     * @throws OofException Exceptions thrown by Command classes.
     */
    public boolean executeCommand(String line) throws OofException {
        Command command = CommandParser.parse(line);
        command.execute(semesterList, tasks, ui, storage);
        return command.isExit();
    }

    /**
     * Runs the Personal Assistant.
     */
    private void run() {
        ui.hello();
        reminder.checkDeadline(tasks, ui, storage);
        boolean isExit = false;
        while (!isExit) {
            try {
                ui.printCommandPrompt();
                String line = ui.scanLine();
                isExit = executeCommand(line);
            } catch (OofException exception) {
                ui.printOofException(exception);
            }
        }
    }

    /**
     * This is the main method which makes use of run method.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new Oof().run();
    }
}