package test.layer.service;

import com.seu.kse.bean.Author;
import com.seu.kse.bean.Paper;
import com.seu.kse.service.impl.AuthorService;
import com.seu.kse.service.impl.PaperService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-mybatis.xml","classpath*:/spring-mvc.xml"})
public class PaperServiceTest {

    @Resource
    private PaperService paperService;
    @Resource
    private AuthorService authorService;
    @Test
    public void testSelectPaperByID() {
        Paper paper = paperService.searchPaper("http://seu/kse/owl/paper/1504.01807");
        System.out.println(paper.getPaperAbstract());
        assert paper!=null;
    }

    @Test
    public void testSelectAuthorByPaperID() {
        Paper paper = paperService.searchPaper("http://seu/kse/owl/paper/1504.01807");
        List<Author> authorsOfpaper = authorService.getAuthorsByPaper(paper.getId());
        if(authorsOfpaper==null){
            authorsOfpaper = new ArrayList<Author>();
        }
        if(authorsOfpaper.size()<=0){
            Author author = new Author();
            author.setAuthorname("未知");
            authorsOfpaper.add(author);
        }
    }
}
