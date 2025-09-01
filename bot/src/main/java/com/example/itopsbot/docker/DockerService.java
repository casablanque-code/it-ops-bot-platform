package com.example.itopsbot.docker;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DockerService {

    private String dockerBin() {
        return System.getenv().getOrDefault("DOCKER_BIN", "docker");
    }

    // Оставляем ту же сигнатуру, чтобы ничего не трогать в боте
    public String getDockerStatus() {
        List<ContainerInfo> list = listContainersAll();
        if (list.isEmpty()) return "Нет контейнеров.";
        return formatTable(list); // HTML <pre> + эмодзи-состояния
    }

    // --- управление пригодится, если решим добавить кнопки ---
    public String start(String name) { return execSimple("start", name); }
    public String stop(String name) { return execSimple("stop", name); }
    public String restart(String name) { return execSimple("restart", name); }

    // ---- внутрянка ----
    public static class ContainerInfo {
        public final String name;
        public final String status; // "Up 3 hours" / "Exited (0) ..."
        public final String image;
        public final String state;  // running|exited|paused|restarting|created|dead
        public ContainerInfo(String name, String status, String image, String state) {
            this.name = name; this.status = status; this.image = image; this.state = state;
        }
    }

    public List<ContainerInfo> listContainersAll() {
        List<ContainerInfo> out = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(
                dockerBin(), "ps", "-a",
                "--format", "{{.Names}}\t{{.Status}}\t{{.Image}}\t{{.State}}"
        );
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        out.add(new ContainerInfo(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            }
            p.waitFor();
        } catch (Exception ignored) {}
        return out;
    }

    public String formatTable(List<ContainerInfo> list) {
        final int nameW = 17;
        final int stateW = 12;
        //final int imageW = 28;

        StringBuilder sb = new StringBuilder();
        sb.append("<pre>");
        sb.append(pad("NAME", nameW)).append(pad("STATE", stateW)).append("STATUS\n");
        for (ContainerInfo c : list) {
            String emoji = switch (c.state) {
                case "running" -> "🟢";
                case "stoped" -> "🔴";
                case "paused" -> "⏸";
                case "restarting" -> "🟡";
                case "created" -> "⚪";
                default -> "❔";
            };
            sb.append(pad(escape(c.name), nameW))
              .append(pad(emoji + " " + c.state, stateW))
              //.append(pad(escape(trim(c.image, imageW - 1)), imageW))
              .append(escape(c.status))
              .append("\n");
        }
        sb.append("</pre>");
        return sb.toString();
    }

    private String execSimple(String cmd, String name) {
        ProcessBuilder pb = new ProcessBuilder(dockerBin(), cmd, name);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String out;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                out = br.readLine();
            }
            int code = p.waitFor();
            if (code == 0) return (out != null ? out : "OK");
            return cmd + " " + name + " failed (code=" + code + ")";
        } catch (IOException | InterruptedException e) {
            return cmd + " " + name + ": " + e.getMessage();
        }
    }

    private static String pad(String s, int w) {
        if (s == null) s = "";
        int len = s.codePointCount(0, s.length());
        if (len >= w) return s;
        return s + " ".repeat(w - len);
    }

    // private static String trim(String s, int w) {
    //     if (s == null) return "";
    //     int len = s.codePointCount(0, s.length());
    //     if (len <= w) return s;
    //     return s.substring(0, s.offsetByCodePoints(0, w - 1)) + "…";
    // }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
