package de.uol.pgdoener.th1.business.service.query;

import java.util.List;

public class QueryRequest {
    public String table;
    public List<String> select;
    public List<Join> joins;
    public List<Filter> filters;
    public List<Aggregation> aggregations;
    public List<String> groupBy;
    public List<OrderBy> orderBy;

    public static class Join {
        public String table;
        public String sourceColumn;
        public String targetColumn;
    }

    public static class Filter {
        public String column;
        public String operator;
        public String value;
    }

    public static class Aggregation {
        public String column;
        public String agg;
        public Having having;

        public static class Having {
            public String operator;
            public String value;
        }
    }

    public static class OrderBy {
        public String column;
        public String direction;
    }
}
