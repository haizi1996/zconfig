package com.hailin.admin.typeHandlers;

import com.hailin.admin.model.TemplateType;
import com.hailin.server.common.bean.PublicStatus;
import com.hailin.zconfig.common.bean.StatusType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AutoEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private BaseTypeHandler typeHandler;

    public AutoEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if(Objects.equals(TemplateType.class , type)){
            // 如果实现了 BaseCodeEnum 则使用我们自定义的转换器
            typeHandler = new TemplateTypeHandler(type);
        }else if(Objects.equals(StatusType.class , type)){
            typeHandler = new CandidateStatusTypeHandler(type);
        }else if(Objects.equals(PublicStatus.class , type)){
            typeHandler = new PublicStatusTypeHandler(type);
        }else {
            // 默认转换器 也可换成 EnumOrdinalTypeHandler
            typeHandler = new EnumTypeHandler<>(type);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        typeHandler.setNonNullParameter(preparedStatement , i , e , jdbcType);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return (E)typeHandler.getNullableResult(resultSet , columnName);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return (E)typeHandler.getNullableResult(resultSet , columnIndex);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return (E) typeHandler.getNullableResult(callableStatement,columnIndex);
    }
}
