package br.net.silva.daniel.stream.load.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class FileProcessingService {

    private final DatabaseService databaseService;

    public Flux<String> processFile(List<String> lines) {
        return Flux.fromIterable(lines)
                .filter(line -> !line.isBlank())
                .flatMap(this::validateAndProcessLine);
    }

    public Mono<String> validateAndProcessLine(String line) {
        return databaseService.validateLine(line)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just("Processed: " + line);
                    } else {
                        return Mono.error(new RuntimeException("Invalid line: " + line));
                    }
                })
                .onErrorResume(e -> Mono.just("Error processing: " + line));
    }
}
