package com.hailin.admin.typeHandlers;

import com.hailin.admin.model.TemplateType;
import com.hailin.server.common.bean.PublicStatus;
import com.hailin.zconfig.common.bean.StatusType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.hailin.zconfig.common.bean.StatusType.codeOf;

public class PublicStatusTypeHandler <E extends Enum<?>> extends BaseTypeHandler<PublicStatus> {

    private Class<E> type;

    public PublicStatusTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, PublicStatus statusType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i , statusType.code());
    }

    @Override
    public PublicStatus getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int code = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public PublicStatus getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        int code = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public PublicStatus getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        int code = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : codeOf(code);
    }

    private PublicStatus codeOf(int code) {
        try {
            return PublicStatus.codeOf( code);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", ex);
        }
    }
}
