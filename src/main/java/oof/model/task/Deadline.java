package oof.model.task;

/**
 * Represents a Task object. A Deadline object is a type of Task.
 */
public class Deadline extends Task {

    protected String by;

    /**
     * Constructor for Deadline.
     *
     * @param description Details of the Task.
     * @param by          Due date and time of the Deadline.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    public String getBy() {
        return by;
    }

    /**
     * Converts a task object to string format for storage.
     * @return Task object in string format for storage.
     */
    @Override
    public String toStorageString() {
        String dateTime = by;
        String date = dateTime.split(" ")[DATE];
        String time = dateTime.split(" ")[TIME];
        return "D" + DELIMITER + getStatusIcon() + DELIMITER + description + DELIMITER + date
                + DELIMITER + time + DELIMITER + DELIMITER;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}