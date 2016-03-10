package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.HttpClient;
import util.Utils;

public class Main {

	// 正则表达式 解析
	private static Pattern splitResult = Pattern.compile("'(.*?)'");
	private static Pattern g_vsig = Pattern.compile("var g_vsig = \"(.*)\";");

	private static String QQ = "your qq number";
	private static String PASSWORD = "your qq password";
	private static String CHECK_URL = "http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&pt_vcode=1&appid=549000912&js_ver=10151&js_type=1&login_sig=&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.08926539402455091";

	private static String VC_SIG_URL = "http://captcha.qq.com/cap_union_show?clientype=2&aid=549000912&pt_style=32&0.7884523742832243";
	private static String VC_URL = "http://captcha.qq.com/getimgbysig?clientype=2&aid=549000912&0.7884523742832243&rand=0.5994134331122041";

	private static String LOGIN_URL = "http://ptlogin2.qq.com/login?pt_randsalt=0&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=3-12-1457403069370&js_ver=10151&js_type=1&login_sig=eCDelEBa3-nk4FOAjNvHf7TYEyAISBIHnQ84KkaKJolqyCJFT*gEjuDuH9o2Cn45&pt_uistyle=32&aid=549000912&daid=5";

	private static Map<String, String> params = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		HttpClient client = new HttpClient();
		params.clear();
		params.put("uin", QQ);
		String checkResult = client.SendGet(CHECK_URL, params,null);

		List<String> checkResultList = getSplitResult(checkResult);
		if (checkResultList.size() != 5) {
			System.err.println("解析账号验证结果出错!");
			System.exit(0);
		}
		System.out.println("checkResult:" + String.join(" ", checkResultList));

		String pt_vcode_v1 = checkResultList.get(0);
		String verifycode = checkResultList.get(1);
		String qqHex = checkResultList.get(2);
		String pt_verifysession_v1 = checkResultList.get(3);

		if (pt_vcode_v1.equals("1")) {
			params.clear();
			params.put("uin", QQ);
			params.put("cap_cd", verifycode);
			String result = client.SendGet(VC_SIG_URL, params,null);

			Matcher m = g_vsig.matcher(result);
//			System.out.println(result);
			String sig = null;
			if (m.find()) {
				sig = m.group(1);
			} else {
				System.err.println("获取验证码sig错误!");
			}
			params.put("sig", sig);
			client.SaveData(VC_URL, params, null,"./file/vc.jpeg");

			// 创建输入对象
			Scanner sc = new Scanner(System.in);

			// 获取用户输入的字符串
			String str = null;
			System.out.print("需要输入验证码:");
			verifycode = sc.nextLine();
			return;
		}

		params.clear();
		params.put("u", QQ);
		params.put("verifycode", verifycode);
		params.put("pt_vcode_v1", pt_vcode_v1);
		params.put("pt_verifysession_v1", pt_verifysession_v1);
		params.put("p", Utils.encryption(PASSWORD, qqHex, verifycode));
		String loginResult = client.SendGet(LOGIN_URL, params,null);
		
		List<String> loginResultList = getSplitResult(loginResult);
		if (loginResultList.size() != 6) {
			System.err.println("解析账号登录结果出错!");
			System.exit(0);
		}
		System.out.println("loginResult:" + String.join(" ", loginResultList));
		
		//得到重定向地址
		params.put("Referer", "http://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http%3A//qzs.qq.com/qzone/v6/portal/proxy.html&daid=5&&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=22&target=self&s_url=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html");
		params.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/47.0.2526.73 Chrome/47.0.2526.73 Safari/537.36");

		String redirect= client.getRedirectAddr(loginResultList.get(2), null,params);
		
		client.SendGet(redirect, null, null);
		Map<String, String> header=new HashMap<String,String>();
		header.put("Host", "user.qzone.qq.com");
	header.put("Upgrade-Insecure-Requests", "1");
	header.put("Referer", "http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone");
	header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/47.0.2526.73 Chrome/47.0.2526.73 Safari/537.36");
		System.out.println(client.SendGet("http://user.qzone.qq.com/1651155395", null,header));

	}

	private static List<String> getSplitResult(String result) {
		Matcher m = splitResult.matcher(result);
		List<String> resultList = new ArrayList<String>();
		while (m.find()) {
			resultList.add(m.group(1));
		}
		return resultList;
	}
	
}
