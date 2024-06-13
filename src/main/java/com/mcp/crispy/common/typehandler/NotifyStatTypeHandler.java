package com.mcp.crispy.common.typeHandler;

import com.mcp.crispy.notification.dto.NotifyStat;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(NotifyStatTypeHandler.class)
public class NotifyStatTypeHandler extends BaseTypeHandler<NotifyStat> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NotifyStat parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public NotifyStat getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return NotifyStat.of(value);
    }

    @Override
    public NotifyStat getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return NotifyStat.of(value);
    }

    @Override
    public NotifyStat getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return NotifyStat.of(value);
    }
}