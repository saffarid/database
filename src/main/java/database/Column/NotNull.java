package database.Column;

/**
 * Интерфейс сообщает системе о том, что реализующая его колонка может поддерживает не пустое значение.
 * */
public interface NotNull
        extends TableColumn {

    boolean isNotNull();

    TableColumn setNotNull(boolean notNull);

}
