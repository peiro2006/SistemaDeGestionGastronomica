package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.request.MovimientoContableCreateReqDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoContableResDto;
import com.example.SistemaDeGestion.dtos.response.MovimientoPageResDto;
import com.example.SistemaDeGestion.services.domain.MovimientoContableService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/contable/movimientos")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class ContableController {

    private final MovimientoContableService movimientoContableService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MovimientoPageResDto>> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) Long desde,
            @RequestParam(required = false) Long hasta,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            @RequestParam(required = false) String concepto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(BaseResponse.ok(
                movimientoContableService.listar(tipo, metodoPago, toInstant(desde), toInstant(hasta),
                        montoMin, montoMax, concepto, page, size),
                "Movimientos contables obtenidos correctamente"
        ));
    }

    @GetMapping("/{idMovimiento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MovimientoContableResDto>> obtener(@PathVariable Long idMovimiento) {
        return ResponseEntity.ok(BaseResponse.ok(
                movimientoContableService.obtenerPorId(idMovimiento),
                "Movimiento contable obtenido correctamente"
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MovimientoContableResDto>> registrar(
            @Valid @RequestBody MovimientoContableCreateReqDto request
    ) {
        return ResponseEntity.ok(BaseResponse.ok(
                movimientoContableService.registrar(request),
                "Movimiento contable registrado correctamente"
        ));
    }

    @GetMapping("/exportar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) Long desde,
            @RequestParam(required = false) Long hasta,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax,
            @RequestParam(required = false) String concepto
    ) {
        List<MovimientoContableResDto> movimientos = movimientoContableService.listarTodas(
                tipo, metodoPago, toInstant(desde), toInstant(hasta), montoMin, montoMax, concepto
        );

        StringBuilder csv = new StringBuilder();
        csv.append("ID;Tipo;Monto;Concepto;MetodoPago;IdCaja;Fecha;RegistradoPor\n");
        for (MovimientoContableResDto m : movimientos) {
            csv.append(m.idMovimiento()).append(';')
                    .append(m.tipo()).append(';')
                    .append(m.monto()).append(';')
                    .append(safe(m.concepto())).append(';')
                    .append(safe(m.metodoPago())).append(';')
                    .append(m.idCaja() == null ? "" : m.idCaja()).append(';')
                    .append(DateTimeFormatter.ISO_INSTANT.format(m.fecha())).append(';')
                    .append(safe(m.registradoPor()))
                    .append('\n');
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimientos_contables.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(bytes);
    }

    private Instant toInstant(Long epochMillis) {
        return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        String sinSaltos = value.replace("\r", " ").replace("\n", " ");
        return sinSaltos.contains(";") ? "\"" + sinSaltos.replace("\"", "\"\"") + "\"" : sinSaltos;
    }
}
