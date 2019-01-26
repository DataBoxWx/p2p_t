package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * package:com.bjpowernode.p2p.web
 * Descrption:
 *
 * @Date:2018/7/12 11:15
 * @Author:guoxin
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        //判断页码是否有值
        if (null == currentPage) {
            //设置默认为第1页
            currentPage = 1;
        }

        //准备分页查询参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("currentPage",(currentPage - 1) * Constants.PAGE_SIZE);
        paramMap.put("pageSize",Constants.PAGE_SIZE);

        //判断产品类型是否有值
        if (null != ptype) {
            paramMap.put("productType",ptype);
        }


        //分页查询产品信息列表(产品类型,页码,每页显示条数) -> 返回(集合<产品>,总条数)分页模型对象PaginataionVO<产品>
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / Constants.PAGE_SIZE;
        int mod = paginationVO.getTotal().intValue() % Constants.PAGE_SIZE;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);

        //判断类型是否有值
        if (null != ptype) {
            model.addAttribute("ptype",ptype);
        }

        //TODO
        //查询用户投资排行榜

        return "loan";
    }


    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam (value = "id",required = true) Integer id) {

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);


        //根据产品标识获取该产品的所有投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByProductId(id);

        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidInfoList",bidInfoList);

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //判断用户是否登录
        if (null != sessionUser) {
            //根据用户标识获取帐户资金信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }



        return "loanInfo";
    }
}
















