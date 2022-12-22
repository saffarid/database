package database.Column.atributes;

import database.Column.TableColumn;

public interface NotNull extends TableColumn {

    boolean isNotNull();
    TableColumn setNotNull(boolean notNull);

}
