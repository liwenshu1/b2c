package com.mr.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadService {

    @Autowired
    FastFileStorageClient storageClient;

    public String upload(MultipartFile file) throws IOException {
        // 1、图片信息校验
//            // 1)校验文件类型
//            String type = file.getContentType();
//            if (!suffixes.contains(type)) {
//                logger.info("上传失败，文件类型不匹配：{}", type);
//                return null;
//            }
//            // 2)校验图片内容
//            BufferedImage image = ImageIO.read(file.getInputStream());
//            if (image == null) {
//                logger.info("上传失败，文件内容不符合要求");
//                return null;
//            }
        // 2、将图片上传到FastDFS
        // 2.1、获取文件后缀名
        String imgName = file.getOriginalFilename();
        String ex = imgName.substring(imgName.lastIndexOf(".") + 1);
        //上传
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ex, null);    //保存一个文件
        String fullPath = storePath.getFullPath();  //获取它的全路径
        System.out.println("http://image.b2c.com/" + fullPath);
        return "http://image.b2c.com/"+  fullPath;
    }
}
