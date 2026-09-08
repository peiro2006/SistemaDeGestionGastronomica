package com.example.SistemaDeGestion.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "caja_arqueo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CajaArqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_arqueo")
    private Long idArqueo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "monto_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "total_efectivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEfectivo;

    @Column(name = "total_debito", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDebito;

    @Column(name = "total_credito", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCredito;

    @Column(name = "total_transferencia", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTransferencia;

    @Column(name = "cantidad_pedidos", nullable = false)
    private Integer cantidadPedidos;

    @Column(name = "fecha_apertura", nullable = false)
    private Instant fechaApertura;

    @Column(name = "fecha_cierre", nullable = false)
    private Instant fechaCierre;

    @Column(name = "cerrado_por")
    private Long cerradoPor;

    @PrePersist
    public void prePersist() {
        if (fechaCierre == null) {
            fechaCierre = Instant.now();
        }
    }
}
