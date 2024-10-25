package br.net.silva.daniel.stream.load.service;

import br.net.silva.daniel.stream.load.model.Registro;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RegistroService {

    private final CrudService crudService;
    private final ExecutorService virtualExecutor;

    public RegistroService(CrudService crudService) {
        this.crudService = crudService;
        this.virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public Mono<Void> processLine(List<String> lines) {
        return Mono.fromCallable(() -> {
            register(lines);
            return null;
        }).subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .then();
    }

    private void register(List<String> lines) {
        List<Registro> registros = new ArrayList<>();
        for (String line : lines) {
            if (isNotTitle(line)) {
                String[] values = line.split(";");
                if (values.length == 3) {
                    final var registro = Registro.builder()
                            .codIdentifier(values[0])
                            .cnpj(values[1])
                            .description(values[2])
                            .build();

                    registros.add(registro);
                }
            }
        }

        crudService.save(registros);

    }

    private boolean isNotTitle(String line) {
        return !line.contains("ID;CNPJ;DESCRICAO");
    }
}
