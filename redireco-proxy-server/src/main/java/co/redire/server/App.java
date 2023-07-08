package co.redire.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {
	
	public static final String SPRING_BOOT_SERVER_PORT_OPTION_PREFIX = "--server.port=";
	
	public static final String CLI_OPTION_PORT = "p";
	public static final String CLI_OPTION_URL = "u";
	public static final String CLI_OPTION_URL_PARAM = "up";
	public static final String CLI_OPTION_HELP = "h";
	public static final String CLI_OPTION_DEBUG = "d";
	
	public static final int DEFAULT_PORT = 8765;
	public static final boolean DEFAULT_DEBUG = false;
	public static final String DEFAULT_URL = null;
	public static final String DEFAULT_URL_PARAMETER = "url";
	
	public static int port = DEFAULT_PORT;
	public static boolean debug = DEFAULT_DEBUG;
	public static String url = DEFAULT_URL;
	public static String urlParameter = DEFAULT_URL_PARAMETER;
	
    public static void main(String[] args) throws Exception {

    	// Define the options for this application
    	Options options = new Options();
    	options.addOption(Option.builder().option(CLI_OPTION_PORT).longOpt("port").hasArg().type(Integer.class).desc("The port this proxy server listens on.").build());
    	options.addOption(Option.builder().option(CLI_OPTION_URL).longOpt("url").hasArg().type(String.class).desc("The hard-coded URL this proxy server forwards to. If set then url-param will be ignored.").build());
    	options.addOption(Option.builder().option(CLI_OPTION_URL_PARAM).longOpt("url-param").hasArg().type(String.class).desc("The request parameter that contains the URL to forward to. Default is 'url'.").build());
    	options.addOption(Option.builder().option(CLI_OPTION_DEBUG).longOpt("debug").type(Boolean.class).desc("Whether to output debug information.").build());
    	options.addOption(Option.builder().option(CLI_OPTION_HELP).longOpt("help").desc("Display usage information.").build());
    	
    	// Parse / validate the options and display help if necessary
    	CommandLine commandLine;
    	try {
        	commandLine = DefaultParser.builder().build().parse(options, args);
        	if (commandLine.hasOption(CLI_OPTION_HELP)) {
        		throw new ParseException("The following options are available:");
        	}
		} catch (ParseException ex) {
			System.out.println(ex.getMessage());
			new HelpFormatter().printHelp(" ", options);
			return;
		}
    	
    	// Manage our options
    	List<String> springApplicationArgs = new ArrayList<>();
    	if (commandLine.hasOption(CLI_OPTION_PORT)) {
    		springApplicationArgs.add(SPRING_BOOT_SERVER_PORT_OPTION_PREFIX + commandLine.getOptionValue(CLI_OPTION_PORT));
    		port = Integer.valueOf(commandLine.getOptionValue(CLI_OPTION_PORT));
    	}
    	if (commandLine.hasOption(CLI_OPTION_DEBUG)) {
    		debug = true;
    	}
    	if (commandLine.hasOption(CLI_OPTION_URL)) {
    		url = commandLine.getOptionValue(CLI_OPTION_URL);
    	}
    	if (commandLine.hasOption(CLI_OPTION_URL_PARAM)) {
    		urlParameter = commandLine.getOptionValue(CLI_OPTION_URL_PARAM);
    	}
    	
    	// Start our application
    	try {
        	SpringApplication.run(App.class, springApplicationArgs.toArray(new String[0]));
    	} catch (Exception ex) {
    		System.out.println("An unexpected error occurred. Start with option " + "-" + CLI_OPTION_DEBUG + " for more detailed information.");
    		System.out.println("Error: " + ex.getMessage());
    		if (debug) {
    			ex.printStackTrace();
    		}
    	}

    }
    
	@Override
	public void run(String... args) throws Exception {
		
		// Provide some feedback
		System.out.println("Server started on port " + port);
		if (App.url != null) {
			System.out.println("All requests sent to http://localhost:" + port + "/proxy will be proxied to " + url + "");			
		} else {
			System.out.println("All requests sent to http://localhost:" + port + "/proxy?url={destination} will be proxied to the destination supplied in the " + "'" + urlParameter + "'" + " parameter");	
		}
		
    	// Configure shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Server shutdown gracefully");
			}
		}));
		
	}
	
}
