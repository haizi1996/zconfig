package com.hailin.admin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class DiffResult<T> {

    private final DiffCount diffCount;

    private final T result;
}
