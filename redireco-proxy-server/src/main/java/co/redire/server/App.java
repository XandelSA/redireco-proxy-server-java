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

import co.redire.server.configuration.ConfigBean;

@SpringBootApplication
public class App implements CommandLineRunner {
	
	public static final String SPRING_BOOT_SERVER_PORT_OPTION_PREFIX = "--server.port=";
	
	public static final String CLI_OPTION_PORT = "p";
	public static final String CLI_OPTION_URL = "u";
	public static final String CLI_OPTION_URL_PARAM = "up";
	public static final String CLI_OPTION_HELP = "h";
	public static final String CLI_OPTION_DEBUG = "d";
	
	public static ConfigBean config = new ConfigBean();
	
    public static void main(String[] args) throws Exception {

    	// Define the options for this application
    	Options options = new Options();
    	options.addOption(Option.builder().option(CLI_OPTION_PORT).longOpt("port").hasArg().type(Integer.class).desc("Optional. The port this proxy server listens on. Default is " + ConfigBean.DEFAULT_PORT + ".").build());
    	options.addOption(Option.builder().option(CLI_OPTION_URL).longOpt("url").hasArg().type(String.class).desc("Optional. The hard-coded URL this proxy server forwards to. If set then url-param will be ignored. Default is " + ConfigBean.DEFAULT_URL_PARAMETER + ".").build());
    	options.addOption(Option.builder().option(CLI_OPTION_URL_PARAM).longOpt("url-param").hasArg().type(String.class).desc("Optional. The request parameter that contains the URL to forward to. Default is " + ConfigBean.DEFAULT_URL + ".").build());
    	options.addOption(Option.builder().option(CLI_OPTION_DEBUG).longOpt("debug").type(Boolean.class).desc("Optional. Whether to output debug information.").build());
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
    		config.setPort(Integer.valueOf(commandLine.getOptionValue(CLI_OPTION_PORT)));
    	}
    	if (commandLine.hasOption(CLI_OPTION_DEBUG)) {
    		config.setDebug(true);
    	}
    	if (commandLine.hasOption(CLI_OPTION_URL)) {
    		config.setUrl(commandLine.getOptionValue(CLI_OPTION_URL));
    	}
    	if (commandLine.hasOption(CLI_OPTION_URL_PARAM)) {
    		config.setUrlParameter(commandLine.getOptionValue(CLI_OPTION_URL_PARAM));
    	}

    	// Prepare the spring boot port option
    	springApplicationArgs.add(SPRING_BOOT_SERVER_PORT_OPTION_PREFIX + config.getPort());
    	
    	// Start our application
    	try {
        	SpringApplication.run(App.class, springApplicationArgs.toArray(new String[0]));
    	} catch (Exception ex) {
    		System.out.println("An unexpected error occurred. Start with option " + "-" + CLI_OPTION_DEBUG + " for more detailed information.");
    		System.out.println("Error: " + ex.getMessage());
    		if (config.isDebug()) {
    			ex.printStackTrace();
    		}
    	}

    }
    
	@Override
	public void run(String... args) throws Exception {
		
		// Provide some feedback
		System.out.println("Server started on port " + config.getPort());
		if (App.config.getUrl() != null) {
			System.out.println("All requests sent to http://localhost:" + config.getPort() + "/proxy will be proxied to " + config.getUrl() + "");			
		} else {
			System.out.println("All requests sent to http://localhost:" + config.getPort() + "/proxy?url={destination} will be proxied to the destination supplied in the " + "'" + config.getUrlParameter() + "'" + " parameter");	
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
