package com.mcp.crispy.common.typeHandler;

import com.mcp.crispy.notification.dto.NotifyCt;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@MappedTypes(NotifyCtTypeHandler.class)
public class NotifyCtTypeHandler extends BaseTypeHandler<NotifyCt> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NotifyCt parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public NotifyCt getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return NotifyCt.of(value);
    }

    @Override
    public NotifyCt getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return NotifyCt.of(value);
    }

    @Override
    public NotifyCt getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return NotifyCt.of(value);
    }
}