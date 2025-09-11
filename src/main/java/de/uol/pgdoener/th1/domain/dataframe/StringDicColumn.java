package de.uol.pgdoener.th1.domain.dataframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Tabellen-Größe	Empfehlung
//< 1000 Zeilen	Einfach String[] verwenden, Dictionary-Encoding nicht nötig
//1.000 – 100.000 Zeilen	Kann sich lohnen, besonders bei stark wiederholten Strings
//> 100.000 Zeilen	Dictionary-Encoding fast immer sinnvoll für Speicher und Performance

class StringDicColumn extends DataColumn {
    private final int[] valueIds;
    private final Map<String, Integer> dict = new HashMap<>();
    private final List<String> reverseDict = new ArrayList<>();
    private int pos = 0;

    public StringDicColumn(String name, int capacity) {
        super(name);
        valueIds = new int[capacity];
    }

    @Override
    public void addValue(String value) {
        if (!dict.containsKey(value)) {
            dict.put(value, dict.size());
            reverseDict.add(value);
        }
        valueIds[pos++] = dict.get(value);
        size++;
    }

    @Override
    public Object getValue(int index) {
        return reverseDict.get(valueIds[index]);
    }
}
