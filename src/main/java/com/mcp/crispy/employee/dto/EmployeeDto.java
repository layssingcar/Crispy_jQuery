package com.mcp.crispy.employee.dto;

import com.mcp.crispy.chat.dto.EntryStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeDto {

    private Integer empNo;
    private String empId;
    private String empPw;
    private String empName;
    private String empEmail;
    private String empPhone;
    private String empZip;
    private String empStreet;
    private String empDetail;
    private String empProfile;
    // 결재 서명
    private String empSign;
    // 연차
    private int empAnnual;
    // 재직 상태
    private EmpStatus empStat;
    private Date empInDt;
    private Date empOutDt;
    private Date createDt;
    private Date modifyDt;
    private Position posNo;
    private int frnNo;
    private String frnName;
    private String posName;
    private Integer chatRoomNo;
    private EntryStat entryStat;
    private String accessToken;
    private String refreshToken;
}
