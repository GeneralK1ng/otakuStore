package com.otaku.controller.admin;

import com.otaku.constant.MessageConstant;
import com.otaku.result.Result;
import com.otaku.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
        /**
     * 文件上传
     * @param file 选择上传的文件
     * @return 上传成功返回文件路径，失败返回错误信息
     */
    @PostMapping("upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取原始文件名的后缀（如：png、jpg）
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构造新文件名称（使用UUID作为文件名前缀）
            String objectName = UUID.randomUUID().toString() + extension;
            // 构造文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
