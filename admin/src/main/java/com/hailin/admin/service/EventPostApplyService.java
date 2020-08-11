package com.hailin.admin.service;

import com.hailin.admin.dto.CandidateDTO;
import com.hailin.admin.exception.ConfigExistException;
import com.hailin.admin.exception.IllegalTemplateException;
import com.hailin.admin.exception.ModifiedException;
import com.hailin.admin.exception.StatusMismatchException;
import com.hailin.admin.exception.TemplateChangedException;

public interface EventPostApplyService {

    /**
     * 强制提交，提交时不检查文件是否存在或已被删除，因为这个方法仅在创建一个与已删除的文件同名的文件时调用，
     * 直接把已删除的文件状态改为“待审核”，并更新其版本号和snapshot，流程与更新一个已发布的版本一致
     */
    void forceApply(CandidateDTO dto, String remarks) throws StatusMismatchException, ModifiedException, TemplateChangedException, IllegalTemplateException;


    /**
     * 申请
     */
    void apply(CandidateDTO dto, String remarks) throws ModifiedException, StatusMismatchException, ConfigExistException, TemplateChangedException, IllegalTemplateException;


    /**
     * 删除(逻辑删除)
     */
    void delete(CandidateDTO dto, String s);
}
