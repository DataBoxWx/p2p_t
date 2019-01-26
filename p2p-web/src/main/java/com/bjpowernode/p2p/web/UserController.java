package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.config.Config;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import com.bjpowernode.p2p.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * ClassName:UserController
 * package:com.bjpowernode.p2p.web
 * Descrption:
 *
 * @Date:2018/7/13 9:52
 * @Author:guoxin
 */
// @Controller + 每个方法返回的都是json对象 等同于 @RestController
@Controller
//@RestController
public class UserController {

    @Autowired
    private Config config;

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private IncomeRecordService incomeRecordService;

    /**
     * 验证手机号是否被注册
     * 接口地址:http://localhost:8080/p2p/loan/checkPhone
     * @param request
     * @param phone 必填
     * @return JSON对象
     * JSON对象返回格式:{"errorMessage":"xxxx"}
     */
//    @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.POST) // 等同于 @PostMapping(value="/loan/checkPhone")
    @PostMapping(value = "/loan/checkPhone")
    public @ResponseBody Object checkPhone(HttpServletRequest request,
                                           @RequestParam (value = "phone",required = true) String phone) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //根据手机号去查询(手机号) -> 返回boolean|int|User
        User user = userService.queryUserByPhone(phone);

        //判断用户是否为空
        if (null != user) {
            retMap.put(Constants.ERROR_MESSAGE,"该手机号已被注册,请更换手机号码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }

//    @RequestMapping(value = "/loan/checkCaptcha",method = RequestMethod.GET) //赞同于 @GetMapping(value="/loan/checkCaptcha")
    @GetMapping(value = "/loan/checkCaptcha")
    public @ResponseBody Object checkCaptcha(HttpServletRequest request,
                               @RequestParam (value = "captcha",required = true) String captcha) {

        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取图形验证码
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        //比较用户输入的图形验证码与session中的图形验证码是否一致
        if (!StringUtils.equalsIgnoreCase(sessionCaptcha,captcha)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
            return retMap;
        }

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        return retMap;
    }

    @RequestMapping(value = "/loan/register")
    public @ResponseBody Object register(HttpServletRequest request,
                           @RequestParam (value = "phone",required = true) String phone,
                           @RequestParam (value = "loginPassword",required = true) String loginPassword,
                           @RequestParam (value = "replayLoginPassword",required = true) String replayLoginPassword) {

        Map<String,Object> retMap = new HashMap<String,Object>();

        //-------------验证参数----------------

        if (!Pattern.matches("^1[1-9]\\d{9}$",phone)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
            return retMap;
        }

        if (!StringUtils.equals(loginPassword,replayLoginPassword)) {
            retMap.put(Constants.ERROR_MESSAGE,"两次密码不一致");
            return retMap;
        }

        //-------------用户注册-----------------

        //调用用户业务接口中的注册方法(手机号,登录密码)[新增用户信息,开立帐户] -> 返回boolean|int|处理结果对象ResultObject
        ResultObject resultObject = userService.register(phone,loginPassword);

        //判断注册是否成功
        if (StringUtils.equals(Constants.SUCCESS,resultObject.getErrorCode())) {
            //注册成功:实际上session中应该保存用户的信息
            request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(phone));

            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        } else {
            //注册失败
            retMap.put(Constants.ERROR_MESSAGE,"注册失败,请重试...");
            return retMap;
        }

        return retMap;
    }



    @RequestMapping(value = "/loan/myFinanceAccount")
    public @ResponseBody FinanceAccount myFinanceAccount(HttpServletRequest request) {

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户资金信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }


    @RequestMapping(value = "/loan/verifyRealName")
    public @ResponseBody Object verifyRealName(HttpServletRequest request,
                                 @RequestParam (value = "realName",required = true) String realName,
                                 @RequestParam (value = "idCard",required = true) String idCard,
                                 @RequestParam (value = "replayIdCard",required = true) String replayIdCard) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //验证参数
        if (!Pattern.matches("(^[一-龥]{2,8}$)",realName)) {
            retMap.put(Constants.ERROR_MESSAGE,"真实姓名只支持中文");
            return retMap;
        }

        if (!Pattern.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",idCard)) {
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的身份证号码");
            return retMap;
        }

        if (!StringUtils.equals(idCard,replayIdCard)) {
            retMap.put(Constants.ERROR_MESSAGE,"两次输入身份证号码不一致");
            return retMap;
        }


        //进行实名认证
        //准备实名认证参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("appkey",config.getRealName_appkey());//您申请的appkey
        paramMap.put("cardNo",idCard);//身份证号码
        paramMap.put("realName",realName);//真实姓名

        //调用互联网实名认证接口(按照接口要求传递参数) -> 返回消息(JSON|xml),对这些格式的字符串进行解析
        String resultJson = HttpClientUtils.doPost(config.getRealName_url(), paramMap);
//        String resultJson = "{\"code\":\"10000\",\"charge\":false,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"乐天磊\",\"idcard\":\"350721197702134399\",\"isok\":true}}}";

        //解析json格式的字符串,使用阿里提供了fastJson
        //1.将json格式的字符串转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(resultJson);

        //2.获取通信code字段值,判断是否通信成功
        String code = jsonObject.getString("code");
        //判断通信结果
        if (StringUtils.equals("10000",code)) {
            //2.1通信成功
            //3.获取业务处理结果isok
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            //判断业务处理结果
            if (isok) {
                //从session中获取用户信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                //3.1 业务处理成功
                //4.将身份证号码和真实姓名更新到当前用户的信息里
                User updateUser = new User();
                updateUser.setId(sessionUser.getId());
                updateUser.setName(realName);
                updateUser.setIdCard(idCard);

                int updateCount = userService.modifyUserById(updateUser);

                if (updateCount > 0) {

                    //更新session中用户的信息
                    request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(sessionUser.getPhone()));

                    retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

                } else {
                    retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
                    return retMap;
                }

            } else {
                //3.2 业务处理失败
                retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
                return retMap;
            }

        } else {
            //2.2通信失败
            retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
            return retMap;
        }
        return retMap;
    }


    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request) {

        //让session失效
        request.getSession().invalidate();

        //删除指定session中的值
//        request.getSession().removeAttribute(Constants.SESSION_USER);

        return "redirect:/index";
    }

    @RequestMapping(value = "/loan/loadStat")
    public @ResponseBody Object loadStat() {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

        //平台注册总人数
        Long allUserCount = userService.queryAllUserCount();

        //平台总投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();

        retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        retMap.put(Constants.ALL_USER_COUNT,allUserCount);
        retMap.put(Constants.ALL_BID_MONEY,allBidMoney);

        return retMap;
    }


    @RequestMapping(value = "/loan/login")
    public @ResponseBody Object login(HttpServletRequest request,
                                      @RequestParam (value = "phone",required = true) String phone,
                                      @RequestParam (value = "loginPassword",required = true) String loginPassword) {
        Map<String,Object> retMap = new HashMap<String,Object>();


        //调用用户业务接口中的登录方法(手机号,登录密码)[根据手机号和密码查询,更新最近登录时间] -> 返回User
        User user = userService.login(phone,loginPassword);

        //判断用户是否为空
        if (null == user) {
            retMap.put(Constants.ERROR_MESSAGE,"用户名或密码有误");
            return retMap;
        }

        //将用户的信息存放到session中
        request.getSession().setAttribute(Constants.SESSION_USER,user);

        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);


        return retMap;
    }


    @RequestMapping (value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request,Model model) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户可用资金
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        //准备查询参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());//用户标识
        paramMap.put("currentPage",0);//页码
        paramMap.put("pageSize",5);


        //获取最近的投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoTopByUid(paramMap);


        //获取最近的充值记录
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRechargeRecordTopByUid(paramMap);


        //获取最近的收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordService.queryIncomeRecordTopByUid(paramMap);


        model.addAttribute("financeAccount",financeAccount);
        model.addAttribute("bidInfoList",bidInfoList);
        model.addAttribute("rechargeRecordList",rechargeRecordList);
        model.addAttribute("incomeRecordList",incomeRecordList);



        return "myCenter";
    }











}













