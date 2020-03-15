package com.zsh.file.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 导出文件
 * @author zhongshenghua
 * @since 2019-10-17
 *
 */
public class ExportFileUtil {
	
	//private static final Log log = LogFactory.getLog(ExportFileUtil.class);
	
	//*************************************需求背景******************************************************************************************
	// 由于svn提交代码后需要同步一份代码到行内git,所以需要导出一份文件出来，行内合并，提交git，这样可以大量节省时间，同时也可以减少遗漏，错误等概率
	// 比如说路径是 ：
	//D:\SVN\Control\pabank\Code\dev20190516001\code\com.yss.acs.acclock\src\main\java\com\yss\acs\acclock\base\biz\AccountLockBO.java
	// 则以此创建文件夹,并cp一份文件导出
	//*************************************************************************************************************************************
	
	/**
	 * 按照所给文件路径导出文件
	 * @param rootSourcePath  源代码跟目录
	 * @param filePath	读取文件全路径
	 * @param exportPath  导出目录
	 * @param dic 是否生成全路径目录
	 */
	public  void exportFilesAndCreateDictionary(String rootSourcePath, String filePath,String exportPath, boolean dic){
		/**
		 * 鉴于方便，我们提交完成之后，svn可以拿到路径，路径一个文件一行，
		 * 所以我们就可以复制到txt,excel等文本，然后我们去读这个文件，一行一行取路径，获取文件路径
		 * 
		 */
		try {
			//读取文件内容，获取文件路径
			List<String> list = readTxt(filePath);
			if(list.size()==0){
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>总共读取文件路径："+list.size()+"个！");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>导出完毕！");
				return;
			}
			int count=0;
			int dicCount=0;
			int noneCount=0;
			for(String path : list){
				//System.out.println("获取path---->"+path);
				// 注意路径为源码全路径
				String sourcePath=rootSourcePath+File.separator+path;
			    File sourceFile = new File(sourcePath);
			    if (!sourceFile.exists()){
			    	noneCount++;
			        continue;
			    }
			    if(sourceFile.isDirectory()){
			    	dicCount++;
			    	continue;
			    }
			    //文件名称 带后缀
			   //String sourceFileName = sourceFile.getName();
			   //目标路径
			   String destPath = exportPath+File.separator+path;
			   //创建目标目录
			   File fileDest = new File(destPath);
			   if(!fileDest.exists()){
				   fileDest.getParentFile().mkdirs();
			   }
			   copyFileUsingFileChannels(sourceFile, fileDest);
			   count++;
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>导出完毕！");
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>总共读取文件路径："+list.size()+"个！");
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>共导出有效文件："+count+"个！其中目录："+dicCount+"个，无效路径："+noneCount+"个！");
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>请到【"+exportPath+"】目录下查看！！");
		} catch (IOException e) {
		} finally{
			
		}
	}
	
	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {    
		FileChannel inputChannel = null;    
		FileChannel outputChannel = null;    
		try {
		    inputChannel = new FileInputStream(source).getChannel();
		    outputChannel = new FileOutputStream(dest).getChannel();
		    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
		   inputChannel.close();
		   outputChannel.close();
		}
	}
	
	/**
	 * 解析text内容，按行读取返回
	 * @param filePath
	 * @throws IOException 
	 */
	public  List<String> readTxt(String filePath) {
		List<String> listFilePath = new ArrayList<String>();
        FileInputStream filestream = null;
        InputStreamReader readStream = null;
        BufferedReader reader = null;
		try {
			filestream = new FileInputStream(filePath);
	        byte[] b = new byte[3];
	        filestream.read(b);
	        String ecode="gbk";
	        if (b[0] == -17 && b[1] == -69 && b[2] == -65){
	            ecode="utf-8";
	        }
	        readStream = new InputStreamReader(filestream,ecode);
	        reader = new BufferedReader(readStream);
	        
	        String temp=null;
	        int line=0;//行号
	        while((temp=reader.readLine())!=null){
	            line++;
	            listFilePath.add(temp);
	        }
		} catch (FileNotFoundException e) {
			System.out.println("读取文件错误"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e1){
			System.out.println("读取文件错误"+e1.getMessage());
			e1.printStackTrace();
		}finally{
			if(filestream!=null){
				try {
					filestream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
			if(readStream!=null){
	            try {
					readStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        if(reader!=null){
	            try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
		return listFilePath;
	}
	
	/**
	 * main函数
	 * @param args
	 */
	public static void main(String[] args) {
		String rootSourcePath ="D:\\SVN\\Control\\pabank\\Code\\dev20190516001\\code";
		String filePathCode ="C:\\Users\\Administrator\\Desktop\\代码目录\\filePathCode.txt";
		String filePathTrunk ="C:\\Users\\Administrator\\Desktop\\代码目录\\filePathTrunk.txt";
		String exportPath ="C:/Users/Administrator/Desktop/代码目录/导出代码/STORY79820广发证券关于深圳单市场ETF申赎申请日";
		boolean dic = false;
		ExportFileUtil util = new ExportFileUtil();
		util.exportFilesAndCreateDictionary(rootSourcePath, filePathCode, exportPath, dic);
		rootSourcePath ="D:\\SVN\\Control\\pabank\\Code\\trunk";
		util.exportFilesAndCreateDictionary(rootSourcePath, filePathTrunk, exportPath, dic);
		
	}

}
