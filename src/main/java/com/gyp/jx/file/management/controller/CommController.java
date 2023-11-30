package com.gyp.jx.file.management.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.gyp.jx.file.management.utils.crypto.UrlBase64Coder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Log4j2
@RequestMapping("/comm")
public class CommController {

    @Value("${app.key}")
    private String key;

    @RequestMapping(value = "404Img")
    public Mono<ResponseEntity<Resource>> get404Img() {
        // 返回文件下载响应
        return Mono.just(ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE)).body(new ClassPathResource("image/404.jpg")));
    }

    @GetMapping("mp4/{encryptHex}/{url4Encode}/{name}")
    public Mono<ResponseEntity<Resource>> getMp4(@PathVariable String encryptHex, @PathVariable String url4Encode) {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, Base64.decode(key));
        long startTime = Long.parseLong(aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8));
        long endTime = System.currentTimeMillis();
        if ((endTime - startTime) > 1000 * 60 * 60 * 2) {//一个链接只有2个小时的时效性
            return Mono.just(ResponseEntity.badRequest().build());
        }
        String url;
        try {
            url = UrlBase64Coder.decode(url4Encode);
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        Path path = Paths.get(url);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        // 检查文件是否存在
        if (!resource.exists() || !resource.isReadable()) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        long contentLength;
        try {
            contentLength = resource.contentLength();
        } catch (IOException e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        //解决下载文件时文件名乱码问题
        byte[] fileNameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        String filename = new String(fileNameBytes, StandardCharsets.ISO_8859_1);
//        String filename = java.net.URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8);
        // 设置响应头信息
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        // 返回文件下载响应
        return Mono.just(ResponseEntity.ok().headers(headers).contentLength(contentLength).contentType(MediaType.parseMediaType("application/octet-stream")).body(resource));
    }


}