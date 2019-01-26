package com.bjpowernode.p2p;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:QRCodeTest
 * package:com.bjpowernode.p2p
 * Descrption:
 *
 * @Date:2018/7/19 9:23
 * @Author:guoxin
 */
public class QRCodeTest {


    @Test
    public void generateQRCode() throws WriterException, IOException {
        //使用fastJson生成json格式字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company","www.bjpowernode.com");
        jsonObject.put("teacher","guoxin");
        JSONObject address = new JSONObject();
        address.put("province","henan");
        address.put("city","anyang");
        jsonObject.put("address",address);

//        String content = jsonObject.toString();
        String content = "weixin://wxpay/bizpayurl?pr=7FGblcA";

        int width = 200;
        int heght = 200;

        Map<EncodeHintType,Object> hint = new HashMap<EncodeHintType, Object>();
        hint.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,width,heght,hint);

        String filePath = "D://";
        String fileName = "QRCode.JPEG";

        //创建一个路径 对象
        Path path = FileSystems.getDefault().getPath(filePath,fileName);

        //通过矩阵转换器,转换成一张图片
        MatrixToImageWriter.writeToPath(bitMatrix,"JPEG",path);

        System.out.println("生成成功");

    }
}
