package com.mcp.crispy.common.typehandler;

import com.mcp.crispy.chat.dto.EntryStat;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;

@MappedTypes(EntryStat.class)
public class EntryStatTypeHandler implements TypeHandler<EntryStat> {

    @Override
    public void setParameter(PreparedStatement ps, int i, EntryStat parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.INTEGER);
        } else {
            ps.setInt(i, parameter.getStatus());
        }
    }

    @Override
    public EntryStat getResult(ResultSet rs, String columnName) throws SQLException {
        int status = rs.getInt(columnName);
        return rs.wasNull() ? null : EntryStat.of(status);
    }

    @Override
    public EntryStat getResult(ResultSet rs, int columnIndex) throws SQLException {
        int status = rs.getInt(columnIndex);
        return rs.wasNull() ? null : EntryStat.of(status);
    }

    @Override
    public EntryStat getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int status = cs.getInt(columnIndex);
        return cs.wasNull() ? null : EntryStat.of(status);
    }
}
