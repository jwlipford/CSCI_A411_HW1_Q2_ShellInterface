/*
 Operating Systems | Homework 1, Part 2 | Shell Interface | Jeffrey Lipford

 Instructions:
     The project will create a simple shell interface which accepts user commands and executes each
     command as an external process.  Your simple shell interface should be able to run both Unix
     command, MS-DOS command and window executable file.

 Basic Requirement:
  a. Creating the external process and executing the command with or without parameter, contents
     returned by the command should be displayed. Process that can run directly by Windows should
     be executed directly.
     
     Internal        |                |
     command of DOS? |  Unix Command  | MS-DOS Command
     ----------------+----------------+-----------------
     Yes             | ls             | dir
     Yes             | pwd            | cd
                     | cp file1 file2 | copy file1 file2
                     | rm file        | del file
                     | more file      | more file
                     | man            | help
                     | nano           | notepad
                     | grep           | find
                     | ping           | ping
                     | mkdir          | mkdir
                     | rmdir          | rmdir
                     | hostname       | hostname
                     | env            | set
                     | whoami         | whoami
                     | ifconfig       | ipconfig
                     | exit           | exit
    
  b. Display an error message if the command is not supported.
  
  c. Add a history feature. When user enters “history” command, the shell interface will output
     commands user has entered before.
     
  d. Terminate the shell when use entered “exit”.
*/

import java.util.*;

public class ShellInterface
{
	private static String[][] commandConversions
	// Each column in this array contains two elements: a command that cannot be directly executed
	// and a corresponding command that can be directly executed. If the user enters a command in
	// the 0th row, it is converted to the corresponding command in the 1st row. A Unix command
	// might be replaced with an MS-DOS command (for example, "nano" to "notepad"), and/or a
	// command that is not actually a process might have "cmd /c " inserted before it, resulting in
	// a process (for example, "dir" to "cmd /c dir").
	= new String[][]
	{
		{      "dir", "cmd /c dir"  },
		{       "cd", "cmd /c cd"   },
		{      "pwd", "cmd /c cd"   },
		{     "copy", "cmd /c copy" },
		{      "del", "cmd /c del"  },
		{     "more", "cmd /c more" },
		{      "set", "cmd /c set"  },
		{      "env", "cmd /c set"  },
		{      "man", "help"        },
		{     "nano", "notepad"     },
		{ "ifconfig", "ipconfig"    }
	};

	private static boolean tryExecuteCommand(String command, boolean verboseConvert)
	// Tries to execute the command. Converts the command using the commandConversions array if
	// possible. If the command was converted and if verboseConvert is true, prints the converted
	// command. Prints from the command's process's input stream and error stream if the command
	// can be executed. Returns true if it was able to be executed and false if not.
	{
		String[] commandTokens = command.split(" ");

		for (int i = 0; i < commandConversions.length; ++i)
		{
			if (commandConversions[i][0].equals(commandTokens[0]))
			{
				command = command.replaceFirst(commandTokens[0], commandConversions[i][1]);
				commandTokens = command.split(" ");
				if(verboseConvert)
					System.out.print(
						"Command converted to the following\nbash> " + command + '\n');
				break;
			}
		}

		ProcessBuilder pb = new ProcessBuilder(commandTokens);
		Process process = null;

		try
		{
			process = pb.start();
		}
		catch (Exception e0)
		{
			return false; // Command not supported
		}

		Scanner s = new Scanner(process.getInputStream());
		while (s.hasNextLine())
			System.out.println(s.nextLine());
		s.close();
		s = new Scanner(process.getErrorStream());
		while (s.hasNextLine())
			System.out.println(s.nextLine());
		s.close();
		System.out.println();

		return true; // Command supported
	}

	public static void main(String[] args)
	// Shell interface
	{
		Scanner s = new Scanner(System.in);
		ArrayList<String> history = new ArrayList<String>();

		while (true) // Until user enters "exit"
		{
			System.out.print("bash>");
			String command = s.nextLine();

			if (command.isEmpty())
				continue;
			if (command.equals("exit"))
			{
				s.close();
				return;
			}
			if (command.equals("history"))
				for (String h : history)
					System.out.println(' ' + h);
			else
			{
				// Try to execute command, and store success/failure in var executed.
				boolean executed = tryExecuteCommand(command, false);
				if (!executed)
					System.out.print("Command not supported\n\n");
			}

			history.add(command);
		}
	}
}
