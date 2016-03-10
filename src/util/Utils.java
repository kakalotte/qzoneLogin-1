package util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Utils {
	public static String encryption(String password,String qq,String verifycode){
		try{
//			ScriptEngineManager manager = new ScriptEngineManager();
//			ScriptEngine engine = manager.getEngineByName("JavaScript");
//			// read script file
//			engine.eval(Files.newBufferedReader(Paths.get("./file/encrytion.js"), StandardCharsets.UTF_8));
//
//			Invocable inv = (Invocable) engine;
//			// call function from script file
//			return inv.invokeFunction("getEncryption", password,qq,verifycode,false).toString();
			
//			 ScriptEngine engine =new ScriptEngineManager().getEngineByName("JavaScript");
//			 engine.eval("load('./file/encrytion.js')");
//			 Invocable inv = (Invocable) engine;
//			 return inv.invokeFunction("getEncryption",password,qq,verifycode,false).toString();
			
//			ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("JavaScript");
//			nashorn.eval("load('./file/password.js')");
//			String str= nashorn.eval(String.format("$.Encryption.getEncryption('%s','%s','%s',%b)", password,qq,verifycode,false)).toString();
//			System.out.println(str);
//			return str;
			
			try{
				ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
				nashorn.eval("load('./file/password.js')");
				return nashorn.eval(String.format("$.Encryption.getEncryption('%s','%s','%s',%b)", password,qq,verifycode,false)).toString();
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}


		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
