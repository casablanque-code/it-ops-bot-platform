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
public String getDockerStatus() {
String dockerBin = System.getenv().getOrDefault("DOCKER_BIN", "docker");
ProcessBuilder pb = new ProcessBuilder(
dockerBin, "ps", "--format", "{{.Names}}\t{{.Status}}\t{{.Image}}"
);
pb.redirectErrorStream(true);
try {
Process p = pb.start();
List<String> lines = new ArrayList<>();
try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
String line;
while ((line = br.readLine()) != null) {
lines.add(line);
}
}
int code = p.waitFor();
if (code != 0) {
return "Не удалось выполнить docker ps (код=" + code + "). Проверьте, что docker доступен в контейнере и смонтирован /var/run/docker.sock.";
}
if (lines.isEmpty()) {
return "Нет запущенных контейнеров.";
}
StringBuilder sb = new StringBuilder();
sb.append("Имя\tСтатус\tОбраз\n");
int max = Math.min(lines.size(), 200); // защитимся от переполнения сообщения
for (int i = 0; i < max; i++) {
sb.append(lines.get(i)).append('\n');
}
if (lines.size() > max) {
sb.append("... и ещё ").append(lines.size() - max).append(" контейнеров");
}
return sb.toString();
} catch (IOException | InterruptedException e) {
return "Ошибка при выполнении docker ps: " + e.getMessage();
}
}
}