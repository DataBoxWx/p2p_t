package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.config.Config;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.OnlyNumberService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.util.DateUtils;
import com.bjpowernode.p2p.util.HttpClientUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.control.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:RechargeRecordController
 * package:com.bjpowernode.p2p.web
 * Descrption:
 *
 * @Date:2018/7/14 16:09
 * @Author:guoxin
 */
@Controller
public class RechargeRecordController {

    @Autowired
    private Config config;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private OnlyNumberService onlyNumberService;

    @RequestMapping(value = "/loan/myRecharge")
    public String myRecharge(HttpServletRequest request, Model model,
                             @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        //判断是否为第1页
        if (null == currentPage) {
            currentPage = 1;
        }


        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);


        //分页查询参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        Integer pageSize = 10;
        paramMap.put("currentPage",(currentPage - 1) * pageSize);
        paramMap.put("pageSize",pageSize);

        //根据用户标识分页查询充值记录(用户标识,页码,每页显示条数) -> 返回分页模型对象
        PaginationVO<RechargeRecord> paginationVO = rechargeRecordService.queryRechargeRecordByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("rechargeRecordList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);


        return "myRecharge";
    }


    @RequestMapping(value = "/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                   @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {


        System.out.println("-----------toAlipayRecharge-----------------");

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);


        //生成全局唯一充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimeStamp() + onlyNumberService.getOnlyNumber();


        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝充值");
        rechargeRecord.setRechargeStatus("0");//0充值中,1充值成功,2充值失败

        int addCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addCount > 0) {
            model.addAttribute("p2p_pay_alipay_url",config.getP2p_pay_alipay_url());
            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("subject","支付宝充值");
            model.addAttribute("body","支付宝充值");
        } else {
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }

        return "toAlipay";
    }


    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam (value = "signVerified",required = true) String signVerified,
                             @RequestParam (value = "total_amount",required = true) Double total_amount) {

        System.out.println("-----------p2p---alipayBack---------");

        //验证签名
        if (StringUtils.equals(signVerified,Constants.SUCCESS)) {

            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("out_trade_no",out_trade_no);

            //调用pay工程的订单查询接口 -> 返回订单消息
            String resultJson = HttpClientUtils.doPost(config.getP2p_pay_alipay_query_url(), paramMap);

            System.out.println("resultJson=" + resultJson);

            //使用fastjson解析json格式字符串
            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            //获取指定的key值
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

            //获取通信标识
            String code = tradeQueryResponse.getString("code");

            //判断通信是否成功
            if (StringUtils.equals("10000",code)) {

                //获取交易结果
                String trade_status = tradeQueryResponse.getString("trade_status");

                /*交易状态：
                WAIT_BUYER_PAY（交易创建，等待买家付款）
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                TRADE_SUCCESS（交易支付成功）
                TRADE_FINISHED（交易结束，不可退款）*/

                if ("TRADE_SUCCESS".equals(trade_status)) {
                    User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                    //充值(更新帐户可用余额,更新充值记录的状态为1)
                    paramMap.put("uid",sessionUser.getId());
                    paramMap.put("rechargeMoney",total_amount);
                    ResultObject resultObject = rechargeRecordService.recharge(paramMap);

                    if (!Constants.SUCCESS.equals(resultObject.getErrorCode())) {
                        model.addAttribute("trade_msg","充值失败");
                        return "toRechargeBack";
                    }
                }

                if ("TRADE_CLOSED".equals(trade_status)) {
                    //将充值记录状态更新为2充值失败
                    RechargeRecord updateRechargeRecord = new RechargeRecord();
                    updateRechargeRecord.setRechargeNo(out_trade_no);
                    updateRechargeRecord.setRechargeStatus("2");
                    int updateStatusCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(updateRechargeRecord);
                    if (updateStatusCount <= 0) {
                        model.addAttribute("trade_msg","充值失败");
                        return "toRechargeBack";
                    }
                }


            } else {
                model.addAttribute("trade_msg","充值失败");
                return "toRechargeBack";
            }

        } else {
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }




        return "redirect:/loan/myRecharge";
    }






    @RequestMapping(value = "/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request,Model model,
                                  @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) {


        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);


        //生成全局唯一充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimeStamp() + onlyNumberService.getOnlyNumber();


        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("微信充值");
        rechargeRecord.setRechargeStatus("0");//0充值中,1充值成功,2充值失败

        int addCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

        if (addCount > 0) {

            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        } else {
            model.addAttribute("trade_msg","充值失败");
            return "toRechargeBack";
        }


        return "showQRCode";
    }


    @RequestMapping (value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam (value = "rechargeMoney",required = true) String rechargeMoney,
                               @RequestParam (value = "rechargeNo",required = true) String rechargeNo) throws IOException, WriterException {


        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("body","微信充值");
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);

        //调用pay工程的统一下单API接口 -> 返回code_url
        //获取生成二维码图形的规则
        String resultJson = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

        //解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(resultJson);

        //获取通信标识
        String return_code = jsonObject.getString("return_code");

        //判断通信是否成功
        if (StringUtils.equals(Constants.SUCCESS,return_code)) {

            //获取业务处理结果
            String result_code = jsonObject.getString("result_code");

            //判断业务处理结果
            if (StringUtils.equals(Constants.SUCCESS,result_code)) {
                //获取code_url
                String code_url = jsonObject.getString("code_url");

                //生成一个二维码图片
                int width = 200;
                int heght = 200;

                Map<EncodeHintType,Object> hint = new HashMap<EncodeHintType, Object>();
                hint.put(EncodeHintType.CHARACTER_SET,"UTF-8");

                //创建一个矩阵对象
                BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE,width,heght,hint);

                //获取输出流对象
                OutputStream outputStream = response.getOutputStream();

                //矩阵对象转换器
                MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);

                outputStream.flush();
                outputStream.close();

            } else {
                response.sendRedirect(request.getContextPath() + "/toRechargeBack");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/toRechargeBack");
        }



    }
}
