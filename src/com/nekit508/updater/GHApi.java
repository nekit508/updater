package com.nekit508.updater;

import arc.func.ConsT;
import arc.struct.Seq;
import arc.util.Http;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;

import java.net.URL;

public class GHApi {
    public static JsonReader jsonReader = new JsonReader();

    public static void get(String url, ConsT<Http.HttpResponse, Exception> handler) {
        Http.get(url).block(handler);
    }

    public static Seq<RemoteFile> getAllFiles(String repo, String branch, Seq<RemoteFile> out) {
        get("https://api.github.com/repos/%s/git/trees/%s?recursive=1".formatted(repo, branch), r -> {
            JsonValue value = jsonReader.parse(r.getResultAsStream());

            for (JsonValue tree = value.get("tree").child(); tree != null; tree = tree.next()) {
                if (tree.get("type").asString().equals("blob")) {
                    String relative = tree.get("path").asString();
                    String url = "https://raw.githubusercontent.com/%s/%s/%s".formatted(repo, branch, relative);

                    out.add(new RemoteFile(new URL(url), relative));
                }
            }
        });

        return out;
    }

    public static JsonValue repoInfo(String repo) {
        JsonValue[] out = new JsonValue[1];
        Http.get("https://api.github.com/repos/%s".formatted(repo)).block(r -> {
            out[0] = jsonReader.parse(r.getResultAsStream());
        });
        return out[0];
    }

    public static JsonValue branchInfo(String repo, String branch) {
        JsonValue[] out = new JsonValue[1];
        Http.get("https://api.github.com/repos/%s/branches/%s".formatted(repo, branch)).block(r -> {
            out[0] = jsonReader.parse(r.getResultAsStream());
        });
        return out[0];
    }
}
