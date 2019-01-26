var referrer = "";//登录后返回页面

// alert(referrer);//true
referrer = document.referrer;//跳转至当前页面之前页面的URL
// alert(referrer);//false

//判断是否为空
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


$(function () {
	//加载平台信息
	loadStat();
});

/*$(document).ready(function () {

});*/

function loadStat() {
	$.ajax({
		url:"loan/loadStat",
		type:"get",
		success:function (jsonObject) {
			$(".historyAverageRate").html(jsonObject.historyAverageRate);
			$("#allUserCount").html(jsonObject.allUserCount);
			$("#allBidMoney").html(jsonObject.allBidMoney);
        }
	});
}


//验证手机号码
function checkPhone() {
	//获取手机号
	var phone = $.trim($("#phone").val());

	if (!phone) {
		$("#showId").html("");
		$("#showId").html("请输入手机号码");
		return false;
	} else if(!/^1[1-9]\d{9}$/.test(phone)) {
        $("#showId").html("");
        $("#showId").html("请输入正确的手机号码");
        return false;
	} else {
        $("#showId").html("");
	}

	return true;

}



//验证登录密码
function checkLoginPassword() {
	//获取登录密码
	var loginPassword = $.trim($("#loginPassword").val());

	if ("" == loginPassword) {
        $("#showId").html("");
        $("#showId").html("请输入登录密码");
        return false;
	} else {
        $("#showId").html("");
	}
	return true;
}

//验证图形验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var flag = true;

	if (!captcha) {
        $("#showId").html("");
        $("#showId").html("请输入图形验证码");
        return false;
	} else {
		$.ajax({
			url:"loan/checkCaptcha",
			type:"get",
			data:"captcha="+captcha,
			async:false,
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
                    $("#showId").html("");
                    flag = true;
				} else {
                    $("#showId").html("");
                    $("#showId").html(jsonObject.errorMessage);
                    flag = false;
				}
            },
			error:function () {
                $("#showId").html("");
                $("#showId").html("系统繁忙,请稍后重试...");
                flag = false;
            }
		});
	}

	if (!flag) {
		return false;
	}
	return true;
}


//用户登录
function login() {

	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());


	if(checkPhone() && checkLoginPassword() && checkCaptcha()) {
		$("#loginPassword").val($.md5(loginPassword));

		$.ajax({
			url:"loan/login",
			type:"post",
			data:"phone="+phone+"&loginPassword="+$.md5(loginPassword),
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					if (!referrer) {
						window.location.href = "index";
					} else {
						window.location.href = referrer;
					}

				} else {
                    $("#showId").html("");
                    $("#showId").html("用户名或密码有误");
				}
            },
			error:function () {
                $("#showId").html("");
                $("#showId").html("系统繁忙,请稍后重试...");
            }
		});
	}

}










