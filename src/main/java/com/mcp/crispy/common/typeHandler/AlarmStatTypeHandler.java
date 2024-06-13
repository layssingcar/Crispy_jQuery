package com.mcp.crispy.common.typeHandler;

import com.mcp.crispy.chat.dto.AlarmStat;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;

@MappedTypes(AlarmStat.class)
public class AlarmStatTypeHandler implements TypeHandler<AlarmStat> {

    @Override
    public void setParameter(PreparedStatement ps, int i, AlarmStat parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.INTEGER);
        } else {
            ps.setInt(i, parameter.getStatus());
        }
    }

    @Override
    public AlarmStat getResult(ResultSet rs, String columnName) throws SQLException {
        int status = rs.getInt(columnName);
        return rs.wasNull() ? null : AlarmStat.of(status);
    }

    @Override
    public AlarmStat getResult(ResultSet rs, int columnIndex) throws SQLException {
        int status = rs.getInt(columnIndex);
        return rs.wasNull() ? null : AlarmStat.of(status);
    }

    @Override
    public AlarmStat getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int status = cs.getInt(columnIndex);
        return cs.wasNull() ? null : AlarmStat.of(status);
    }
}
