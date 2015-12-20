package amidst;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.crash.CrashWindow;
import amidst.logging.FileLogger;
import amidst.logging.Log;

@NotThreadSafe
public class Amidst {
	private static final String UNCAUGHT_EXCEPTION_ERROR_MESSAGE = "Amidst has encounted an uncaught exception on thread: ";
	private static final String COMMAND_LINE_PARSING_ERROR_MESSAGE = "There was an issue parsing command line parameters.";
	private static final CommandLineParameters PARAMETERS = new CommandLineParameters();

	@CalledOnlyBy(AmidstThread.STARTUP)
	public static void main(String args[]) {
		initUncaughtExceptionHandler();
		parseCommandLineArguments(args);
		initLogger();
		initLookAndFeel();
		setJava2DEnvironmentVariables();
		startApplication();
	}

	private static void initUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				handleCrash(e, UNCAUGHT_EXCEPTION_ERROR_MESSAGE + thread);
			}
		});
	}

	private static void parseCommandLineArguments(String[] args) {
		try {
			new CmdLineParser(PARAMETERS).parseArgument(args);
		} catch (CmdLineException e) {
			Log.w(COMMAND_LINE_PARSING_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static void initLogger() {
		if (PARAMETERS.logPath != null) {
			Log.addListener("file",
					new FileLogger(new File(PARAMETERS.logPath)));
		}
	}

	private static void initLookAndFeel() {
		if (!isOSX()) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				Log.printTraceStack(e);
			}
		}
	}

	private static boolean isOSX() {
		return System.getProperty("os.name").contains("OS X");
	}

	private static void setJava2DEnvironmentVariables() {
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("sun.java2d.accthreshold", "0");
	}

	private static void startApplication() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				doStartApplication();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void doStartApplication() {
		try {
			new Application(PARAMETERS).run();
		} catch (Exception e) {
			handleCrash(e, "Amidst crashed!");
		}
	}

	@CalledByAny
	private static void handleCrash(Throwable e, String message) {
		try {
			Log.crash(e, message);
			displayCrashWindow(message, Log.getAllMessages());
		} catch (Throwable t) {
			System.err.println("Amidst crashed!");
			System.err.println(message);
			e.printStackTrace();
		}
	}

	private static void displayCrashWindow(final String message,
			final String allMessages) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CrashWindow(message, allMessages, new Runnable() {
					@Override
					public void run() {
						System.exit(4);
					}
				});
			}
		});
	}
}