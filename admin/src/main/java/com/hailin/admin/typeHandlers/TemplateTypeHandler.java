package com.hailin.admin.typeHandlers;

import com.hailin.admin.model.TemplateType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplateTypeHandler<E extends Enum<?>>extends BaseTypeHandler<TemplateType> {

    private Class<E> type;

    public TemplateTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, TemplateType templateType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i , templateType.getCode());
    }

    @Override
    public TemplateType getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int code = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public TemplateType getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        int code = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public TemplateType getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        int code = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : codeOf(code);
    }

    private TemplateType codeOf(int code) {
        try {
            return TemplateType.fromCode( code);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", ex);
        }
    }
}
