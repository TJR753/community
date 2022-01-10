package com.example.community.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.example.community.domain.DiscussPost;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import com.example.community.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPost(DiscussPost discussPost) {
        discussPostRepository.delete(discussPost);
    }

    @Override
    public List<DiscussPost> searchDiscussPost(String keywords, int current, int limit,String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keywords, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"))
                .highlighter(new HighlightBuilder().field("content").preTags("<em>").postTags("</em>"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<DiscussPost> posts = new ArrayList<>();
        for(SearchHit hit:hits){
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            HighlightField title = hit.getHighlightFields().get("title");
            if(title!=null){
                discussPost.setTitle(title.getFragments()[0].toString());
            }
            HighlightField content = hit.getHighlightFields().get("content");
            if(content!=null){
                discussPost.setTitle(content.getFragments()[0].toString());
            }
            posts.add(discussPost);
        }
        return  posts;
    }
}
