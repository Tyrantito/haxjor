package major.haxjor.settings;

import major.haxjor.settings.exception.HJSPFigureException;
import major.haxjor.settings.exception.HJSPNonInitializedFieldException;

@SuppressWarnings("unchecked")
public final class HJSPObject<T> {
    private int line; //what line this object is located at on the file
    boolean init; //have we initialized this object yet. this ensures objects immutability
    String data; // the field data (aka value)
    private T t;

    HJSPObject(int line, String data) {
        this.line = line;
        this.data = data;
        //try to simply parse
        simpleParse();
    }

    //some fields are easy to parse
    void simpleParse() {
        if (data.isEmpty()) {
            return;
        }
        if (data.startsWith("\"")) {
            //string
            if (!data.endsWith("\"")) {
                throw new HJSPFigureException("Excepted end of String but found \"" + data.substring(data.length() - 1) + "\" instead.", line);
            }
            String finalField = data.substring(data.indexOf("\"") + 1, data.lastIndexOf("\""));
            set((T) finalField);
//                    System.out.println("the field is se t! " + finalField);
            return;
        } else if (data.startsWith("'")) {
            //char
            if (!data.endsWith("'")) {
                throw new HJSPFigureException("Excepted end of Character but found \"" + data.substring(data.length() - 1) + "\" instead.", line);
            }
            final String parsedChar = data.substring(data.indexOf("'") + 1, data.lastIndexOf("'"));
            if (parsedChar.toCharArray().length > 1) {
                throw new HJSPFigureException("Invalid character found.", line);
            }
            Character finalField = parsedChar.charAt(0);
            set((T) finalField);
        } else if (data.startsWith("$")) {
            data = data.substring(data.indexOf("$") + 1);
            if (data.isEmpty()) {
                throw new HJSPFigureException("Excepted any reference but no value assigned.", line);
            }
        } else {
            //try parsing numberic fields
            try {
                if (data.endsWith("L")) {
                    //long
                    set((T) Long.valueOf(Long.parseLong(data.substring(0, data.length() - 1))));
                    return;
                } else if (data.endsWith("D")) {
                    //double
                    set((T) Double.valueOf(Double.parseDouble(data.substring(0, data.length() - 1))));
                    return;
                } else if (data.endsWith("F")) {
                    System.out.println("data: " + data.substring(0, data.length() - 1) + " //" + data.length() + "");
                    //float
                    set((T) Float.valueOf(Float.parseFloat(data.substring(0, data.length() - 1))));
                    return;
                } else {
                    //int
                    set((T) Integer.valueOf(Integer.parseInt(data.substring(0, data.length() - 1))));
                    return;
                }
            } catch (NumberFormatException e) {
                //boolean
                if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false")) {
                    set((T) Boolean.valueOf(Boolean.parseBoolean(data)));
                    return;
                }
            }
        }
    }

    //init value
    void set(T t) {
        if (t == null) {
            throw new HJSPFigureException("Field value can not be a null.", line);
        }
        this.t = t;
        this.init = true;
    }

    T get() {
        if (!init) {
            throw new HJSPNonInitializedFieldException("Field has no value yet.", line);
        } else {
            return this.t;
        }
    }

    //TODO hashCode
    //TODO test hash & equal fucntions
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HJSPObject)) {
            return false;
        }
        HJSPObject otherObject = (HJSPObject) other;
        if (line != otherObject.line) {
            return false;
        }
        //does it matter if we initialized? TODO shouldn't be
        if (init != otherObject.init) {
            return false;
        }
        if (!data.equals(otherObject.data)) {
            return false;
        }
        if (!t.equals(otherObject.t)) {
            System.out.println("This might be a failure since all objects must have a proper #equals comparation");
            return false;
        }
        return true;
    }
}
