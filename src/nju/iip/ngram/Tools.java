package nju.iip.ngram;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class Tools {
	
	 /**
     * 
    * @Title: segStr
    * @Description: 返回帖子的词序列
    * @param @param content
    * @param @return    
    * @return ArrayList<String>   
    * @throws
     */
    public static ArrayList<String> segStr(String content){
        // 分词
    	StringReader input = new StringReader(content);
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lexeme = null;
        ArrayList<String> words = new ArrayList<String>();
        try {
            while ((lexeme = iks.next()) != null) {
            	if(lexeme.getLength()>1){
            		words.add(lexeme.getLexemeText());
            	}
            }       
        }catch(IOException e) {
            e.printStackTrace();
        }
        return words;
    }
    
    
    /**
     * @decription 计算平均值
     * @param list
     * @return
     */
    public static Double getMean(ArrayList<Double>list){
    	Double sum=0.0;
		for(int i=0;i<list.size();i++){
			sum=sum+list.get(i);
		}
		Double mean=sum/10;
		return mean;
    }

}
