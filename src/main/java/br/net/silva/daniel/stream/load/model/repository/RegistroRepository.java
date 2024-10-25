package br.net.silva.daniel.stream.load.model.repository;

import br.net.silva.daniel.stream.load.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
}
