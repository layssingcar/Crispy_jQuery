package com.mcp.crispy.common.typeHandler;

import com.mcp.crispy.email.dto.VerifyStat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@MappedTypes(VerifyStat.class)
public class VerifyStatTypeHandler extends BaseTypeHandler<VerifyStat> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, VerifyStat parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public VerifyStat getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : VerifyStat.of(code);
    }

    @Override
    public VerifyStat getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : VerifyStat.of(code);
    }

    @Override
    public VerifyStat getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : VerifyStat.of(code);
    }
}
