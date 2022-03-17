package ntu.n1043826.scanit.Model;

public class PdfModel {
    public String id;
    public String title;
    public String category;
    public String date;
    public String imgurl;
    public PdfModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public PdfModel(String id, String title, String category, String date, String imgurl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.imgurl = imgurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }


}
