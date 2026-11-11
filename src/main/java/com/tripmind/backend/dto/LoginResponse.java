
package com.tripmind.backend.dto;

public class LoginResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String email;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message,
                         Long userId, String email, String token) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
