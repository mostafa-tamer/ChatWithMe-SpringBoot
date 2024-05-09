package com.mostafatamer.trysomethingcrazy.utils;

import lombok.NoArgsConstructor;
import org.hibernate.sql.ast.tree.from.TableGroupJoin;

@NoArgsConstructor
final public class RoutBuilder {

    private final StringBuilder rout = new StringBuilder();

    public RoutBuilder(String part) {
        rout.append(part);
    }

    public RoutBuilder addPart(String part) {
        rout.append(part);
        return this;
    }

    public RoutBuilder addPart(Character part) {
        rout.append(part);
        return this;
    }

    public String build() {
        return rout.toString();
    }
}
