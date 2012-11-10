package org.pa.imgpag;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ImgPag {

	public static void main(String[] args) {

		// parse commandlines
		boolean areArgsValid = true;
		boolean printHelp = false;

		Options cliOptions = new Options();
		cliOptions.addOption(new Option("h", "help", false,
				"prints help page to stdout"));
		cliOptions.addOption(new Option("d", "directorx", true,
				"Directory with all the images"));

		PosixParser cliParser = new PosixParser();
		String imgDirPath = null;
		Path imgDir = null;

		try {
			CommandLine cli = cliParser.parse(cliOptions, args);
			printHelp = cli.hasOption("h");
			imgDirPath = cli.getOptionValue("d", ".");
			if (imgDirPath == null) {
				imgDir = Paths.get(imgDirPath);
			}
		} catch (InvalidPathException e) {
			err.println("Invalid path: " + imgDirPath);
			areArgsValid = false;
		} catch (ParseException e) {
			areArgsValid = false;
			printHelp = true;
		}

		if (printHelp) {
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter
					.printHelp(
							new PrintWriter(out),
							80,
							"usage: imgpag [OPTIONS]",
							"ImgPag is a tiny tool to create simple gallery pages of images",
							cliOptions, 4, 4, "");
		}

		if (areArgsValid) {
		}
	}

}
