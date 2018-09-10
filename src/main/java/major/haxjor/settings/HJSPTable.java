package major.haxjor.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//TODO override  #equals, #hashCode, #toString.
public final class HJSPTable {

    HJSPTable parent;//the table that links this table
    private HJSPTable[] childs;//the tables that are linked to this table
    String name;//indicate what table this is
    Map<String, HJSPObject> fields;//the fields of this table

    HJSPTable(String name) {
        this.name = name;
        childs = new HJSPTable[0];
        fields = new HashMap<>();
    }

    void addChild(HJSPTable childTable) {
        int currentSize = childs.length;
        HJSPTable[] prevChilds = childs;
        childs = new HJSPTable[++currentSize];
        System.arraycopy(prevChilds, 0, childs, 0, prevChilds.length);
        childs[childs.length - 1] = childTable;
    }

    public void setParent(HJSPTable parent) {
        this.parent = parent;
    }

    public HJSPTable getParent() {
        return parent;
    }

    /**
     * Get the root table of this table.
     */
    HJSPTable getFirstParent() {
        HJSPTable firstParent = this.parent;
        //if no parent..
        if (firstParent == null) {
            return this;
        }
        //find the first parent
        while (firstParent.parent != null) {
            firstParent = firstParent.parent;
        }
        return firstParent;
    }

    public HJSPTable[] getChilds() {
        return childs;
    }

    //performance killer?
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HJSPTable)) {
            return false;
        }
        HJSPTable otherTable = (HJSPTable) other;
        //first all cheap operations
        if (childs.length != ((HJSPTable) other).childs.length) {
            return false;
        }
        if (!name.equals(otherTable.name)) {
            return false;
        }
        if (fields.size() != otherTable.fields.size()) {
            return false;
        }
        //then all heavy operations
        //TODO need 2 loop every entry and compare
        if (fields.equals(otherTable.fields)) {
            return false;
        }
        for (int i = 0; i < childs.length; i++) {
            if (!childs[i].equals(otherTable.childs[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("parent=%s, fields=%s, childs=%d", parent.name, Arrays.toString(fields.keySet().toArray()), childs.length);
    }
}