package database.Column.Implements;

import database.Column.atributes.AutoincrementColumn;
import database.Column.DataTypes;
import database.Column.atributes.PrimaryKeyColumn;
import database.Column.TableColumn;

public class PrimaryKeyAutoncrement
        extends Column
        implements PrimaryKeyColumn, AutoincrementColumn {

    /**
     * Шаблон описания атрибутов колонки первичного автоинкрементируемого ключа
     * <ul style="list-style-type: decimal">
     *      <li>Наименование колонки</li>
     *      <li>Тип колонки</li>
     * </ul>
     * */
    private final String templateForCreate = "`%1$s` %2$s primary key autoincrement";

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
