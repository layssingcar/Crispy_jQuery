package com.mcp.crispy.common.typehandler;

import com.mcp.crispy.employee.dto.Position;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Position.class)
public class PositionTypeHandler implements TypeHandler<Position> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Position parameter, JdbcType jdbcType) throws SQLException {
        System.out.println("Setting Position: Index=" + i + ", Code=" + (parameter != null ? parameter.getCode() : "null"));
        if (parameter == null) {
            ps.setNull(i, JdbcType.INTEGER.TYPE_CODE);
        System.out.println("인덱스: Index=" + i + ", INTEGER.TYPE_CODE=" + JdbcType.INTEGER.TYPE_CODE);
        } else {
            ps.setInt(i, parameter.getCode());
        }
    }

    @Override
    public Position getResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return Position.of(code); // Position에서 code에 해당하는 enum 반환 메소드 필요
    }

    @Override
    public Position getResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return Position.of(code); // Position에서 code에 해당하는 enum 반환 메소드 필요
    }

    @Override
    public Position getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return Position.of(code); // Position에서 code에 해당하는 enum 반환 메소드 필요
    }
}

