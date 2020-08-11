package com.hailin.admin.typeHandlers;

import com.hailin.zconfig.common.bean.StatusType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidateStatusTypeHandler<E extends Enum<?>> extends BaseTypeHandler<StatusType> {

    private Class<E> type;

    public CandidateStatusTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, StatusType statusType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i , statusType.code());
    }

    @Override
    public StatusType getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int code = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public StatusType getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        int code = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public StatusType getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        int code = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : codeOf(code);
    }

    private StatusType codeOf(int code) {
        try {
            return StatusType.codeOf( code);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", ex);
        }
    }
}
