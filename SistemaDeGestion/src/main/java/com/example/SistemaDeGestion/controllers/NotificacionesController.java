package com.example.SistemaDeGestion.controllers;

import com.example.SistemaDeGestion.configs.BaseResponse;
import com.example.SistemaDeGestion.dtos.response.NotificacionResDto;
import com.example.SistemaDeGestion.services.domain.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notificaciones")
@AllArgsConstructor
public class NotificacionesController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificacionResDto>>> listar(
            @RequestParam(required = false) Boolean soloNoLeidas
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        notificacionService.listar(soloNoLeidas),
                        "Notificaciones obtenidas correctamente"
                )
        );
    }

    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<BaseResponse<NotificacionResDto>> marcarLeida(
            @PathVariable Long idNotificacion
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        notificacionService.marcarLeida(idNotificacion),
                        "Notificacion marcada como leida"
                )
        );
    }

}
