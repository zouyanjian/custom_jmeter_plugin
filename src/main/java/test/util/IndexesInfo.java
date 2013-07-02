package test.util;
import java.util.HashMap;


public class IndexesInfo {

    private String name;
    private short type;
    private short direction;
    private String columnName;
    private short state;
    private String expression;
    private String comment;


    public IndexesInfo(String name) {
        this.name = name;
    }


    public IndexesInfo setType(short type) {
        this.type = type;
        return this;
    }


    public IndexesInfo setDirection(short direction) {
        this.direction = direction;
        return this;
    }


    public IndexesInfo setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }


    public IndexesInfo setState(short state) {
        this.state = state;
        return this;
    }


    public IndexesInfo setExpression(String expression) {
        this.expression = expression;
        return this;
    }


    public IndexesInfo setComment(String comment) {
        this.comment = comment;
        return this;
    }


    public HashMap<String, Object> build() {

        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("name", name);
        result.put("type", type);
        result.put("direction", direction);
        result.put("columnName", columnName);
        result.put("state", state);
        result.put("expression", expression);
        result.put("comment", comment);

        return result;
    }

}
