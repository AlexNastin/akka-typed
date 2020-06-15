package com.nastsin.akka.common.entity;

import java.util.Objects;
import java.util.StringJoiner;

public class PoolDo implements AkkaCommand {

    private String id;

    public PoolDo(int id) {
        this.id = String.valueOf(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoolDo poolDo = (PoolDo) o;
        return Objects.equals(id, poolDo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PoolDo.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .toString();
    }
}
