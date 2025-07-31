package az.cybernet.invoice.handler;

import az.cybernet.invoice.enums.InvoiceStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(InvoiceStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class InvoiceStatusTypeHandler extends BaseTypeHandler<InvoiceStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, InvoiceStatus status, JdbcType jdbcType) throws SQLException {
        ps.setString(i, status.name());
    }

    @Override
    public InvoiceStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : InvoiceStatus.valueOf(value);
    }

    @Override
    public InvoiceStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : InvoiceStatus.valueOf(value);
    }

    @Override
    public InvoiceStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : InvoiceStatus.valueOf(value);
    }
}
