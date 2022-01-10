package com.example.community;

import com.alibaba.fastjson.JSONObject;
import com.example.community.domain.DiscussPost;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.findDiscussPostById(241+""));
    }
    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.getMyPost(101));
        discussPostRepository.saveAll(discussPostMapper.getMyPost(102));
        discussPostRepository.saveAll(discussPostMapper.getMyPost(103));
        discussPostRepository.saveAll(discussPostMapper.getMyPost(111));
        discussPostRepository.saveAll(discussPostMapper.getMyPost(112));
        discussPostRepository.saveAll(discussPostMapper.getMyPost(131));
    }
    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.findDiscussPostById(231 + "");
        post.setContent("你好，明天更好");
        discussPostRepository.save(post);
    }
    @Test
    public void testDelete(){
        discussPostRepository.deleteById(231);
    }
    @Test
    public void testSearchRepository(){
        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"))
                .highlighter(new HighlightBuilder().field("content").preTags("<em>").postTags("</em>"))
                .from(0)
                .size(10);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ArrayList<DiscussPost> list = new ArrayList<>();
            for(SearchHit searchHit:searchResponse.getHits().getHits()){
                DiscussPost discussPost = JSONObject.parseObject(searchHit.getSourceAsString(), DiscussPost.class);
                HighlightField title = searchHit.getHighlightFields().get("title");
                if(title!=null){
                    discussPost.setTitle(title.getFragments()[0].toString());
                }
                HighlightField content = searchHit.getHighlightFields().get("content");
                if(content!=null){
                    discussPost.setTitle(content.getFragments()[0].toString());
                }
                list.add(discussPost);
            }
            list.forEach(i-> System.out.println(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
