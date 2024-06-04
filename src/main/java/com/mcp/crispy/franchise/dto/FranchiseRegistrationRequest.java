package com.mcp.crispy.franchise.dto;


import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRegistrationRequest {
    @Valid
    @NotNull(message = "가맹점 정보를 확인해주세요.")
    private FranchiseRegisterDto franchise;

    @Valid
    @NotNull(message = "점주 정보를 확인해주세요.")
    private OwnerRegisterDto owner;
}
