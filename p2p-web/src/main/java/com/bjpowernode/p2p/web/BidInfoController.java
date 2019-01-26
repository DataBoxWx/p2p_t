package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:BidInfoController
 * package:com.bjpowernode.p2p.web
 * Descrption:
 *
 * @Date:2018/7/14 15:08
 * @Author:guoxin
 */
@Controller
public class BidInfoController {

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/loan/myInvest")
    public String myInvest(HttpServletRequest request, Model model,
                           @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        //判断是否为第1页
        if (null == currentPage) {
            currentPage = 1;
        }

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备分页查询参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        Integer pageSize = 10;
        paramMap.put("currentPage",(currentPage-1) * pageSize);
        paramMap.put("pageSize",pageSize);
        paramMap.put("uid",sessionUser.getId());

        //根据用户标识分页查询投资记录(用户标识,页码,每页显示条数) -> 分页模型对象
        PaginationVO<BidInfo> paginationVO = bidInfoService.queryBidInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("bidInfoList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);


        return "myInvest";
    }


    @RequestMapping(value = "/loan/invest")
    public @ResponseBody Object invest(HttpServletRequest request,
                                       @RequestParam (value = "loanId",required = true) Integer loanId,
                                       @RequestParam (value = "bidMoney",required = true) Double bidMoney) {
        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取用户标识
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备投资参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("loanId",loanId);
        paramMap.put("bidMoney",bidMoney);

        //用户投资(用户标识,产品标识,投资金额) -> 返回ResultObject
        ResultObject resultObject = bidInfoService.invest(paramMap);

        //判断投资是否成功
        if (StringUtils.equals(Constants.SUCCESS,resultObject.getErrorCode())) {
            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        } else {
            retMap.put(Constants.ERROR_MESSAGE,"投资失败");
            return retMap;
        }

        return retMap;
    }
}
