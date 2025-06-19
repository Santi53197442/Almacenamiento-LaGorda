package com.almacenamiento.backend.service;

import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.CasaRepository;
import com.almacenamiento.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CasaService {

    private final CasaRepository casaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Casa crearCasa(String nombreCasa) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario.getCasa() != null) {
            throw new IllegalStateException("El usuario ya pertenece a una casa.");
        }

        Casa nuevaCasa = Casa.builder()
                .nombre(nombreCasa)
                .codigoInvitacion(UUID.randomUUID().toString().substring(0, 8)) // Código de 8 caracteres
                .build();

        Casa casaGuardada = casaRepository.save(nuevaCasa);

        usuario.setCasa(casaGuardada);
        usuarioRepository.save(usuario);

        return casaGuardada;
    }

    @Transactional
    public Casa unirseACasa(String codigoInvitacion) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario.getCasa() != null) {
            throw new IllegalStateException("El usuario ya pertenece a una casa.");
        }

        Casa casa = casaRepository.findByCodigoInvitacion(codigoInvitacion)
                .orElseThrow(() -> new IllegalArgumentException("Código de invitación inválido."));

        usuario.setCasa(casa);
        usuarioRepository.save(usuario);

        return casa;
    }

    private Usuario getUsuarioAutenticado() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado en el contexto de seguridad."));
    }
}