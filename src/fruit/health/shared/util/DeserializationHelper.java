package fruit.health.shared.util;

public class DeserializationHelper {

	@SuppressWarnings("serial")
	public static class UnsupportedVersion extends Exception {
		public UnsupportedVersion(String msg) {
			super(msg);
		}
	}

	@SuppressWarnings("serial")
    public class IncompleteData extends Exception {}

	private char[] serialized;
	private int objectVersion;

	public DeserializationHelper(String serialized) throws IncompleteData, UnsupportedVersion {
		this.serialized = serialized.toCharArray();
		int version = (int) readLong();
		if (SerializationHelper.ENCODING_VERSION!=version) {
			throw new UnsupportedVersion(version+""); //$NON-NLS-1$
		}
		objectVersion = (int) readLong();
	}

	public int getObjectVersion() {
		return objectVersion;
	}
	
	private int _idx=0; // Where are we in the input?

    public long readLong() throws IncompleteData {
        StringBuffer buf = new StringBuffer();
        char c;
        while (SerializationHelper.SEPARATOR_CHAR!=(c = getNextChar())) {
            buf.append(c);
        }
        return Long.parseLong(buf.toString());
    }

    public double readDouble() throws IncompleteData {
        StringBuffer buf = new StringBuffer();
        char c;
        while (SerializationHelper.SEPARATOR_CHAR!=(c = getNextChar())) {
            buf.append(c);
        }
        return Double.parseDouble(buf.toString());
    }

    public Long[] readLongArray() throws IncompleteData {
        int size = (int) readLong();
        if (SerializationHelper.NULL_MARKER==size) {
        	return null;
        }
        Long[] array = new Long[size];
        for (int i=0; i<size; i++) {
            array[i] = readLong();
        }
        return array;
    }

    public String readString() throws IncompleteData {
        int size = (int) readLong();
        if (SerializationHelper.NULL_MARKER==size) {
        	return null;
        }
        int offset = _idx;
        _idx += size;
        try {
            return new String(serialized, offset, size);
        } catch (IndexOutOfBoundsException e) {
            throw new IncompleteData();
        }
    }

	public boolean hasMore() {
		return serialized.length!=_idx;
	}
	
	private char getNextChar() throws IncompleteData {
	    if (hasMore()) {
	        return serialized[_idx++];
	    } else {
	        throw new IncompleteData();
	    }
	}
}
