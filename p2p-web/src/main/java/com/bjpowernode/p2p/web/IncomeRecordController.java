package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:IncomeRecordController
 * package:com.bjpowernode.p2p.web
 * Descrption:
 *
 * @Date:2018/7/14 16:42
 * @Author:guoxin
 */
@Controller
public class IncomeRecordController {

    @Autowired
    private IncomeRecordService incomeRecordService;

    @RequestMapping (value = "/loan/myIncome")
    public String myIncome(HttpServletRequest request, Model model,
                           @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        if (null == currentPage) {
            currentPage = 1;
        }

        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        Integer pageSize = 10;
        paramMap.put("currentPage",(currentPage - 1) * pageSize);
        paramMap.put("pageSize",pageSize);

        PaginationVO<IncomeRecord> paginationVO = incomeRecordService.queryIncomeRecordByPage(paramMap);

        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }


        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("incomeRecordList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);


        return "myIncome";
    }
}
