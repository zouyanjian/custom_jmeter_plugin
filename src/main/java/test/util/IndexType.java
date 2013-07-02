package test.util;


public enum IndexType {
	SKIPLIST(1),
	BTREE(2),
	HASHMAP(3);
	
	private short type;
	
	IndexType(int i) {
		this.type = (short)i;
	}
	
	public short getValue() {
        return this.type;
    }
	
	public static IndexType valueOf(int type) {
        switch (type) {
        case 1:
            return SKIPLIST;
        case 2:
            return BTREE;
        case 3:
            return HASHMAP;
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }
}
