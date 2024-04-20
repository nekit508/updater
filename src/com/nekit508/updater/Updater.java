package com.nekit508.updater;

import arc.files.Fi;
import arc.func.Cons;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;
import com.nekit508.updater.log.Logger;
import com.nekit508.updater.parts.Copy;
import com.nekit508.updater.parts.Part;

public class Updater {
    public static StringMap args = new StringMap();
    public static Seq<Part> parts = new Seq<>();

    public static String repo, branch;

    public static Fi localRoot;
    public static Fi configDir;

    public static JsonReader jsonReader = new JsonReader();
    public static Logger logger = new Logger();

    public static Seq<ArgParser> parsers = Seq.with(
            new ArgParser("repo", r -> repo = r, () -> {
                throw new RuntimeException("Repo arg must be defined.");
            }),
            new ArgParser("branch", b -> branch = b, () -> {
                throw new RuntimeException("Branch arg must be defined.");
            }),
            new ArgParser("root", r -> localRoot = new Fi(r), () -> localRoot = new Fi(""))
            );

    public static void main(String[] args) {
        Log.logger = logger;

        new Seq<>(args).each(s -> {
            if (s.startsWith("-") && s.contains("=")) {
                int sep = s.indexOf("=");
                Updater.args.put(s.substring(1, sep), s.substring(sep+1));
            }
        });

        parts.add(new Copy());

        // setup (settings resolving and arguments parsing)
        parseArgs();
        parts.each(Part::setup);

        // prepare (files resolving)
        parts.each(Part::prepare);

        // handle (files download)
        parts.each(Part::handle);

        // post process
        parts.each(Part::postProcess);
    }

    public static void parseArgs() {
        parsers.each(parser -> {
            if (args.containsKey(parser.arg))
                parser.handler.get(args.get(parser.arg));
            else
                parser.dontExists.run();
        });

        configDir = localRoot.child(".updater");
        configDir.mkdirs();
    }

    static class ArgParser {
        public String arg;
        public Cons<String> handler;
        public Runnable dontExists = () -> {};

        public ArgParser(String a, Cons<String> h) {
            arg = a;
            handler = h;
        }

        public ArgParser(String a, Cons<String> h, Runnable d) {
            this(a, h);
            dontExists = d;
        }
    }
}
