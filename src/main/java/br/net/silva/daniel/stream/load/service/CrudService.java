package br.net.silva.daniel.stream.load.service;

import br.net.silva.daniel.stream.load.model.Registro;
import br.net.silva.daniel.stream.load.model.repository.RegistroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CrudService {

    private final RegistroRepository repository;

    public void save(List<Registro> registros) {
        repository.saveAll(registros);
    }
}
