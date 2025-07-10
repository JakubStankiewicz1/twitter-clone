package com.myxapp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 50, message = "Nazwa użytkownika musi mieć od 3 do 50 znaków")
    private String username;
    
    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    @Size(max = 100, message = "Email nie może być dłuższy niż 100 znaków")
    private String email;
    
    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    private String password;
    
    @Size(max = 100, message = "Nazwa wyświetlana nie może być dłuższa niż 100 znaków")
    private String displayName;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String username, String email, String password, String displayName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
