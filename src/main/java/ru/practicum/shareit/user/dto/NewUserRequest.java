package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class NewUserRequest {
    private String name;

    @Email(message = "Некорректный формат почты")
    @NotBlank(message = "Email должен быть указан.")
    private String email;
}
