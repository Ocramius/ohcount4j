package net.ohloh.ohcount4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ohloh.ohcount4j.detect.Detector;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Ohcount {

    public static void main(String[] args) {
        final OhcountOptions opts = new OhcountOptions();
        final CmdLineParser optParser = new CmdLineParser(opts);
        try {
            optParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Error parsing options - " + e.getMessage());
            System.exit(-1);
        }

        if (opts.help) {
            optParser.printUsage(System.out);
            System.exit(0);
        }

        if (opts.supportedLanguages) {
            printSupportedLanguages();
            System.exit(0);
        }

        // Count the current directory by default
        if (opts.targets.size() == 0) {
            opts.targets.add(".");
        }

        try {
            FileFinder ff = new FileFinder();
            for (String path : opts.targets) {
                ff.addPath(path);
            }
            ArrayList<File> files = ff.getFiles();

            if (opts.annotate) {
                annotate(files, getFilenames(files));
            } else if (opts.detect) {
                detect(files, getFilenames(files));
            } else {
                summarize(files, getFilenames(files));
            }
            System.exit(0);
        } catch (OhcountException e) {
            System.err.println("Error - " + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error - " + e.getMessage());
            System.exit(-1);
        }
    }

    static void printSupportedLanguages() {
        String formattedString = "  %-18s%-18s\n";
        System.out.println("Ohcount4j supported languages\n");
        System.out.println("----------------------------------");
        System.out.format(formattedString, "Name", "Nice name");
        System.out.println("----------------------------------");
        for (Language language : Language.values()) {
            System.out.format(formattedString, language.uname(), language.niceName());
        }
    }

    static void annotate(List<File> files, List<String> filenames) throws IOException {
        AnnotationWriter handler = new AnnotationWriter();
        for (File file : files) {
            try (SourceFile sourceFile = new SourceFile(file)) {
                Language language = Detector.detect(sourceFile, filenames);
                if (language != null) {
                    language.makeScanner().scan(sourceFile, handler);
                }
            }
        }
    }

    static void detect(List<File> files, List<String> filenames) throws IOException {
        for (File file : files) {
            try (SourceFile sourceFile = new SourceFile(file)) {
                Language language = Detector.detect(sourceFile, filenames);
                if (language != null) {
                    System.out.printf("%s\t%s\n", language.niceName(), file.getPath());
                }
            }
        }
    }

    static void summarize(List<File> files, List<String> filenames) throws IOException {
        new ThreadedFileListCounter(4).count(files, filenames).print();
        // new FileListCounter().count(files, filenames).print();
    }

    static List<String> getFilenames(List<File> files) {
        ArrayList<String> result = new ArrayList<String>();
        for (File file : files) {
            result.add(file.getPath());
        }
        return result;
    }

    static class OhcountOptions {
        @Argument(metaVar = "[file]", usage = "target")
        List<String> targets = new ArrayList<String>();

        @Option(name = "-h", usage = "display this message")
        boolean help = false;

        @Option(name = "-s", usage = "show line count summary (default)")
        boolean summary = true;

        @Option(name = "-a", usage = "show annotated source code")
        boolean annotate = false;

        @Option(name = "-d", usage = "show detected file types only")
        boolean detect = false;

        @Option(name = "-l", usage = "show supported languages")
        boolean supportedLanguages = false;
    }

}
