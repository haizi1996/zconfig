package com.hailin.admin.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DiffCount {

    private final int add;

    private final int delete;

    private final int modify;

    public DiffCount(int add, int delete) {
        this(add, delete, 0);
    }

    public DiffCount(int add, int delete, int modify) {
        this.add = add;
        this.delete = delete;
        this.modify = modify;
    }
    public boolean hasDiff() {
        return add > 0 || delete > 0 || modify > 0;
    }

    public DiffCount add(DiffCount count) {
        return new DiffCount(add + count.add, delete + count.delete, modify + count.modify);
    }
}
