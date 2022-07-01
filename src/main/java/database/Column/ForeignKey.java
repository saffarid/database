package database.Column;

/**
 * Интерфейс сообщает системе о том, что реализующая его колонка может поддерживать внешнюю ссылку.
 * */
public interface ForeignKey
        extends TableColumn {

    TableColumn getForeignKey();

    boolean hasForeignKey();

    TableColumn setForeignKey(TableColumn fkColumn);
}
