package by.nyurush.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ResetRequestDto {

    @NotNull
    private String code;

    @NotNull
    private String password;
}