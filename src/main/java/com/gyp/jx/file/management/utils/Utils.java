package com.gyp.jx.file.management.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class Utils {

    public static final Collator CMP = Collator.getInstance(java.util.Locale.CHINA);//中文排序器
    private static String formatFileNameNumber(String fileName) {
        return ReUtil.replaceAll(fileName, "\\d+", (match) -> StringUtils.leftPad(match.group(), 6, "0"));
    }

    public static List<Path> chinaSort(List<Path> list) {
        list.sort((o1, o2) -> {
            if ((Files.isRegularFile(o1) == Files.isRegularFile(o2)) || (Files.isDirectory(o1) == Files.isDirectory(o2))) {
                String d1Name = o1.getFileName() == null ? "" : formatFileNameNumber(o1.getFileName().toString());
                String d2Name = o2.getFileName() == null ? "" : formatFileNameNumber(o2.getFileName().toString());
                CollationKey c1 = CMP.getCollationKey(d1Name);
                CollationKey c2 = CMP.getCollationKey(d2Name);
                return CMP.compare(c1.getSourceString(), c2.getSourceString());
            } else if (Files.isDirectory(o1)) {
                return -1;
            } else {
                return 1;
            }
        });
        return list;
    }


    /**
     * 文件删除并且删除该目录下的所有文件
     *
     * @param file 文件或文件夹
     */
    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    delFile(f);
                }
            }
        }
        log.info("删除文件：{},状态:{}", file.getAbsolutePath(), file.delete());
    }

    public static void getCmd(List<String> commands) {
        StringBuilder cmd = new StringBuilder();
        for (String s : commands) {
            cmd.append(s).append(" ");
        }
        log.info("命令：" + cmd);
    }

    /**
     * Linux的设置软链接
     *
     * @param sourceUrl 源文件
     * @param targetUrl 软链接地址
     */
    public static void createLinkForLinux(String sourceUrl, String targetUrl) {
        List<String> commend = new ArrayList<>();
        commend.add("ln");
        commend.add("-s");
        commend.add(sourceUrl);
        commend.add(targetUrl);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            getCmd(commend);
            builder.command(commend);
            builder.redirectErrorStream(true);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("创建Linux链接失败！源文件地址：" + sourceUrl + "，目标地址：" + targetUrl);
        }
    }

    public static void rmDirForLinux(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        if (url.equals("/") || url.equals("/*")) {
            return;
        }
        List<String> commend = new ArrayList<>();
        commend.add("rm");
        commend.add("-rf");
        commend.add(url);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.redirectErrorStream(true);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("删除失败，删除地址：" + url);
        }
    }

    /**
     * 判断一个文件是否是链接文件
     *
     * @param file 文件
     * @return boolean
     * @throws IOException 异常
     */
    public static boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }
}
