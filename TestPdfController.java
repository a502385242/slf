package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * @author Steven
 * @version 1.0
 * @description com.demo.controller
 * @date 2020-4-21
 */
@RestController
@RequestMapping("/testPdf")
public class TestPdfController {

    //http://localhost:8080/testPdf/download
    //下面的方法二有优化
    @RequestMapping("/download")
    public void testPdf(HttpServletResponse response, HttpServletRequest request) {
        InputStream inputStream=null;
        try {
            File file = new File("D:\\LearningCode\\springboot_helloworld\\src\\main\\resources\\testFiles\\xy天域.pdf");
            inputStream=new FileInputStream(file);
            fileDownload(file.getName(),inputStream,response);
        }catch (Exception e){
            e.printStackTrace();
            //log.error("出现异常了，请稍后重试");
        }
    }

    //文件下载方法，可以防止util中
    public static void fileDownload(String filename, InputStream input, HttpServletResponse response) {
        try {
            //保证中文名不乱码
            filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            // 清空response
            response.reset();
            // 设置response的Header
            //response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            //关闭，即下载
            toClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //http://localhost:8080/testPdf/download2
    //方法二在方法一的基础上进行了优化，在实现功能的基础上考虑了性能。
    // 从上面代码可以看出，方法一在大文件下载时会占用很多内存资源，多个用户同时下载大文件会存在宕机的隐患 。
    // 实际项目中该功能是从分布式文件存储系统上下载文件响应给用户，本例进行了简化。
    @RequestMapping("/download2")
    public void testPdf2(HttpServletResponse response, HttpServletRequest request) {
        InputStream inputStream=null;
        try {
            File file = new File("D:\\LearningCode\\springboot_helloworld\\src\\main\\resources\\testFiles\\xy天域.pdf");
            inputStream=new FileInputStream(file);
            fileDownload2(file.getName(),inputStream,response);
        }catch (Exception e){
            e.printStackTrace();
            //log.error("出现异常了，请稍后重试");
        }
    }

    //文件下载方法，可以防止util中
    public static void fileDownload2(String filename, InputStream input, HttpServletResponse response) {
        try {
            //保证中文名不乱码
            filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            byte[] buffer = new byte[4096];
            int readLength = 0;
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            while ((readLength=input.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                toClient.write(bytes);
            }
            input.close();
            toClient.flush();
            toClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 预览pdf
     */
    @GetMapping(value = "previewDocument")
    public void previewDocument(HttpServletResponse response,String fileName) throws Exception{
        String downloadPath = fileName;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //相应二进制流
            response.setContentType("application/pdf");
            //根据网络文件地址创建URL
            String filterFileName = downloadPath.trim();
            //这里对中文名进行编码，如果不进行编码，下面使用 会报错400；
            String fileNameTemp = URLEncoder.encode(filterFileName,"GBK");
            //这个url要是浏览器访问就直接下载文件的那种url
            String newUrl = fileNameTemp;
            //目前这行报错，http://localhost:8080/testPdf/previewDocument?fileName=http://localhost:8080/testPdf/download
            // 需要http://www.mycompany.com:8080/index.pdf这种包含路径和文件名的url
            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GRT");
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);
            int responsecode = connection.getResponseCode();
            //获取此路径的链接
            URLConnection conn = url.openConnection();
            if (responsecode ==200){
                //构造读取流
                bis = new BufferedInputStream(connection.getInputStream());
            }
            //构造输出流
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;
            //每次读取缓存大小的流，写到输出流
            while (-1!=(bytesRead = bis.read(buff,0,buff.length))){
                bos.write(buff,0,bytesRead);
            }

        }catch (Exception e){
            //throw new Exception("文件预览失败");
            e.printStackTrace();
        }
        finally {
            try {
                if (null!=bis){
                    bis.close();
                }
                if (null!=bos){
                    bos.close();
                }
            }catch (IOException e){
                throw new Exception();
            }
        }

    }
}
