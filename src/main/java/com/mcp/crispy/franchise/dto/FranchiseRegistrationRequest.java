package com.mcp.crispy.franchise.dto;


import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRegistrationRequest {
    private FranchiseDto franchiseDto;
    private OwnerRegisterDto ownerRegisterDto;
}
