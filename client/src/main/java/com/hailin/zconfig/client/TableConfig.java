package com.hailin.zconfig.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ListenableFuture;
import com.hailin.zconfig.client.impl.AbstractConfiguration;
import com.hailin.zconfig.common.util.Constants;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class TableConfig extends AbstractConfiguration<QTable>{

    public static final TableParser TRIM_PARSER = new TableParser(true);

    public static final TableParser NOT_TRIM_PARSER = new TableParser(false);

    private static final ObjectMapper mapper = new ObjectMapper();

    private TableConfig(Feature feature, String fileName) {
        super(feature, fileName);
    }


    @Override
    public ListenableFuture<Boolean> initFuture() {
        return null;
    }

    @Override
    public QTable emptyData() {
        return null;
    }

    @Override
    public QTable parse(String data) throws IOException {
        return null;
    }

    @Override
    public void addListener(ConfigListener<QTable> listener) {

    }

    public static class TableParser implements Parser<Table<String, String, String>> {

        private final boolean trimValue;

        public TableParser(boolean trimValue) {
            this.trimValue = trimValue;
        }

        @Override
        public Table<String, String, String> parse(String data) throws IOException {
            JsonNode jsonNode = mapper.readTree(data);

            ImmutableTable.Builder<String, String, String> builder = ImmutableTable.builder();
            Iterator<JsonNode> elements = jsonNode.elements();
            while (elements.hasNext()) {
                JsonNode rowNode = elements.next();
                String row = rowNode.get(Constants.ROW).asText();
                Iterator<Map.Entry<String, JsonNode>> fields = rowNode.get(Constants.COLUMNS).fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    JsonNode valueNode = field.getValue();
                    if (valueNode != null && valueNode.asText() != null) {
                        String column = field.getKey();
                        String value = valueNode.asText();
                        if (trimValue) {
                            value = value.trim();
                        }
                        if (!Strings.isNullOrEmpty(value)) {
                            builder.put(row, column, value);
                        }
                    }
                }
            }
            return builder.build();
        }
    }
}
