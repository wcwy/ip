package duke.command;

import duke.data.TaskList;
import duke.data.task.Task;
import duke.exception.DukeException;
import duke.storage.Storage;
import duke.ui.Ui;

/**
 * <code>MarkCommand</code> is the command that marks a task as done based on an index given.
 */
public class MarkCommand extends ModifyCommand {
    public static final String COMMAND_WORD = "MARK";

    public MarkCommand() {
        super();
    }

    /**
     * Checks that the task index is within the valid range of task list and marks the task as done.
     * Display a mark as done successful message to user.
     * Rewrite the file storage based on new task list.
     *
     * @param taskList List of tasks stored in current execution.
     * @param ui       User interface to display messages.
     * @param storage  File storage to read, append or rewrite file.
     * @throws DukeException Exception triggered on invalid user input or erroneous file operation.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws DukeException {
        checkTaskIndexRange(taskIndex, taskList.getSize());
        Task task = taskList.markTask(taskIndex);
        ui.displayTaskMarkedMessage(task.getTaskName());
        storage.rewriteDukeFile(taskList);
    }
}
