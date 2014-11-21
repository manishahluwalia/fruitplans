package fruit.health.shared.util;

public class SerializationHelper {
    static final char SEPARATOR_CHAR = ';';
    static final int ENCODING_VERSION = 1;
	static final long NULL_MARKER = -1;

    StringBuffer buffer = new StringBuffer();

    public SerializationHelper(int object_version) {
    	writeLong(ENCODING_VERSION);
    	writeLong(object_version);
    }
    
    public void writeLong(long data) {
        buffer.append(Long.toString(data));
        buffer.append(SEPARATOR_CHAR);
    }
    
    public void writeDouble(double data) {
        buffer.append(Double.toString(data));
        buffer.append(SEPARATOR_CHAR);
    }

    public void writeString(String data) {
    	if (null==data) {
    		writeLong(NULL_MARKER);
    		return;
    	}
        writeLong(data.length());
        buffer.append(data.toCharArray());
    }

    public void writeLongArray(Long[] data) {
    	if (null==data) {
    		writeLong(NULL_MARKER);
    		return;
    	}
        writeLong(data.length);
        for (Long s : data) {
            writeLong(s);
        }
    }
    
    public String getSerialized() {
    	return buffer.toString();
    }
}
