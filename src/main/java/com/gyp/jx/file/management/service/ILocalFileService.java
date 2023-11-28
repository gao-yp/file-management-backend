package com.gyp.jx.file.management.service;


import com.gyp.jx.file.management.config.FileSearchTypeEnum;
import com.gyp.jx.file.management.config.error.BizException;
import com.gyp.jx.file.management.entity.LocalFile;

import java.util.List;

public interface ILocalFileService {


    /**
     * 获取文件列表
     *
     * @param dirUrl 文件夹地址
     * @return
     * @throws Exception
     */
    List<LocalFile> getList(String dirUrl, int startNum, int pageSize) throws BizException;

    /**
     * 获取文件列表长度
     *
     * @param dirUrl
     * @return
     */
    long getListTotal(String dirUrl);

    /**
     * 获取父类对象
     *
     * @param dirUrl
     * @return
     */
    LocalFile getParent(String dirUrl);


    List<LocalFile> keywordSearchList(FileSearchTypeEnum searchType, String dirUrl, String keyword) throws BizException;

    /**
     * 文件删除
     *
     * @param url
     * @throws Exception
     */
    void delete(String url) throws BizException;

    /**
     * 文件重命名
     *
     * @param url
     * @param newName
     * @throws Exception
     */
    void rename(String url, String newName) throws BizException;
}
