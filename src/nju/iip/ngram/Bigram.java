package nju.iip.ngram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * @since 2014-11-7
 * @author wangqiang
 *
 */
public class Bigram {
	
	private static String file_path="lily";//文本路径
	
	/**
	 * 所有帖子集合
	 */
	private static Map<String,ArrayList<Post>>all_post_list=new HashMap<String,ArrayList<Post>>();
	
	
	/**
	 * @description 获得某个板块下帖子的集合
	 * @param txt_name
	 * @return
	 * @throws IOException 
	 */
	public static ArrayList<Post>getPostList(String txt_name){
		ArrayList<Post>post_list=new ArrayList<Post>();
		String txt_path=file_path+File.separator+txt_name;
		try{
			FileInputStream fs=new FileInputStream(txt_path);
			InputStreamReader is=new InputStreamReader(fs,"UTF-8");
			BufferedReader br=new BufferedReader(is);
			String line=br.readLine();
			while(line!=null){
				Post post=new Post(txt_name,Tools.segStr(line));
				post_list.add(post);
				line=br.readLine();
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return post_list;
	}
	
	
	/**
	 * @description 获得所有帖子集合
	 * @return
	 * @throws IOException 
	 */
	public static Map<String,ArrayList<Post>>getAllpostList(){
		File f=new File(file_path);
		String[] txt_list=f.list();
		for(int i=0;i<txt_list.length;i++){
			all_post_list.put(txt_list[i], getPostList(txt_list[i]));
		}
		return all_post_list;
		
	}
	
	
	/**
	 * @description 创建某类的词典
	 * @param sample
	 * @return
	 */
	public static Map<String,Integer>creatDictionaryMap(ArrayList<Post>sample){
		Map<String,Integer>dictionary_map=new LinkedHashMap<String,Integer>();
		for(int i=0;i<sample.size();i++){
			Post post=sample.get(i);
			ArrayList<String>word_sequence=post.get_words_sequence();
			for(int j=0;j<word_sequence.size();j++){
				if(!dictionary_map.containsKey(word_sequence.get(j))){
					dictionary_map.put(word_sequence.get(j), 1);
				}
				else{
					dictionary_map.put(word_sequence.get(j),dictionary_map.get(word_sequence.get(j))+1);
				}
			}
		}
		return dictionary_map;
	}
		
	
	/**
	 * @description 计算某一类帖子对应的bigram矩阵
	 * @param sample
	 * @return BigramMatrix
	 */
	public static BigramMatrix creatMatrix(ArrayList<Post>sample){
		Map<String,Integer>dictionary_map=creatDictionaryMap(sample);
		HashMap<String,Integer>bigram_map=new HashMap<String,Integer>();
		for(int i=0;i<sample.size();i++){
			Post post=sample.get(i);
			ArrayList<String>word_sequence=post.get_words_sequence();
			for(int j=0;j<word_sequence.size()-1;j++){
				String first_word=word_sequence.get(j);
				String second_word=word_sequence.get(j+1);
				
				String two_continuous_word=first_word+second_word;
				
				if(!bigram_map.containsKey(two_continuous_word)){
					bigram_map.put(two_continuous_word, 1);
				}
				
				else{
					bigram_map.put(two_continuous_word, bigram_map.get(two_continuous_word)+1);
				}
			}
		}
		BigramMatrix bigramMatrix=new BigramMatrix(bigram_map,dictionary_map);
		Set<String>words=dictionary_map.keySet();
		int num=0;
		for(String word:words){
			num=num+dictionary_map.get(word);
		}
		bigramMatrix.setWordsNum(num);
		return bigramMatrix;
	}
	
	
	/**
	 * @description 求得十个bigram矩阵
	 * @param train_sample
	 * @return
	 */
	public static Map<String,BigramMatrix>getTenMatrixMap(Map<String,ArrayList<Post>>train_sample){
		Map<String,BigramMatrix>ten_matrix_map=new HashMap<String,BigramMatrix>();
		Set<String>txt_names=train_sample.keySet();
		for(String txt_name:txt_names){
			ten_matrix_map.put(txt_name, creatMatrix(train_sample.get(txt_name)));
		}
		return ten_matrix_map;
	}
	
	/**
	 * @description 计算某篇帖子属于某个类的概率
	 * @param post
	 * @param matrix
	 * @return probility
	 */
	public static double getProbility(Post post,BigramMatrix bigramMatrix){
		double probility=0.0;
		HashMap<String,Integer>bigram_map=bigramMatrix.getBigramMap();
		Map<String,Integer>dictionary_map=bigramMatrix.getDictionary_map();
		ArrayList<String>words_sequence=post.get_words_sequence();
		for(int i=0;i<words_sequence.size()-1;i++){
			if(dictionary_map.containsKey(words_sequence.get(i))&&dictionary_map.containsKey(words_sequence.get(i+1))){
				String first_word=words_sequence.get(i);
				String second_word=words_sequence.get(i+1);
				String two_continuous_word=first_word+second_word;
				int two_continuous_word_tf=0;
				if(bigram_map.containsKey(two_continuous_word)){
					two_continuous_word_tf=bigram_map.get(two_continuous_word);
				}
				
				probility=probility+Math.log((two_continuous_word_tf+0.000001)/dictionary_map.get(words_sequence.get(i)));
			}
			
			else{
				probility=probility+Math.log(0.000001/bigramMatrix.getWordNum());
			}
		}
		return probility;
		
	}
	
	/**
	 * @计算某篇帖子属于哪个类
	 * @param post
	 * @param ten_matrix_map
	 * @return 帖子所属类别
	 */
	public static String getResult(Post post,Map<String,BigramMatrix>ten_matrix_map){
		String result="";
		double probility=-Double.POSITIVE_INFINITY;
		Set<String>txt_names=ten_matrix_map.keySet();
		for(String txt_name:txt_names){
			double temp=getProbility(post,ten_matrix_map.get(txt_name));
			if(temp>probility){
				probility=temp;
				result=txt_name;
			}
		}
		return result;
	}
	
	
	/**
	 * 十折划分训练样本
	 * @param n
	 * @param test_sample
	 * @param train_sample
	 */
	public static void divide(int n,ArrayList<Post>test_sample,Map<String,ArrayList<Post>>train_sample){
		Set<String>txt_names=all_post_list.keySet();
		for(String txt_name:txt_names){
			ArrayList<Post>list=new ArrayList<Post>();
			train_sample.put(txt_name, list);
			ArrayList<Post>post_list=all_post_list.get(txt_name);
			for(int i=0;i<post_list.size();i++){
				if(i>=10*n&&i<(n+1)*10){
					test_sample.add(post_list.get(i));
				}
				
				else{
					train_sample.get(txt_name).add(post_list.get(i));
				}
			}
		}
		
	}
	
	public static void process(){
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int k=0;k<10;k++){
			int count=0;
			ArrayList<Post>test_sample=new ArrayList<Post>();
			Map<String,ArrayList<Post>>train_sample=new HashMap<String,ArrayList<Post>>();
			divide(k,test_sample,train_sample);
			Map<String,BigramMatrix>ten_matrix_map=getTenMatrixMap(train_sample);
			for(int i=0;i<test_sample.size();i++){
				String result=getResult(test_sample.get(i),ten_matrix_map);
				String post_id=test_sample.get(i).get_post_id();
				if(result.equals(post_id)){
					count++;
				}
			}
			System.out.println("第"+(k+1)+"折命中率为:"+count/100.0);
			resultList.add(count/100.0);
		}
		System.out.println("十折验证平均值为:"+Tools.getMean(resultList));
		
	}
	
	public static void main(String[] args){
		getAllpostList();
		process();
	}

}
