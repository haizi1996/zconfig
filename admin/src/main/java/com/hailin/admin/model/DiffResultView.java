package com.hailin.admin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiffResultView {

    private String result;

    private String uniDiffResult;

    private String diffResultText;

    private DiffCount diffCount;

    public DiffResultView(String result, String diffResultText, DiffCount diffCount) {
        this(result, null, diffResultText, diffCount);
    }

    public DiffResultView(String result, String uniDiffResult, String diffResultText, DiffCount diffCount) {
        this.result = result;
        this.uniDiffResult = uniDiffResult;
        this.diffResultText = diffResultText;
        this.diffCount = diffCount;
    }
}
