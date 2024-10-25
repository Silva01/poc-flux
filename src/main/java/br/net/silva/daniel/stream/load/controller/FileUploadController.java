package br.net.silva.daniel.stream.load.controller;

import br.net.silva.daniel.stream.load.service.FileProcessingService;
import br.net.silva.daniel.stream.load.service.RegistroService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class FileUploadController {

    private final FileProcessingService fileProcessingService;
    private final RegistroService registroService;

    @PostMapping("/load")
    public Mono<Void> uploadFile(@RequestPart("file") Flux<DataBuffer> file) {
        return file
//                .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
//                .flatMap(line -> Flux.fromArray(line.split("\n"))
                .flatMap(dataBuffer -> {
                    StringBuilder currentLine = new StringBuilder();
                    List<String> completeLines = new ArrayList<>();
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    String content = new String(bytes, StandardCharsets.UTF_8);

                    for (char c : content.toCharArray()) {
                        if (c == '\n') {
                            // Linha completa encontrada, adiciona à lista
                            completeLines.add(currentLine.toString());
                            currentLine.setLength(0); // Reseta o buffer de linha
                        } else {
                            // Continua construindo a linha
                            currentLine.append(c);
                        }
                    }

                    // Armazena a linha parcial para ser completada no próximo bloco
                    if (currentLine.length() > 0) {
                        completeLines.add(currentLine.toString());
                    }

                    return Flux.fromIterable(completeLines);
                })
                        .buffer(1000)
                        .flatMap(registroService::processLine)
                        .then(Mono.empty());
    }

    @PostMapping("/validate")
    public Flux<String> validateFile(@RequestPart("file") FilePart file) {
        return file
                .content()
                .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
                .flatMap(line -> Flux.fromArray(line.split("\n"))
                .buffer(1000)
                .flatMap(fileProcessingService::processFile)
                .then(Mono.just("File processed")));
    }
}
