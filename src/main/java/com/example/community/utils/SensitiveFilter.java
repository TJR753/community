package com.example.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;

@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static final String REPLACEMENT="***";
    //根节点
    private TrieNode root=new TrieNode();
    //构造前缀树
    @PostConstruct
    public void init(){
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            String b=null;
            while((b=br.readLine())!=null){
                addKeyWord(b);
            }
        }catch (IOException e){
            logger.error("文件加载错误"+e.getMessage());
        }
    }
    private void addKeyWord(String b) {
        TrieNode temp=root;
        for (int i = 0; i < b.length(); i++) {
            char c=b.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if(subNode==null){
                subNode=new TrieNode();
                temp.addSubNode(c,subNode);
            }
            temp=subNode;
            if(i==b.length()-1){
                temp.setKeyWordEnd(true);
            }
        }
    }

    /**
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // 跳过符号
                if (isSymbol(c)) {
                    if (tempNode == root) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = root;
                }
                // 发现敏感词
                else if (tempNode.isKeyWordEnd()) {
                    sb.append(REPLACEMENT);
                    begin = ++position;
                }
                // 检查下一个字符
                else {
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9fff);
    }


    private class TrieNode{
        //标注是否为叶子节点
        private boolean keyWordEnd=false;
        //
        private HashMap<Character,TrieNode> subNode=new HashMap<>();

        public boolean isKeyWordEnd() {
            return keyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            this.keyWordEnd = keyWordEnd;
        }

        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }

        public void addSubNode(Character c,TrieNode subNode) {
            this.subNode.put(c,subNode);
        }
    }
}
