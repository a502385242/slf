package com.demo.Utils;

import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Base64;

public class Base64Utils{
    /**
     * 将inputstream转为Base64
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static String inputStream2Base64(InputStream is) throws Exception {
        byte[] data = null;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new Exception("输入流关闭异常");
                }
            }
        }

        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * base64转inputStream
     *
     * @param base64string
     * @return
     */
    public static InputStream base2InputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static InputStream base2InputStream2(String base64string, OutputStream out) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(base64string);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0){
                    b[i]+=256;
                }
            }
            out.write(b);
            out.flush();
            out.close();
            //stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }
}

