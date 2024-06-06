package com.mcp.crispy.common.typehandler;

import com.mcp.crispy.chat.dto.MsgStat;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@MappedTypes(MsgStat.class)
public class MsgStatTypeHandler extends BaseTypeHandler<MsgStat> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MsgStat parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getStatus());
    }

    @Override
    public MsgStat getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int status = rs.getInt(columnName);
        return rs.wasNull() ? null : MsgStat.of(status);
    }

    @Override
    public MsgStat getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int status = rs.getInt(columnIndex);
        return rs.wasNull() ? null : MsgStat.of(status);
    }

    @Override
    public MsgStat getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int status = cs.getInt(columnIndex);
        return cs.wasNull() ? null : MsgStat.of(status);
    }
}
