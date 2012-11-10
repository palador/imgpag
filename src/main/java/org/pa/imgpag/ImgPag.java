package org.pa.imgpag;

import static java.lang.System.err;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walkFileTree;
import static java.util.Collections.sort;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;

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
			imgDir = Paths.get(imgDirPath);
			if (!isDirectory(imgDir)) {
				throw new InvalidPathException(imgDirPath, "no a directory");
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
			helpFormatter.printHelp("imgpag [OPTIONS]", cliOptions);
		}

		if (areArgsValid) {
			try {
				final ArrayList<Path> imgFiles = new ArrayList<Path>();
				walkFileTree(imgDir, new FileVisitor<Path>() {

					Path root;

					@Override
					public FileVisitResult postVisitDirectory(Path dir,
							IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) throws IOException {
						if (root == null || root.equals(dir)) {
							root = dir;
							return FileVisitResult.CONTINUE;
						} else {
							return FileVisitResult.SKIP_SUBTREE;
						}
					}

					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						if (file.toString().toLowerCase().endsWith(".jpg")) {
							imgFiles.add(file);
						}
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file,
							IOException exc) throws IOException {
						throw exc;
					}
				});

				sort(imgFiles, new Comparator<Path>() {

					@Override
					public int compare(Path arg0, Path arg1) {
						return arg0.getFileName().toString()
								.compareTo(arg1.getFileName().toString());
					}
				});

				StringBuilder indexHtml = new StringBuilder();
				indexHtml
						.append("<html>\n    <head>\n        <title>gallery</title>\n    </head>\n    <body>\n");
				for (Path file : imgFiles) {
					String name = file.getFileName().toString();
					String nameWOutExt = name.substring(0,
							name.lastIndexOf('.'));
					indexHtml.append("        <a href=\"").append(name)
							.append("\">")
							.append(nameWOutExt).append("</a><br>\n");
				}
				indexHtml.append("    </body>\n</html>");
				System.out.println(indexHtml);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
