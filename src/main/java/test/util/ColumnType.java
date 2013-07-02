package test.util;



public enum ColumnType {
    NULL(0),
    BOOLEAN(1),
    BYTE(2),
    SHORT(3),
    INT(4),
    LONG(5),
    DECIMAL(6),
    DOUBLE(7),
    FLOAT(8),
    TIME(9),
    DATE(10),
    TIMESTAMP(11),
    BYTES(12),
    STRING(13),
    STRING_IGNORECASE(14),
    BLOB(15),
    CLOB(16);

    private final int type;


    ColumnType(int type) {
        this.type = type;
    }


    public int getValue() {
        return this.type;
    }


    public static ColumnType valueOf(int type) {
        switch (type) {
        case 0:
            return NULL;
        case 1:
            return BOOLEAN;
        case 2:
            return BYTE;
        case 3:
            return SHORT;
        case 4:
            return INT;
        case 5:
            return LONG;
        case 6:
            return DECIMAL;
        case 7:
            return DOUBLE;
        case 8:
            return FLOAT;
        case 9:
            return TIME;
        case 10:
            return DATE;
        case 11:
            return TIMESTAMP;
        case 12:
            return BYTES;
        case 13:
            return STRING;
        case 14:
            return STRING_IGNORECASE;
        case 15:
            return BLOB;
        case 16:
            return CLOB;
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }
}

