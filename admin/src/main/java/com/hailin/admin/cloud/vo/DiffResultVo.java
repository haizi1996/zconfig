package com.hailin.admin.cloud.vo;

import com.hailin.admin.model.DiffCount;
import com.sksamuel.diffpatch.DiffMatchPatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class DiffResultVo {

    private List<DiffMatchPatch.Diff> diff;

    private String diffText;

    private DiffCount diffCount;
}
