package nju.iip.ngram;

import java.util.ArrayList;


/**
 * @description 帖子类
 * @author wangqiang
 * @since 2014-11-7
 */
public class Post {
	/**
	 * 帖子对应的词序列
	 */
	private ArrayList<String>words_sequence;
	
	/**
	 * 帖子所属类别
	 */
	private String post_id;
	
	
	
	
	/**
	 * @description 构造函数
	 * @param id
	 * @param word
	 */
	public Post(String id,ArrayList<String> word){
		this.post_id=id;
		this.words_sequence=word;
	}
	
	/**
	 * @decription 返回帖子词序列
	 * @return
	 */
	public ArrayList<String>get_words_sequence(){
		return this.words_sequence;
	}
	
	
	/**
	 * @decription 返回帖子所属类别
	 * @return
	 */
	public String get_post_id(){
		return this.post_id;
	}
	
	
	

}
