package com.gyp.jx.file.management.controller.api;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.gyp.jx.file.management.config.FileSearchTypeEnum;
import com.gyp.jx.file.management.config.ResultBody;
import com.gyp.jx.file.management.entity.LocalFile;
import com.gyp.jx.file.management.service.ILocalFileService;
import com.gyp.jx.file.management.utils.crypto.UrlBase64Coder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/localFile")
public class LocalFileController {

    @Autowired
    private ILocalFileService iLocalFileService;

    @Value("${app.default-home}")
    private String defaultHome;

    @Value("${app.key}")
    private String key;

    @GetMapping("list")
    public ResponseEntity<ResultBody> list(@RequestParam(value = "path", required = false) String dirUrl4Encode,
                                           @RequestParam(defaultValue = "0") Integer startNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) throws Exception {
        String dirUrl = "";
        if (StringUtils.isBlank(dirUrl4Encode)) {
            dirUrl = String.valueOf(defaultHome);
        } else {
            dirUrl = UrlBase64Coder.decode(dirUrl4Encode);
        }
        Map<String, Object> map = new HashMap<>();
        if (startNum == 0 && !StringUtils.equals(dirUrl, defaultHome)) {
            map.put("parent", iLocalFileService.getParent(dirUrl));
        }
        map.put("rows", iLocalFileService.getList(dirUrl, startNum, pageSize));
        map.put("total", iLocalFileService.getListTotal(dirUrl));
        return ResponseEntity.ok(ResultBody.success(map));
    }

    @GetMapping("keywordSearchList")
    public ResponseEntity<ResultBody> keywordSearchList(FileSearchTypeEnum searchType,
                                                        @RequestParam(value = "path") String dirUrl4Encode,
                                                        String keyword) throws Exception {
        String dirUrl = "";
        if (StringUtils.isBlank(dirUrl4Encode)) {
            dirUrl = String.valueOf(defaultHome);
        } else {
            dirUrl = UrlBase64Coder.decode(dirUrl4Encode);
        }
        Map<String, Object> map = new HashMap<>();
        List<LocalFile> localFiles = iLocalFileService.keywordSearchList(searchType, dirUrl, keyword);
        map.put("rows", localFiles);
        map.put("total", localFiles.size());
        return ResponseEntity.ok(ResultBody.success(map));
    }

    @DeleteMapping("deleteFile/{path}")
    public ResponseEntity<ResultBody> deleteFile(@PathVariable(value = "path") String dirUrl4Encode) throws Exception {
        String url = UrlBase64Coder.decode(dirUrl4Encode);
        iLocalFileService.delete(url);
        return ResponseEntity.ok(ResultBody.success());
    }

    @GetMapping("/getFileOpenKey")
    public ResponseEntity<ResultBody> getFileOpenKey() {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, Base64.decode(key));
        String encryptHex = aes.encryptHex(String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(ResultBody.success(encryptHex));
    }



}