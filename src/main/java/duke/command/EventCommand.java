package duke.command;

import duke.data.TaskList;
import duke.data.task.Task;
import duke.exception.DukeException;
import duke.exception.EventMissingDescriptionException;
import duke.exception.EventMissingTagException;
import duke.exception.EventMissingTimeException;
import duke.storage.Storage;
import duke.ui.Ui;

/**
 * <code>EventCommand</code> is the command that adds an event task into the task list.
 */
public class EventCommand extends AddCommand {
    public static final String COMMAND_WORD = "EVENT";
    private String eventTime;

    public EventCommand() {
        super();
    }

    /**
     * Check the validity of user input and set the event description and time of the event.
     *
     * @param parameterInput The extracted part of user input after the command entered.
     * @throws DukeException Exception triggered on invalid user input.
     */
    @Override
    public void checkAndSetParameters(String parameterInput) throws DukeException {
        checkParameters(parameterInput);
        setParameters(parameterInput);
    }

    /**
     * Check that user input contains a description, separator and a time.
     *
     * @param parameterInput The extracted part of user input after the command entered.
     * @throws DukeException Exception triggered on invalid user input.
     */
    @Override
    protected void checkParameters(String parameterInput) throws DukeException {
        if (!containsTimeSeparator(parameterInput, " /at ")) {
            throw new EventMissingTagException();
        }
        if (!containsTaskDescription(parameterInput, " /at ")) {
            throw new EventMissingDescriptionException();
        }
        if (!containsTaskTime(parameterInput, " /at ")) {
            throw new EventMissingTimeException();
        }
    }

    /**
     * Store the event description and time of the event.
     *
     * @param parameterInput The extracted part of user input after the command entered.
     */
    @Override
    protected void setParameters(String parameterInput) {
        String[] splits = splitTaskName(" /at ", parameterInput);
        super.taskName = splits[0];
        eventTime = splits[1];
    }

    /**
     * Create an event task and add it to the task list.
     * Display an addition successful message to user.
     * Append the event task to the file storage.
     *
     * @param taskList List of tasks stored in current execution.
     * @param ui       User interface to display messages.
     * @param storage  File storage to read, append or rewrite file.
     * @throws DukeException Exception triggered on erroneous file operation.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws DukeException {
        Task task = taskList.addEvent(super.taskName, eventTime);
        ui.displayTaskAdditionMessage(task.getTaskFullDetails(), taskList.getSize());
        storage.appendDukeFile(taskList);
    }
}
