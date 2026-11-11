
package com.tripmind.backend.dto;

public class RegisterResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String email;

    public RegisterResponse() {
    }

    public RegisterResponse(boolean success, String message,
                            Long userId, String email) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = email;
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
}
