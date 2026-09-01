package com.example.SistemaDeGestion.services.domain;

import com.example.SistemaDeGestion.configs.exceptions.BadRequestException;
import com.example.SistemaDeGestion.configs.exceptions.NotFoundException;
import com.example.SistemaDeGestion.dtos.request.MovimientoContableCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoContableResDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoPageResDto;
import com.example.SistemaDeGestion.mappers.MovimientoContableMapper;
import com.example.SistemaDeGestion.models.MovimientoContable;
import com.example.SistemaDeGestion.models.Usuario;
import com.example.SistemaDeGestion.repositories.MovimientoContableRepository;
import com.example.SistemaDeGestion.repositories.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MovimientoContableService {

    private static final String TIPO_INGRESO = "INGRESO";
    private static final String TIPO_EGRESO = "EGRESO";
    private static final Set<String> METODOS_PAGO = Set.of("EFECTIVO", "DEBITO", "TRANSFERENCIA");
    private static final int MAX_SIZE = 100;

    private final MovimientoContableRepository movimientoContableRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public MovimientoPageResDto listar(String tipo, String metodoPago, Instant desde, Instant hasta,
            BigDecimal montoMin, BigDecimal montoMax, String concepto, int page, int size) {

        if (page < 0) {
            throw new BadRequestException("La pagina no puede ser negativa");
        }
        int tam = Math.min(size <= 0 ? 20 : size, MAX_SIZE);

        Pageable pageable = PageRequest.of(page, tam, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<MovimientoContable> resultado = movimientoContableRepository.findAll(buildSpec(tipo, metodoPago, desde, hasta, montoMin, montoMax, concepto), pageable);

        Instant filtroDesde = desde != null ? desde : Instant.EPOCH;
        Instant filtroHasta = hasta != null ? hasta : Instant.MAX;
        BigDecimal totalIngresos = nz(movimientoContableRepository.sumarMontoEntre(TIPO_INGRESO, filtroDesde, filtroHasta));
        BigDecimal totalEgresos = nz(movimientoContableRepository.sumarMontoEntre(TIPO_EGRESO, filtroDesde, filtroHasta));

        return new MovimientoPageResDto(
                MovimientoContableMapper.toResponseDtoList(resultado.getContent()),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.hasNext(),
                resultado.hasPrevious(),
                totalIngresos,
                totalEgresos,
                totalIngresos.subtract(totalEgresos)
        );
    }

    @Transactional(readOnly = true)
    public MovimientoContableResDto obtenerPorId(Long idMovimiento) {
        MovimientoContable movimiento = movimientoContableRepository.findById(idMovimiento)
                .orElseThrow(() -> new NotFoundException("No existe un movimiento contable con id " + idMovimiento));
        return MovimientoContableMapper.toResponseDto(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoContableResDto> listarTodas(String tipo, String metodoPago, Instant desde,
            Instant hasta, BigDecimal montoMin, BigDecimal montoMax, String concepto) {
        List<MovimientoContable> movimientos = movimientoContableRepository.findAll(
                buildSpec(tipo, metodoPago, desde, hasta, montoMin, montoMax, concepto),
                Sort.by(Sort.Direction.DESC, "fecha")
        );
        return MovimientoContableMapper.toResponseDtoList(movimientos);
    }

    @Transactional
    public MovimientoContableResDto registrar(MovimientoContableCreateReqDto request) {
        String tipo = normalizarTipo(request.tipo());

        MovimientoContable movimiento = MovimientoContable.builder()
                .tipo(tipo)
                .monto(request.monto())
                .concepto(request.concepto())
                .idCaja(request.idCaja())
                .fecha(Instant.now())
                .registradoPor(obtenerUsuarioAutenticado())
                .build();

        if (TIPO_INGRESO.equals(tipo)) {
            String metodoPago = normalizarMetodoPago(request.metodoPago());
            movimiento.setMetodoPago(metodoPago);
        }

        return MovimientoContableMapper.toResponseDto(movimientoContableRepository.save(movimiento));
    }

    private Specification<MovimientoContable> buildSpec(String tipo, String metodoPago, Instant desde,
            Instant hasta, BigDecimal montoMin, BigDecimal montoMax, String concepto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipo != null && !tipo.isBlank()) {
                predicates.add(cb.equal(root.get("tipo"), normalizarTipo(tipo)));
            }
            if (metodoPago != null && !metodoPago.isBlank()) {
                predicates.add(cb.equal(root.get("metodoPago"), normalizarMetodoPago(metodoPago)));
            }
            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), desde));
            }
            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), hasta));
            }
            if (montoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("monto"), montoMin));
            }
            if (montoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("monto"), montoMax));
            }
            if (concepto != null && !concepto.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("concepto")), "%" + concepto.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("Debe indicar el tipo de movimiento");
        }
        String normalizado = tipo.trim().toUpperCase();
        if (!TIPO_INGRESO.equals(normalizado) && !TIPO_EGRESO.equals(normalizado)) {
            throw new BadRequestException("El tipo de movimiento debe ser INGRESO o EGRESO");
        }
        return normalizado;
    }

    private String normalizarMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new BadRequestException("Debe indicar el metodo de pago del ingreso (EFECTIVO, DEBITO o TRANSFERENCIA)");
        }
        String normalizado = metodoPago.trim().toUpperCase();
        if (!METODOS_PAGO.contains(normalizado)) {
            throw new BadRequestException("El metodo de pago debe ser EFECTIVO, DEBITO o TRANSFERENCIA");
        }
        return normalizado;
    }

    private String obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("No se pudo identificar al usuario autenticado");
        }
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new NotFoundException("No existe el usuario autenticado"));
        return usuario.getEmail();
    }

    private BigDecimal nz(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
