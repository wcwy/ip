package duke;

import duke.command.Menu;
import duke.command.DukeFile;
import duke.exception.DukeException;
import duke.exception.InvalidCommandException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Duke {
    public static String[] splitInput(String userInput) {
        String[] split = userInput.split(" ", 2);
        if (split.length == 1) {
            return new String[]{split[0], ""};
        }
        return split;
    }

    public static void executeInstruction(Menu dukeMenu, String instruction, String inputValue) throws DukeException {
        switch (instruction) {
        case "list":
            dukeMenu.list();
            break;
        case "mark":
            dukeMenu.mark(inputValue, false);
            break;
        case "unmark":
            dukeMenu.unmark(inputValue);
            break;
        case "bye":
            dukeMenu.quit();
            break;
        case "todo":
            // Fallthrough
        case "deadline":
            // Fallthrough
        case "event":
            dukeMenu.addTask(instruction, inputValue, false);
            break;
        default:
            throw new InvalidCommandException();
        }
    }

    public static void saveFile(DukeFile dukeFile, Menu dukeMenu, String instruction) throws IOException {
        switch (instruction) {
        case "mark":
            // Fallthrough
        case "unmark":
            // Fallthrough
        case "delete":
            dukeFile.rewriteDukeFile(dukeMenu);
            break;
        case "todo":
            // Fallthrough
        case "deadline":
            // Fallthrough
        case "event":
            dukeFile.appendDukeFile(dukeMenu);
            break;
        default:
            // No action for other command or invalid command
            break;
        }
    }

    public static void safeExecuteInstruction(Menu dukeMenu, String instruction, String inputValue, DukeFile dukeFile) {
        try {
            executeInstruction(dukeMenu, instruction, inputValue);
            saveFile(dukeFile, dukeMenu, instruction);
        } catch (DukeException exception) {
            System.out.println(exception.getMessage());
        } catch (IOException exception){
            dukeMenu.displayErrorMessage();
        }
    }

    public static void main(String[] args) {
        Menu dukeMenu = new Menu();
        DukeFile dukeFile = new DukeFile();
        String userInput = "", instruction = "", inputValue = "";
        Scanner in = new Scanner(System.in);
        dukeFile.readDukeFile(dukeMenu);
        dukeMenu.greet();
        while (!instruction.equals("bye")) {
            userInput = in.nextLine();
            String[] splits = splitInput(userInput);
            instruction = splits[0];
            inputValue = splits[1];
            safeExecuteInstruction(dukeMenu, instruction, inputValue, dukeFile);
        }
    }
}
