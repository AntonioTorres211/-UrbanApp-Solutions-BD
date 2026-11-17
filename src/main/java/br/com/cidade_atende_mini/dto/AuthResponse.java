package br.com.cidade_atende_mini.dto;

public record AuthResponse(String message, boolean authenticated, String email, String name, String role) {
}
