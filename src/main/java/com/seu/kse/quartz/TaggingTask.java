package com.seu.kse.quartz;

import com.seu.kse.bean.PaperTagKey;
import com.seu.kse.dao.PaperTagMapper;
import com.seu.kse.dao.TagMapper;
import com.seu.kse.dao.UserTagMapper;
import com.seu.kse.util.Constant;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新标签
 * 为论文打标签
 */
@Service
public class TaggingTask {

    private final TagMapper tagDao;
    private final UserTagMapper userTagDao;
    private final PaperTagMapper paperTagDao;

    @Autowired
    public TaggingTask(TagMapper tagDao, UserTagMapper userTagDao, PaperTagMapper paperTagDao){
        this.tagDao = tagDao;
        this.userTagDao = userTagDao;
        this.paperTagDao = paperTagDao;
    }

    public void execute(){

    }

    public SearchHits search(String terms){
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY).
                    addTransportAddress(new TransportAddress(InetAddress.getByName("120.78.165.80"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(terms, Constant.ES_SEARCH_FIELDS);

        SearchResponse search_response = client.prepareSearch(Constant.ES_INDEX)
                .setTypes(Constant.ES_TYPE)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(multiMatchQueryBuilder)
                .setExplain(true).setMinScore(0.5f).setSize(1000).get();

        search_response.getTotalShards();

        SearchHits hits = search_response.getHits();
        return hits;
    }


    public void taggingPaper(String tag){

        SearchHits hits = search(tag);

        for(SearchHit hit : hits){
            Map<String,Object> res = hit.getSourceAsMap();
            String pid = (String) res.get(Constant.ES_FIELD_ID);
            // 更新论文-标签库
            PaperTagKey paperTagKey = new PaperTagKey();
            paperTagKey.setPid(pid);
            paperTagKey.setTagname(tag);
            paperTagDao.insert(paperTagKey);
        }
    }

    public void taggingUser(){

    }


    public boolean isUpdatedOfPaper(){
        return true;
    }

    public boolean isUpdatedOfTag(){
        return true;
    }

    //更新标签
    public void updateTag(){
        //1 . 检查OWL表，有无更新，将其更新到 tag库
        //2 . 检查当天的日志信息，统计日志中用户搜索的单词，加入标签库

        //标签更新
        if(isUpdatedOfTag()){

        }

        //论文更新
        if(isUpdatedOfPaper()){

        }

    }

    //统计日志
    public Map<String,String> countKeyWordsOfLog(){
        Map<String,String> userKeyWords = new HashMap<String, String>();
        return userKeyWords;
    }

    //

}
