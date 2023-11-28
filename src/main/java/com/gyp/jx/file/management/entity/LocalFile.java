package com.gyp.jx.file.management.entity;

import lombok.Data;

@Data
public class LocalFile {
    private String fileName; //文件名
    private Long fileSize; //文件大小
    private String lastTime; //最后更新时间
    private String url; //文件地址
    private String url4Encode; //编码后的文件地址
    private Boolean isFile;  //是否文件
    private Integer childrenSize; // 子文件个数
}
