package com.mcp.crispy.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseDto {
    private int frnNo;
    private String frnName;
    private String frnOwner;
    private String frnTel;
    private String frnZip;
    private String frnStreet;
    private String frnDetail;
    private String frnX;
    private String frnY;
    private String frnStartTime;
    private String frnEndTime;
    private String frnImg;
    private LocalDateTime frnJoinDt;
    private Date createDt;
    private Date modifyDt;
    private int creator;
    private int modifier;
    private String empId;
    private int empNo;
    private int pageNo;             // 페이지번호

}
