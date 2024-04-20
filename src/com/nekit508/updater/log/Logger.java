package com.nekit508.updater.log;

import arc.struct.Seq;
import arc.util.Log;

public class Logger implements Log.LogHandler {
    public Seq<String> end = new Seq<>(new String[]{"\n\r"});

    @Override
    public void log(Log.LogLevel level, String text) {
        System.out.print(Log.format((
                level == Log.LogLevel.debug ? "&lc&fb" :
                        level == Log.LogLevel.info ? "&fb" :
                                level == Log.LogLevel.warn ? "&ly&fb" :
                                        level == Log.LogLevel.err ? "&lr&fb" :
                                                "") + text + "&fr") + end.peek());
    }

    public String set(String str) {
        String out = end.peek();
        end.add(str);
        return out;
    }

    public String pop() {
        if (end.size > 1) {
            return end.pop();
        } else {
            throw new RuntimeException("Only one element remained in end queue.");
        }
    }
}
