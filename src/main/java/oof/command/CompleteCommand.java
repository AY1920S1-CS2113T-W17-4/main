package oof.command;

import oof.model.module.SemesterList;
import oof.Ui;
import oof.Storage;
import oof.exception.OofException;
import oof.model.task.Task;
import oof.model.task.TaskList;

/**
 * Represents a Command to mark Task as done.
 */
public class CompleteCommand extends Command {

    private int index;

    /**
     * Constructor for CompleteCommand.
     *
     * @param index Represents the index of the Task to be marked as done.
     */
    public CompleteCommand(int index) {
        super();
        this.index = index;
    }

    /**
     * Marks the specific Task defined by the user as done
     * after confirming the validity of the Command inputted by the user.
     * @param semesterList Instance of SemesterList that stores Semester objects.
     * @param taskList     Instance of TaskList that stores Task objects.
     * @param ui           Instance of Ui that is responsible for visual feedback.
     * @param storage      Instance of Storage that enables the reading and writing of Task
     */
    @Override
    public void execute(SemesterList semesterList, TaskList taskList, Ui ui, Storage storage) {
        try {
            if (!taskList.isIndexValid(this.index)) {
                throw new OofException("OOPS!!! The index is invalid.");
            } else if (taskList.getTask(this.index).getStatus()) {
                throw new OofException("OOPS!!! The task is already marked as done.");
            } else {
                Task task = taskList.getTask(this.index);
                task.setStatus();
                ui.completeMessage(task);
                storage.writeTaskList(taskList);
                storage.checkDone(task.getStatusIcon());
            }
        } catch (OofException e) {
            ui.printOofException(e);
        }
    }

}
