package database.Column.Implements;

import database.Column.AutoincrementColumn;
import database.Column.DataTypes;
import database.Column.PrimaryKeyColumn;
import database.Column.TableColumn;

public class PrimaryKeyAutoncrement
        extends Column
        implements PrimaryKeyColumn, AutoincrementColumn {

    private final String templateForCreate = "`%1s` %2s primary key autoincrement";

    public PrimaryKeyAutoncrement() {
        name = "id";
        type = DataTypes.ID;
    }

    @Override
    public TableColumn setName(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TableColumn setType(DataTypes type) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String comandForCreate() {
        return String.format(templateForCreate, name, type.getDataType());
    }
}
