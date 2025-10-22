package io.github.vicen621.volveacasa.persistence.dao.filtros;

import java.util.List;
import java.util.Map;

public record QueryComponents(List<String> predicates, Map<String, Object> parameters) {}
