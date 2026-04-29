package no.soprasteria.domain;

import java.time.LocalDateTime;

public record IdemDataDTO(String id, String author, String message, LocalDateTime createdAt) {}
