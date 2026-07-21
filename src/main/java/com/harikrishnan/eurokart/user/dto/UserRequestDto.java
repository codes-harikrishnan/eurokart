package com.harikrishnan.eurokart.user.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDto {

    @NotBlank(message = "Email id should not be blank")
    @Email(message = "Email should be in the proper format")
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 6, message = "Password should not be blank")
    private String password;

}
