package database.Column.Implements;

import database.Column.DataTypes;
import database.Column.PrimaryKeyColumn;

public class PrimaryKeyCustom
        extends Column
        implements PrimaryKeyColumn {

    private String templateForCreate = "`%1s` %2s primary key";

    public PrimaryKeyCustom() {
        name = "id";
        type = DataTypes.ID;
    }

    @Override
    public String comandForCreate() {
        return String.format(templateForCreate, name, type.getDataType());
    }

}
