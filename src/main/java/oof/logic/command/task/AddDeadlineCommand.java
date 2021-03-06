package oof.logic.command.task;

import java.util.ArrayList;

import oof.logic.command.Command;
import oof.ui.Ui;
import oof.commons.exceptions.command.CommandException;
import oof.commons.exceptions.command.InvalidArgumentException;
import oof.commons.exceptions.command.MissingArgumentException;
import oof.model.university.SemesterList;
import oof.model.task.Deadline;
import oof.model.task.TaskList;
import oof.storage.StorageManager;

/**
 * Represents a Command that appends a new Deadline
 * object to the TaskList.
 */
public class AddDeadlineCommand extends Command {

    public static final String COMMAND_WORD = "deadline";
    protected ArrayList<String> arguments;
    static final int INDEX_DESCRIPTION = 0;
    protected static final int INDEX_DATE = 1;
    static final int ARRAY_SIZE_DATE = 2;
    static final int DESCRIPTION_LENGTH_MAX = 20;


    /**
     * Constructor for AddDeadlineCommand.
     *
     * @param arguments Command inputted by user for processing.
     */
    public AddDeadlineCommand(ArrayList<String> arguments) {
        super();
        this.arguments = arguments;
    }

    /**
     * Adds a deadline task to taskList.
     *
     * @param semesterList   Instance of SemesterList that stores Semester objects.
     * @param taskList       Instance of TaskList that stores Task objects.
     * @param ui             Instance of Ui that is responsible for visual feedback.
     * @param storageManager Instance of Storage that enables the reading and writing of Task
     *                       objects to hard disk.
     * @throws CommandException if user input contains missing or invalid arguments.
     */
    @Override
    public void execute(SemesterList semesterList, TaskList taskList, Ui ui, StorageManager storageManager)
            throws CommandException {
        if (arguments.get(INDEX_DESCRIPTION).isEmpty()) {
            throw new MissingArgumentException("OOPS!!! The deadline needs a description.");
        } else if (arguments.size() < ARRAY_SIZE_DATE || arguments.get(INDEX_DATE).isEmpty()) {
            throw new MissingArgumentException("OOPS!!! The deadline needs a due date.");
        }
        String description = arguments.get(INDEX_DESCRIPTION);
        String date = parseDateTime(arguments.get(INDEX_DATE));
        if (exceedsMaxLength(description, DESCRIPTION_LENGTH_MAX)) {
            throw new InvalidArgumentException("OOPS!!! Task description exceeds maximum length of "
                    + DESCRIPTION_LENGTH_MAX + "!");
        } else if (!isDateValid(date)) {
            throw new InvalidArgumentException("OOPS!!! The due date is invalid.");
        } else {
            Deadline deadline = new Deadline(description, date);
            taskList.addTask(deadline);
            ui.addTaskMessage(deadline, taskList.getSize());
            storageManager.writeTaskList(taskList);
        }
    }
}
