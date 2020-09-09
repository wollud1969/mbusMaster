package de.hottis.mbusMaster;

import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ADataObject implements Serializable {
	private static final long serialVersionUID = 1L;

    static final String KIND_KEY = "kind";

	final protected Logger logger = LogManager.getRootLogger();

	private String name;
	private Map<String, Object> values;
    private String kind;
	
	public ADataObject(String name, String kind) {
		this.name = name;
        this.kind = kind;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	
	abstract public String getTableName();
	
	public Map<String, Object> getValues() {
		return values;
	}
    
    public boolean hasKey(String k) {
        return this.values.containsKey(k);
    }

	public String getName() {
		return name;
	}
    
    public String getKind() {
        return this.kind;
    }

	public String toString() {
		StringBuffer sb = new StringBuffer();
                sb.append("{\"name\":\"");
                sb.append(name);
                sb.append("\", \"kind\":\"");
                sb.append(kind);
                sb.append("\", \"values\":{");
                boolean first = true;
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (! first) {
                        sb.append(", ");
                    } else {
                        first = false;
                    }
                    sb.append("\"");
                    sb.append(entry.getKey());
                    sb.append("\":");
                    Object value = entry.getValue();
                    if (! ((value instanceof Double) || (value instanceof Integer))) {
                        sb.append("\"");
                    }
                    sb.append(value);
                    if (! ((value instanceof Double) || (value instanceof Integer))) {
                        sb.append("\"");
                    }
                }
                sb.append("}}");
/*
                sb.append("<");
		sb.append(name);
		sb.append(", ");
		sb.append(timestamp);
		sb.append(", ");
		sb.append(values.toString());
		sb.append(">");
*/                
        return sb.toString();
	}
}
