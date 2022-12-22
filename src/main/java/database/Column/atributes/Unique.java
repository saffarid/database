package database.Column.atributes;


import database.Column.TableColumn;

public interface Unique
        extends TableColumn {
    boolean isUnique();
    TableColumn setUnique(boolean unique);
}
