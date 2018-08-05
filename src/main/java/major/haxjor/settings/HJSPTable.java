package major.haxjor.settings;

import java.util.HashMap;
import java.util.Map;

public final class HJSPTable {
    HJSPTable parent;//the table that links this table
    HJSPTable[] childs;//the tables that are linked to this table
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
        //now we added a table ;o or does it override the last one?
        childs[childs.length - 1] = childTable;
    }

    public void setParent(HJSPTable parent) {
        this.parent = parent;
    }

    //parse the table
    HJSPTable build(String[] lines) {
        return this;
    }

    /**
     * Get the root table of this table.
     */
    HJSPTable getRoot() {
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
}