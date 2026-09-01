package com.example.SistemaDeGestion.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record MovimientoPageResDto(
        List<MovimientoContableResDto> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        BigDecimal totalIngresos,
        BigDecimal totalEgresos,
        BigDecimal balance
) {}