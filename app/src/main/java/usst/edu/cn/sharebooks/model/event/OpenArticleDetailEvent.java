package usst.edu.cn.sharebooks.model.event;


public class OpenArticleDetailEvent {
    private String article_id;

    public String getArticle_id() {
        return article_id;
    }

    public OpenArticleDetailEvent setArticle_id(String article_id) {
        this.article_id = article_id;
        return this;
    }
}
