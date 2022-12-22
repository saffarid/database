package database.Column.Implements;

import database.Column.DataTypes;
import database.Column.atributes.PrimaryKeyColumn;

public class PrimaryKeyCustom
        extends Column
        implements PrimaryKeyColumn {

    /**
     * Шаблон описания атрибутов колонки первичного ключа
     * <ul style="list-style-type: decimal">
     *      <li>Наименование колонки</li>
     *      <li>Тип колонки</li>
     * </ul>
     * */
    private String templateForCreate = "`%1$s` %2$s primary key";

    public PrimaryKeyCustom() {
        name = "id";
        type = DataTypes.ID;
    }

    @Override
    public String comandForCreate() {
        return String.format(templateForCreate, name, type.getDataType());
    }

}
