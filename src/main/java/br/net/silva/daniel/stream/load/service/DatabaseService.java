package br.net.silva.daniel.stream.load.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DatabaseService {

    public Mono<Boolean> validateLine(String line) {
        return Mono.just(true);
    }
}
