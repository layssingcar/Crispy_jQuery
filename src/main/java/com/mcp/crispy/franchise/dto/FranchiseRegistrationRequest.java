package com.mcp.crispy.franchise.dto;


import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRegistrationRequest {

    private FranchiseRegisterDto franchise;

    @Valid
    private OwnerRegisterDto owner;
}
