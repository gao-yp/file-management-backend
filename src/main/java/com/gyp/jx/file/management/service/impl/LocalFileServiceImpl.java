package com.gyp.jx.file.management.service.impl;

import cn.hutool.core.io.FileUtil;
import com.gyp.jx.file.management.config.FileSearchTypeEnum;
import com.gyp.jx.file.management.config.error.BizException;
import com.gyp.jx.file.management.entity.LocalFile;
import com.gyp.jx.file.management.service.ILocalFileService;
import com.gyp.jx.file.management.utils.Utils;
import com.gyp.jx.file.management.utils.crypto.UrlBase64Coder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Service
@Log4j2
public class LocalFileServiceImpl implements ILocalFileService {

    // 定义日期时间格式
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${app.default-home}")
    private String defaultHome;

    @Override
    public List<LocalFile> getList(String dirUrl, int startNum, int pageSize) throws BizException {
        List<LocalFile> list = new ArrayList<>();
        Path dir = Paths.get(dirUrl);
        if (!Files.isDirectory(dir)) {
            throw new BizException("非目录");
        }
        if (!StringUtils.startsWith(dirUrl, defaultHome)) {
            throw new BizException("非法目录");
        }
        //判断是否有权限
        if (!Files.isWritable(dir)) {
            return list;
        }
        try (Stream<Path> files = Files.list(dir)) {
            List<Path> sortedFiles = new ArrayList<>();
            files.forEach(sortedFiles::add);
            Utils.chinaSort(sortedFiles);
            sortedFiles.stream().skip(startNum).limit(pageSize).forEach(item -> {
                LocalFile localFile = toLocalFile(item.toFile(), defaultHome);
                list.add(localFile);
            });
        } catch (IOException e) {
            log.error("获取文件失败", e);
            throw new BizException("获取文件失败");
        }
        return list;
    }

    private static LocalFile toLocalFile(File f, String defaultHome) {
        boolean flag = f.isFile();
        LocalFile localFile = new LocalFile();
        localFile.setIsFile(flag);
        localFile.setFileName(f.getName());
        // 将long类型时间转换为LocalDateTime
        LocalDateTime dateTime =
                Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        localFile.setLastTime(dateTime.format(DATE_TIME_FORMATTER));
        localFile.setFileSize(flag ? f.length() : null);
        String absolutePath = f.getAbsolutePath();
        localFile.setUrl(StringUtils.removeStart(absolutePath, defaultHome));
        try {
            localFile.setUrl4Encode(UrlBase64Coder.encode(absolutePath));
        } catch (UnsupportedEncodingException e) {
            log.error("文件地址编码失败", e);
        }
        if (flag) {
            return localFile;
        }
        localFile.setChildrenSize(0);
        if (Files.isWritable(Paths.get(f.toURI()))) {
            String[] children = f.list();
            if (children != null && children.length > 0) {
                localFile.setChildrenSize(children.length);
            }
        }
        return localFile;
    }

    @Override
    public long getListTotal(String dirUrl) {
        if (!Files.isWritable(Paths.get(dirUrl))) {
            return 0L;
        }
        File dir = new File(dirUrl);
        if (!dir.isDirectory() || dir.listFiles() == null) {
            return 1;
        }
        return Objects.requireNonNull(dir.listFiles()).length;
    }

    @Override
    public LocalFile getParent(String dirUrl) {
        LocalFile localFile;
        File parentFile = new File(dirUrl).getParentFile();
        if (StringUtils.equals(parentFile.getAbsolutePath(), defaultHome)) {
            localFile = new LocalFile();
            localFile.setIsFile(false);
            localFile.setFileName("..");
            String absolutePath = parentFile.getAbsolutePath();
            localFile.setUrl("根");
            try {
                localFile.setUrl4Encode(UrlBase64Coder.encode(absolutePath));
            } catch (UnsupportedEncodingException e) {
                log.error("文件地址编码失败", e);
            }
        } else {
            localFile = toLocalFile(parentFile, defaultHome);
            localFile.setFileName("..");
        }
        return localFile;
    }

    @Override
    public List<LocalFile> keywordSearchList(FileSearchTypeEnum searchType, String dirUrl, String keyword) throws BizException {
        if (StringUtils.isBlank(keyword)) {
            throw new BizException("请输入关键字");
        }

        Path dir = Paths.get(dirUrl);
        boolean isRecursion;
        switch (searchType) {
            case Current -> isRecursion = false;
            case Recursion -> isRecursion = true;
            case All -> {
                dir = Paths.get(defaultHome);
                isRecursion = true;
            }
            default -> throw new BizException("异常操作");
        }
        List<LocalFile> list = new ArrayList<>();
        if (!Files.isDirectory(dir)) {
            throw new BizException("非目录");
        }
        if (!StringUtils.startsWith(dirUrl, defaultHome)) {
            throw new BizException("非法目录");
        }
        keywordSearchList(list, dir, isRecursion, keyword, defaultHome);
        return list;
    }

    private static void keywordSearchList(List<LocalFile> list, Path dir, Boolean isRecursion, String keyword,
                                          String defaultHome) {

        //判断是否有权限
        if (!Files.isWritable(dir)) {
            return;
        }

        try (Stream<Path> files = Files.list(dir)) {
            files.forEach(item -> {
                if (matchKeyword(item.getFileName().toString(), keyword)) {
                    LocalFile localFile = toLocalFile(item.toFile(), defaultHome);
                    list.add(localFile);
                } else if (isRecursion && Files.isDirectory(item)) {
                    keywordSearchList(list, item, true, keyword, defaultHome);
                }
            });
        } catch (IOException e) {
            log.error("获取文件失败", e);
            throw new BizException("获取文件失败");
        }
    }


    /**
     * 关键字匹配
     *
     * @param name    匹配对象
     * @param keyword 关键字
     */
    public static boolean matchKeyword(String name, String keyword) {
        return name.toUpperCase().contains(keyword.toUpperCase());
    }

    @Override
    public void delete(String url) {
        if (!FileUtil.del(url)) {
            throw new BizException("删除失败");
        }
    }

    @Override
    public void rename(String url, String newName) throws BizException {
        if (StringUtils.isBlank(newName)) {
            throw new BizException("名称不能为空");
        }
        File file = new File(url);
        if (file.getName().equals(newName)) {
            throw new BizException("文件名重复");
        }
        FileUtil.rename(file, newName, false);
    }

}
