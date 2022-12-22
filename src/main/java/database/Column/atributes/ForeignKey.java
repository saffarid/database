package database.Column.atributes;

import database.Column.TableColumn;

public interface ForeignKey extends TableColumn {

    TableColumn getForeignKey();
    boolean hasForeignKey();
    TableColumn setForeignKey(TableColumn fkColumn);

}
