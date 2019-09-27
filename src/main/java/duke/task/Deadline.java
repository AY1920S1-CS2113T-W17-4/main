package duke.task;

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
     * Customises the toString() method to print the Deadline object.
     *
     * @return Customised String for Deadline object.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}