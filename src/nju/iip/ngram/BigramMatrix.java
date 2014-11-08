package nju.iip.ngram;

import java.util.HashMap;
import java.util.Map;

/**
 * @description 某类对应bigram Map<String,int>和词典(Map<String,Word>)集合类
 * @author wangqiang
 * @since 2014-11-7
 *
 */
public class BigramMatrix {
	
	private HashMap<String,Integer>bigram_map;
	
	private Map<String,Integer>dictionary_map;//词典
	
	private int words_num;//该类的单词总数
	
	
	public void setBigramMap(HashMap<String,Integer>bigram_map){
		this.bigram_map=bigram_map;
	}
	
	public HashMap<String,Integer>getBigramMap(){
		return this.bigram_map;
	}
	
	public void setWordsNum(int num){
		this.words_num=num;
	}
	
	public int getWordNum(){
		return this.words_num;
	}
	
	
	public BigramMatrix(HashMap<String,Integer>bigram_map,Map<String,Integer>dictionary_map){
		this.bigram_map=bigram_map;
		this.dictionary_map=dictionary_map;
	}
	
	public Map<String,Integer>getDictionary_map(){
		return this.dictionary_map;
	}

}
