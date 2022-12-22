package database.Column.atributes;

import database.Column.TableColumn;

public interface CustomColumn
        extends TableColumn
        , ForeignKey
        , NotNull
        , Unique{
}
