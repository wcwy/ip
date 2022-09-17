package duke.storage;

import duke.Duke;
import duke.command.Menu;
import duke.data.TaskList;
import duke.exception.DukeException;
import duke.data.task.Deadline;
import duke.data.task.Event;
import duke.data.task.Task;
import duke.data.task.Todo;
import duke.exception.StorageInitializationException;
import duke.exception.StorageOutputException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Storage {
    private String filePath;
    private TaskList storedTaskList;

    public Storage(String filePath) {
        this.filePath = filePath;
        this.storedTaskList = new TaskList();
    }

    // Start of task initialization
    public TaskList initialize() throws DukeException {
        openDukeFile();
        return storedTaskList;
    }

    public void openDukeFile() throws DukeException {
        if (Files.exists(Paths.get(filePath))) {
            File dukeFile = new File(filePath);
            safeReadDukeFile(dukeFile);
        }
    }

    private void safeReadDukeFile(File dukeFile) throws DukeException {
        try {
            Scanner in = new Scanner(dukeFile);
            readDukeFileAndInitializeTask(in);
        } catch (FileNotFoundException exception) {
            throw new StorageInitializationException();
        } catch (DukeException exception) {
            throw new StorageInitializationException();
        }
    }

    private void readDukeFileAndInitializeTask(Scanner in) throws DukeException {
        while (in.hasNext()) {
            String line = in.nextLine();
            String[] splits = line.split(" \\| ");
            initializeTask(splits);
        }
    }

    private void initializeTask(String[] splits) throws DukeException {
        switch (splits[0]) {
        case "T":
            storedTaskList.addTask("todo", splits[2], true);
            break;
        case "D":
            storedTaskList.addTask("deadline", splits[2] + " /by " + splits[3], true);
            break;
        case "E":
            storedTaskList.addTask("event", splits[2] + " /at " + splits[3], true);
            break;
        default:
            break;
        }
        if (splits[1].equals("1")) {
            storedTaskList.mark(Integer.toString(storedTaskList.getTasks().size()), true);
        }
    }

    // End of task initialization

    public void rewriteDukeFile(TaskList taskList) throws DukeException {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                File dukeFile = new File(filePath);
                dukeFile.createNewFile();
            }

            FileWriter dukeFileWriter = new FileWriter(filePath, false);
            for (Task task : taskList.getTasks()) {
                String output = retrieveTaskInformationForFileStorage(task);
                dukeFileWriter.append(output);
            }
            dukeFileWriter.close();
        } catch (IOException exception){
            throw new StorageOutputException();
        }
    }

    public void appendDukeFile(TaskList taskList) throws DukeException {
        try {
            FileWriter dukeFileWriter;
            if (Files.exists(Paths.get(filePath))) {
                dukeFileWriter = new FileWriter(filePath, true);
            } else {
                File dukeFile = new File(filePath);
                dukeFile.createNewFile();
                dukeFileWriter = new FileWriter(filePath, false);
            }

            Task newTask = taskList.getTasks().get(taskList.getTasks().size() - 1);
            String output = retrieveTaskInformationForFileStorage(newTask);
            dukeFileWriter.append(output);
            dukeFileWriter.close();
        } catch (IOException exception) {
            throw new StorageOutputException();
        }
    }

    private static String retrieveTaskInformationForFileStorage(Task task) {
        String output = "";
        if (task instanceof Todo) {
            output = String.format("%s | %s | %s", "T", task.isDone() ? "1" : "0", task.getTaskName());
        }
        if (task instanceof Deadline) {
            output = String.format("%s | %s | %s | %s", "D", task.isDone() ? "1" : "0",
                    task.getTaskName(), ((Deadline) task).getDeadlineTime());
        }
        if (task instanceof Event) {
            output = String.format("%s | %s | %s | %s", "E", task.isDone() ? "1" : "0",
                    task.getTaskName(), ((Event) task).getEventTime());
        }
        output += System.lineSeparator();
        return output;
    }
}
