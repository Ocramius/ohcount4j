package net.ohloh.ohcount4j.detect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ohloh.ohcount4j.Language;
import net.ohloh.ohcount4j.SourceFile;

public class ExtnHResolver implements Resolver {

    @Override
    public Language resolve(SourceFile sourceFile, List<String> filenames) throws IOException {
        Language result = null;

        if (result == null) {
            result = resolveByNearbyFiles(sourceFile, filenames);
        }
        if (result == null) {
            result = resolveByIncludes(sourceFile);
        }
        if (result == null) {
            result = resolveByKeywords(sourceFile);
        }
        if (result == null) {
            result = Language.C;
        }

        return result;
    }

    @Override
    public Language resolve(SourceFile sourceFile) throws IOException {
        return resolve(sourceFile, new ArrayList<String>());
    }

    @Override
    public boolean canResolve(Language language) {
        if (language == Language.C ||
                language == Language.CPP ||
                language == Language.OBJECTIVE_C) {
            return true;
        }
        return false;
    }

    /*
     * Does the source tree contain a *.m file to match this *.h file?
     * If so, likely Objective-C.
     */
    private Language resolveByNearbyFiles(SourceFile source, List<String> filenames) {

        String path_with_m = source.getPath().replaceFirst("\\.h$", ".m");

        if (filenames.contains(path_with_m)) {
            return Language.OBJECTIVE_C;
        } else {
            return null;
        }
    }

    private static Pattern cppKeywordsPattern = Pattern.compile(
            "\\b(?:class|namespace|template|typename)\\b", Pattern.MULTILINE
            );

    private Language resolveByKeywords(SourceFile source) throws IOException {
        Matcher m = cppKeywordsPattern.matcher(new String(source.getContents()));
        if (m.find()) {
            return Language.CPP;
        } else {
            return null;
        }
    }

    private static Pattern includePattern = Pattern.compile(
            "^#include\\s*(?:<|\")([\\w\\.\\/]+)(?:>|\")", Pattern.MULTILINE
            );

    /*
     * Returns the names of all libraries #included in this file
     */
    public List<String> findIncludes(SourceFile source) throws IOException {
        ArrayList<String> result = new ArrayList<String>();

        Matcher m = includePattern.matcher(new String(source.getContents()));
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }

    /*
     * Look for headers which are used by C++ only
     */
    private Language resolveByIncludes(SourceFile source) throws IOException {
        for (String include : findIncludes(source)) {
            if (cppIncludes.contains(include)) {
                return Language.CPP;
            }
        }
        return null;
    }

    @SuppressWarnings("serial")
    private static Set<String> cppIncludes = new HashSet<String>() {
        {
            add("string");
            add("algorithm");
            add("array");
            add("bitset");
            add("cassert");
            add("ccomplex");
            add("cctype");
            add("cerrno");
            add("cfenv");
            add("cfloat");
            add("cinttypes");
            add("ciso646");
            add("climits");
            add("clocale");
            add("cmath");
            add("csetjmp");
            add("csignal");
            add("cstdarg");
            add("cstdbool");
            add("cstddef");
            add("cstdint");
            add("cstdio");
            add("cstdlib");
            add("cstring");
            add("ctgmath");
            add("ctime");
            add("cwchar");
            add("cwctype");
            add("deque");
            add("exception");
            add("fstream");
            add("functional");
            add("iomanip");
            add("ios");
            add("iosfwd");
            add("iostream");
            add("istream");
            add("iterator");
            add("limits");
            add("list");
            add("locale");
            add("map");
            add("memory");
            add("new");
            add("numeric");
            add("ostream");
            add("queue");
            add("random");
            add("regex");
            add("set");
            add("sstream");
            add("stack");
            add("stdexcept");
            add("streambuf");
            add("string");
            add("system_error");
            add("tuple");
            add("type_traits");
            add("typeinfo");
            add("unordered_map");
            add("unordered_set");
            add("utility");
            add("valarray");
            add("vector");
            add("tr1/array");
            add("tr1/ccomplex");
            add("tr1/cctype");
            add("tr1/cfenv");
            add("tr1/cfloat");
            add("tr1/cinttypes");
            add("tr1/climits");
            add("tr1/cmath");
            add("tr1/complex");
            add("tr1/cstdarg");
            add("tr1/cstdbool");
            add("tr1/cstdint");
            add("tr1/cstdio");
            add("tr1/cstdlib");
            add("tr1/ctgmath");
            add("tr1/ctime");
            add("tr1/cwchar");
            add("tr1/cwctype");
            add("tr1/memory");
            add("tr1/random");
            add("tr1/regex");
            add("tr1/tuple");
            add("tr1/type_traits");
            add("tr1/unordered_map");
            add("tr1/unordered_set");
            add("tr1/utility");
        }
    };
}